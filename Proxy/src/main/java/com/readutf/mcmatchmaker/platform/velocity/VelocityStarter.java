package com.readutf.mcmatchmaker.platform.velocity;

import co.aikar.commands.VelocityCommandManager;
import com.google.inject.Inject;
import com.readutf.mcmatchmaker.ProxyMatchMaker;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;

import java.io.File;
import java.nio.file.Path;

@Plugin(id = "mcmatchmaker", name = "MCMatchMaker", version = "1.0")
public class VelocityStarter {

    private final ProxyServer proxyServer;
    private final File directory;
    private final ProxyMatchMaker proxyMatchMaker;

    @Inject
    public VelocityStarter(ProxyServer proxyServer, @DataDirectory Path directory) {
        this.proxyServer = proxyServer;
        this.directory = directory.toFile();
        this.proxyMatchMaker = new ProxyMatchMaker(new VelocityPlatform(proxyServer));
    }

    @Subscribe
    public void onInit(ProxyInitializeEvent e) {
        proxyMatchMaker.init(new VelocityCommandManager(proxyServer, this));
    }

    @Subscribe
    public void onDisable(ProxyShutdownEvent e) {
        proxyMatchMaker.shutdown();
    }

}
