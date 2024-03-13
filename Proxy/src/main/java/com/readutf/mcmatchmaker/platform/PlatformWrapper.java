package com.readutf.mcmatchmaker.platform;

import co.aikar.commands.CommandIssuer;
import com.readutf.matchmaker.shared.server.Server;
import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.TextComponent;

import java.util.UUID;

public interface PlatformWrapper {

    void registerServer(Server server) throws Exception;

    void unregisterServer(UUID serverId) throws Exception;
    void messagePlayer(CommandIssuer issuer, TextComponent message);
    void messagePlayer(UUID playerId, TextComponent message);

    boolean isPlayerOnline(UUID playerId);

    void sendToServer(UUID player, UUID gameServerId);
}
