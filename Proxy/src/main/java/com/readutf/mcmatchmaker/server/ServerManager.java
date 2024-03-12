package com.readutf.mcmatchmaker.server;

import com.readutf.matchmaker.server.ServerService;
import com.readutf.matchmaker.shared.api.ApiResponse;
import com.readutf.matchmaker.shared.server.Server;
import com.readutf.matchmaker.shared.server.ServerHeartbeat;
import com.readutf.mcmatchmaker.platform.PlatformWrapper;
import com.readutf.mcmatchmaker.utils.ColorUtils;
import com.readutf.mcmatchmaker.utils.RetrofitHelper;
import lombok.Getter;
import lombok.SneakyThrows;
import net.kyori.adventure.text.TextComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Retrofit;

import java.awt.*;
import java.util.*;
import java.util.List;

public class ServerManager {

    private static final Logger logger = LoggerFactory.getLogger(ServerManager.class);

    private final ServerService serverService;
    private final PlatformWrapper platformWrapper;
    @Getter private final Map<UUID, Server> localServers;

    private final @Getter Set<UUID> serverAlerts;

    public ServerManager(Retrofit retrofit, PlatformWrapper platformWrapper) {
        this.serverService = ServerService.builder(retrofit);
        this.localServers = new HashMap<>();
        this.platformWrapper = platformWrapper;
        this.serverAlerts = new HashSet<>();

        ApiResponse<List<Server>> serversResponse = RetrofitHelper.getOrDefault(serverService.getServers(), ApiResponse.error("Failed to get servers"));
        if(!serversResponse.isSuccess()) {
            logger.warn("Failed to resolve remote servers.");
            return;

        }

        for (Server server : serversResponse.getData()) {
            registerServer(server);
        }
    }

    public void registerServer(Server server) {
        logger.info("Registering server " + server.getShortId() + " with address " + server.getAddress() + ":" + server.getPort());
        sendServerAlert(ColorUtils.color("&aServer " + server.getShortId() + " has been registered"));

        try {
            platformWrapper.registerServer(server);
        } catch (Exception e) {
            logger.warn("Failed to register server " + server.getShortId(), e);
        }

        localServers.put(server.getId(), server);
    }

    public void unregisterServer(UUID serverId) {
        logger.info("Unregistering server " + serverId);
        Server server = localServers.get(serverId);
        if(server == null) return;

        sendServerAlert(ColorUtils.color("&cServer " + server.getShortId() + " has been unregistered"));

        try {
            platformWrapper.unregisterServer(serverId);
        } catch (Exception e) {
            logger.warn("Failed to unregister server " + serverId, e);
        }

        localServers.remove(serverId);
    }

    public void updateServer(ServerHeartbeat serverHeartbeat) {
        logger.info("Updating server " + serverHeartbeat.getServerId());

        Server server = localServers.get(serverHeartbeat.getServerId());
        if(server == null) return;

        server.handleHeartbeat(serverHeartbeat);
    }

    public void sendServerAlert(TextComponent textComponent) {
        for (UUID serverAlert : serverAlerts) {
            platformWrapper.messagePlayer(serverAlert, ColorUtils.color("&7[&bEros&7] ").append(textComponent));
        }
    }

}
