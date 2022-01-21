package com.github.mammut53.instantwhitelist;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkConstants;

@Mod(Constants.MOD_ID)
public class InstantWhitelist {
    
    public InstantWhitelist() {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));

        Common.init();

        MinecraftForge.EVENT_BUS.addListener(this::onServerStarted);
        MinecraftForge.EVENT_BUS.addListener(this::onServerStopping);
    }

    private void onServerStarted(ServerStartedEvent event) {
        if (!event.getServer().isDedicatedServer()) return;

        Common.onServerStarted(event.getServer());
    }

    private void onServerStopping(ServerStoppingEvent event) {
        if (!event.getServer().isDedicatedServer()) return;

        Common.onServerStopping(event.getServer());
    }
}