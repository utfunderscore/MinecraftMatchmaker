package com.readutf.mcmatchmaker.platform.velocity;

import co.aikar.commands.CommandIssuer;
import co.aikar.commands.VelocityCommandIssuer;
import com.readutf.matchmaker.shared.server.Server;
import com.readutf.mcmatchmaker.platform.PlatformWrapper;
import com.readutf.mcmatchmaker.utils.ColorUtils;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.TextComponent;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class VelocityPlatform implements PlatformWrapper {

    private final ProxyServer proxyServer;

    @Override
    public void registerServer(Server server) {

        proxyServer.registerServer(new ServerInfo(
                server.getId().toString(),
                new InetSocketAddress(server.getAddress(), server.getPort())
        ));

    }

    @Override
    public void unregisterServer(UUID serverId) {

        Optional<RegisteredServer> server = proxyServer.getServer(serverId.toString());
        RegisteredServer registeredServer = server.orElseThrow(() -> new IllegalArgumentException("Server not found"));

        proxyServer.unregisterServer(registeredServer.getServerInfo());

    }

    @Override
    public void messagePlayer(CommandIssuer issuer, TextComponent message) {

        if(issuer.isPlayer() && issuer instanceof VelocityCommandIssuer velocityCommandIssuer) {
            velocityCommandIssuer.getPlayer().sendMessage(message);
        }
    }

    @Override
    public void messagePlayer(UUID playerId, TextComponent message) {

           proxyServer.getPlayer(playerId).ifPresent(player -> player.sendMessage(message));
    }

    @Override
    public boolean isPlayerOnline(UUID playerId) {
        return proxyServer.getPlayer(playerId).isPresent();
    }

    @Override
    public void sendToServer(UUID player, UUID gameServerId) {
        proxyServer.getPlayer(player).ifPresent(player1 -> {
            Optional<RegisteredServer> server = proxyServer.getServer(gameServerId.toString());
            server.ifPresent(registeredServer -> player1.createConnectionRequest(registeredServer).connect());
            player1.sendMessage(ColorUtils.color("&aYou are being sent to cluster-" + gameServerId.toString().substring(0, 8)));
        });
    }
}
