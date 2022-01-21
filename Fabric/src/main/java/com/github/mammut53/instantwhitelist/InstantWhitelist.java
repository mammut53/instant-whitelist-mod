package com.github.mammut53.instantwhitelist;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class InstantWhitelist implements ModInitializer {
    
    @Override
    public void onInitialize() {

        Common.init();

        ServerLifecycleEvents.SERVER_STARTED.register(Common::onServerStarted);
        ServerLifecycleEvents.SERVER_STOPPING.register(Common::onServerStopping);
    }
}
