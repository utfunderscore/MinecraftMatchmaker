package com.readutf.mcmatchmaker.server;

import com.readutf.matchmaker.server.ServerChangeHandler;
import com.readutf.matchmaker.shared.server.ServerUpdate;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ServerChangeListener implements ServerChangeHandler {

    private final ServerManager serverManager;

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
}
