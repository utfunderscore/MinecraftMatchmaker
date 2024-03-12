package com.readutf.mcmatchmaker.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import com.readutf.mcmatchmaker.platform.PlatformWrapper;
import com.readutf.mcmatchmaker.server.ServerManager;
import com.readutf.mcmatchmaker.utils.ColorUtils;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ServerAlertsCommand extends BaseCommand {

    private final PlatformWrapper platformWrapper;
    private final ServerManager serverManager;

    @CommandAlias("serveralerts")
    public void toggleAlerts(CommandIssuer player) {

        if(!player.isPlayer()) {
            player.sendMessage("You must be a player to use this command");
            return;
        }

        if (serverManager.getServerAlerts().remove(player.getUniqueId())) {
            platformWrapper.messagePlayer(player, ColorUtils.color("&cServer alerts have been disabled!"));
        } else {
            serverManager.getServerAlerts().add(player.getUniqueId());
            platformWrapper.messagePlayer(player, ColorUtils.color("&aServer alerts have been enabled!"));
        }

    }

}
