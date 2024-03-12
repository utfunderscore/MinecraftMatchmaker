package com.readutf.mcmatchmaker.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import com.readutf.matchmaker.shared.server.Server;
import com.readutf.mcmatchmaker.server.ServerManager;
import com.readutf.mcmatchmaker.utils.ColorUtils;
import com.velocitypowered.api.proxy.Player;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class ServersCommand extends BaseCommand {

    private final ServerManager serverManager;

    @CommandAlias( "servers" )
    public void listServers(Player commandIssuer) {


        TextComponent text = ColorUtils.color("&7&lServers: &7");
        for (Map.Entry<UUID, Server> entry : serverManager.getLocalServers().entrySet()) {
            UUID uuid = entry.getKey();
            Server server = entry.getValue();
            TextComponent serverComponent = Component.text(server.getId().toString().substring(0, 8)).hoverEvent(HoverEvent.showText(Component.text("""
                    Active Games: %s
                    Load: %s
                    State: %s
                    """.formatted(server.getActiveGames(), server.getLoadPercentage(), server.isUnreachable() ? "Unreachable" : "Online"))));

            text = text.append(serverComponent.color(NamedTextColor.GREEN)).append(Component.text(", ").color(NamedTextColor.GRAY));
        }

        commandIssuer.sendMessage(text);
    }


}
