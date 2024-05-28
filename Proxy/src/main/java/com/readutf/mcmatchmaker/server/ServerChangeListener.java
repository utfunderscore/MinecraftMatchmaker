package com.readutf.mcmatchmaker.server;

import com.readutf.matchmaker.server.ServerChangeHandler;
import com.readutf.matchmaker.shared.server.ServerUpdate;
import com.readutf.mcmatchmaker.ProxyMatchMaker;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

@RequiredArgsConstructor
public class ServerChangeListener implements ServerChangeHandler {

    private static final Logger logger = LoggerFactory.getLogger(ServerChangeListener.class);

    private final ProxyMatchMaker proxyMatchMaker;
    private final ServerManager serverManager;

    public ServerChangeListener(ProxyMatchMaker proxyMatchMaker) {
        this.proxyMatchMaker = proxyMatchMaker;
        this.serverManager = proxyMatchMaker.getServerManager();
    }

    @Override
    public void handleUpdate(ServerUpdate<?> serverUpdate) {

        switch (serverUpdate) {
            case ServerUpdate.ServerAddUpdate serverAddUpdate:
                serverManager.registerServer(serverAddUpdate.getObject());
                break;
            case ServerUpdate.ServerRemoveUpdate serverRemoveUpdate:
                serverManager.unregisterServer(serverRemoveUpdate.getObject());
                break;
            case ServerUpdate.ServerHeartbeatUpdate serverUpdateUpdate:
                serverManager.updateServer(serverUpdateUpdate.getObject());
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + serverUpdate);
        }

    }

    @Override
    public void onOrchestrationClose() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                logger.info("Connection Closed, attempting to reconnect...");

                proxyMatchMaker.sync();
            }
        }, 5000);
    }
}
