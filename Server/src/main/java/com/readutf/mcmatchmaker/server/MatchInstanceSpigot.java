package com.readutf.mcmatchmaker.server;

import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.game.team.Team;
import com.readutf.inari.test.InariDemo;
import com.readutf.inari.test.games.GameStarter;
import com.readutf.matchmaker.client.ErosClient;
import com.readutf.matchmaker.shared.match.MatchData;
import com.readutf.matchmaker.shared.match.MatchResponse;
import com.readutf.matchmaker.shared.server.Server;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MatchInstanceSpigot extends JavaPlugin {

    private static final UUID serverId = UUID.randomUUID();

    @Override
    public void onEnable() {

        InariDemo inari = InariDemo.getInstance();

        new ErosClient(() -> {

            Collection<Game> games = inari.getGameManager().getGames();
            return new Server(
                    serverId, Bukkit.getServer().getIp(), Bukkit.getServer().getPort(), "minigame",
                    games.stream().map(game -> new MatchData(game.getGameId(), game.getAllPlayers().size(), "")).toList(),
                    new HashMap<>(), System.currentTimeMillis(), games.size(), 20
            );

        }, matchRequest -> {

            long start = System.currentTimeMillis();

            
            System.out.println("Received match request: " + matchRequest.getRequestId() + " for queue " + matchRequest.getQueueId() + " with teams " + matchRequest.getTeams());

            try {
                String queueId = matchRequest.getQueueId();
                GameStarter starter = inari.getGameStarterManager().getStarter(queueId);

                if (starter == null) {
                    return MatchResponse.failure(matchRequest.getRequestId(), "Invalid queue id.");
                }

                if (matchRequest.getTeams().size() != 2) {
                    return MatchResponse.failure(matchRequest.getRequestId(), "Invalid number of teams.");
                }

                List<Team> teams = List.of(
                        new Team("Team 1", ChatColor.RED, matchRequest.getTeams().get(0)),
                        new Team("Team 2", ChatColor.BLUE, matchRequest.getTeams().get(1))
                );

                Game game = starter.startGame(teams).join();

                System.out.println("Match started in " + (System.currentTimeMillis() - start) + "ms");

                return MatchResponse.success(matchRequest.getRequestId(), game.getGameId());
            } catch (Exception e) {
                return MatchResponse.failure(matchRequest.getRequestId(), e.getMessage());
            }


        });

    }

}