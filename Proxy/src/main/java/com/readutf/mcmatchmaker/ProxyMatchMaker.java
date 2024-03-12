package com.readutf.mcmatchmaker;

import co.aikar.commands.CommandManager;
import co.aikar.commands.VelocityCommandManager;
import com.readutf.matchmaker.queue.QueueListener;
import com.readutf.matchmaker.server.ServerListener;
import com.readutf.mcmatchmaker.commands.ServerAlertsCommand;
import com.readutf.mcmatchmaker.commands.ServersCommand;
import com.readutf.mcmatchmaker.platform.PlatformWrapper;
import com.readutf.mcmatchmaker.server.ServerChangeListener;
import com.readutf.mcmatchmaker.server.ServerManager;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.List;

public class ProxyMatchMaker {

    private final Retrofit retrofit;
    private final PlatformWrapper platformWrapper;
    private final ServerManager serverManager;
    private final ServerListener serverListener;
    private final QueueListener queueListener;

    public ProxyMatchMaker(PlatformWrapper platformWrapper) {
        this.platformWrapper = platformWrapper;
        this.retrofit = new Retrofit.Builder().baseUrl("http://localhost:8080").addConverterFactory(GsonConverterFactory.create()).build();
        this.serverManager = new ServerManager(retrofit, platformWrapper);
        this.serverListener = ServerListener.instance("ws://localhost:8080", List.of("*"), new ServerChangeListener(serverManager));
        this.queueListener = QueueListener.instance("ws://localhost:8080", queueEvent -> {});

        serverListener.connect();
        queueListener.connect();
    }

    public void init(CommandManager commandManager) {
        commandManager.registerCommand(new ServersCommand(serverManager));
        commandManager.registerCommand(new ServerAlertsCommand(platformWrapper, serverManager));
    }

    public void shutdown() {

    }

}
