package com.github.mammut53.instantwhitelist;

import com.google.common.collect.Lists;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.UserWhiteList;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class Common {

    private static Thread FILE_WATCHER_THREAD;

    public static void init() {}

    public static void onServerStarted(MinecraftServer server) {

        PlayerList playerList = server.getPlayerList();
        UserWhiteList whiteList = playerList.getWhiteList();

        FILE_WATCHER_THREAD = new Thread(() -> {

            WatchService watchService = null;
            try {
                watchService = FileSystems.getDefault().newWatchService();
                whiteList.getFile().getAbsoluteFile().getParentFile().toPath().register(watchService, ENTRY_MODIFY);
            } catch (IOException e) {
                Constants.LOG.warn("Failed to register whitelist watch service: ", e);
            }

            boolean poll = true;
            while (poll) {

                WatchKey key;
                try {
                    assert watchService != null;
                    key = watchService.take();
                } catch (InterruptedException e) {
                    return;
                }

                assert key != null;
                for (WatchEvent<?> watchEvent : key.pollEvents()) {
                    if (!watchEvent.context().toString().equals("whitelist.json")) continue;
                    if (!playerList.isUsingWhitelist()) continue;

                    Constants.LOG.debug("Modification of whitelist.json detected: Reloading whitelist");

                    try {
                        playerList.getWhiteList().load();
                    } catch (NullPointerException e) {
                        Constants.LOG.debug("Failed to automatically load whitelist: ", e);
                        continue;
                    } catch (Exception e) {
                        Constants.LOG.warn("Failed to automatically load whitelist: ", e);
                        continue;
                    }

                    if (!server.isEnforceWhitelist()) continue;

                    Constants.LOG.debug("Whitelist is enforced: Kicking trespassing players");

                    for (ServerPlayer serverPlayer : Lists.newArrayList(playerList.getPlayers())) {
                        if (whiteList.isWhiteListed(serverPlayer.getGameProfile())) continue;

                        serverPlayer.connection.disconnect(new TranslatableComponent("multiplayer.disconnect.not_whitelisted"));
                    }
                }

                poll = key.reset();
            }
        });

        FILE_WATCHER_THREAD.start();
    }

    public static void onServerStopping(MinecraftServer server) {
        FILE_WATCHER_THREAD.interrupt();
    }
}