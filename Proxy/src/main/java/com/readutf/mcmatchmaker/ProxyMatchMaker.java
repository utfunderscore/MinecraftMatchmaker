package com.readutf.mcmatchmaker;

import co.aikar.commands.CommandManager;
import com.readutf.matchmaker.queue.QueueListener;
import com.readutf.matchmaker.queue.QueueService;
import com.readutf.matchmaker.server.ServerListener;
import com.readutf.mcmatchmaker.commands.QueueCommand;
import com.readutf.mcmatchmaker.commands.ServerAlertsCommand;
import com.readutf.mcmatchmaker.commands.ServersCommand;
import com.readutf.mcmatchmaker.platform.PlatformWrapper;
import com.readutf.mcmatchmaker.queue.QueueEventListener;
import com.readutf.mcmatchmaker.queue.QueueManager;
import com.readutf.mcmatchmaker.server.ServerChangeListener;
import com.readutf.mcmatchmaker.server.ServerManager;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.Arrays;
import java.util.List;

public class ProxyMatchMaker {

    private final Retrofit retrofit;
    private final PlatformWrapper platformWrapper;
    private final ServerManager serverManager;
    private final QueueManager queueManager;
    private final ServerListener serverListener;
    private final QueueListener queueListener;

    public ProxyMatchMaker(PlatformWrapper platformWrapper) {
        this.platformWrapper = platformWrapper;
        this.retrofit = new Retrofit.Builder().baseUrl("http://localhost:8080").addConverterFactory(GsonConverterFactory.create()).build();
        this.serverManager = new ServerManager(retrofit, platformWrapper);
        this.queueManager = new QueueManager(serverManager, platformWrapper, QueueService.builder(retrofit));
        this.serverListener = ServerListener.instance("ws://localhost:8080", List.of("*"), new ServerChangeListener(serverManager));
        this.queueListener = QueueListener.instance("ws://localhost:8080", new QueueEventListener(queueManager));


        serverListener.connect();
        queueListener.connect();
    }

    public void init(CommandManager commandManager) {

        Arrays.asList(
                new ServersCommand(serverManager),
                new ServerAlertsCommand(platformWrapper, serverManager),
                new QueueCommand(queueManager, platformWrapper)
        ).forEach(commandManager::registerCommand);
    }

    public void shutdown() {

    }

}
