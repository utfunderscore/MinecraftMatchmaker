package com.readutf.mcmatchmaker.queue;

import com.readutf.matchmaker.queue.QueueService;
import com.readutf.matchmaker.shared.api.ApiResponse;
import com.readutf.matchmaker.shared.match.MatchRequest;
import com.readutf.matchmaker.shared.match.MatchResponse;
import com.readutf.matchmaker.shared.queue.Queue;
import com.readutf.matchmaker.shared.queue.QueueEntry;
import com.readutf.matchmaker.shared.queue.events.QueuePlayerEvent;
import com.readutf.matchmaker.shared.queue.events.QueueResultEvent;
import com.readutf.mcmatchmaker.platform.PlatformWrapper;
import com.readutf.mcmatchmaker.server.ServerManager;
import com.readutf.mcmatchmaker.utils.ColorUtils;
import com.readutf.mcmatchmaker.utils.RetrofitHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class QueueManager {

    private static final Logger logger = LoggerFactory.getLogger(QueueManager.class);

    private final ServerManager serverManager;
    private final PlatformWrapper platformWrapper;
    private final QueueService queueService;
    private final Map<String, Queue> queues;

    public QueueManager(ServerManager serverManager, PlatformWrapper platformWrapper, QueueService queueServices) {
        this.platformWrapper = platformWrapper;
        this.queues = new HashMap<>();
        this.queueService = queueServices;
        this.serverManager = serverManager;

        ApiResponse<List<Queue>> remoteQueues = RetrofitHelper.getOrDefault(queueServices.getQueueList(), ApiResponse.error(""));
        if (remoteQueues.isSuccess()) {
            for (Queue queue : remoteQueues.getData()) {
                queues.put(queue.getName(), queue);
            }
        }

    }

    public void addToQueue(String queueId, UUID playerId) {
        ApiResponse<Queue> joinQueue = RetrofitHelper.getOrDefault(queueService.joinQueue(queueId, playerId.toString()),
                ApiResponse.error("&cCould not contact queue server, please try again later"));

        if (!joinQueue.isSuccess()) {
            platformWrapper.messagePlayer(playerId, ColorUtils.color("&cError: " + joinQueue.getMessage()));
        }
    }

    public void removeFromQueue(UUID playerId) {
        ApiResponse<Void> leaveQueue = RetrofitHelper.getOrDefault(queueService.leaveQueue(playerId.toString()),
                ApiResponse.error("&cCould not contact queue server, please try again later"));

        if (leaveQueue.isSuccess()) {
            platformWrapper.messagePlayer(playerId, ColorUtils.color("&aYou have left the queue"));
        } else {
            platformWrapper.messagePlayer(playerId, ColorUtils.color("&cError: " + leaveQueue.getMessage()));
        }
    }

    public void handleQueueEvent(QueueResultEvent queueResultEvent) {

        if (!serverManager.getLocalServers().containsKey(queueResultEvent.getServer().getId())) {
            serverManager.registerServer(queueResultEvent.getServer());
        }

        MatchRequest matchRequest = queueResultEvent.getMatchRequest();
        MatchResponse last = queueResultEvent.getResponses().getLast();
        if(!last.isSuccessful()) {
            handleError(queueResultEvent.getQueueId(), last.getFailureReason());
            return;
        }

        logger.info("Match request: " + matchRequest.getTeams().size() + " teams");

        for (List<UUID> team : matchRequest.getTeams()) {
            for (UUID player : team) {
                if(!platformWrapper.isPlayerOnline(player)) {
                    return;
                }

                platformWrapper.sendToServer(player, queueResultEvent.getServer().getId());

            }
        }

    }

    public void updateQueue(Queue queue) {
        queues.put(queue.getName(), queue);
    }

    public void handleError(String queueId, String error) {
        logger.error("Error in queue: " + queueId + " - " + error);

        Queue queue = queues.get(queueId);
        if (queue == null) return;

        for (QueueEntry queueEntry : queue.getInQueue()) {
            for (UUID player : queueEntry.getPlayers()) {
                platformWrapper.messagePlayer(player, ColorUtils.color("&cError: " + error));
            }
        }

    }

    public void deleteQueue(String queueId) {
        queues.remove(queueId);
    }

    public void handlePlayerEvent(QueuePlayerEvent queuePlayerEvent) {
        Queue queue = queues.get(queuePlayerEvent.getQueue().getName());
        if (queue == null) return;

        if (queuePlayerEvent.isJoining()) {
            for (UUID player : queuePlayerEvent.getPlayerIds()) {
                platformWrapper.messagePlayer(player, ColorUtils.color("&aYou have joined the queue '" + queue.getName() + "'"));
            }
        } else {
            for (UUID player : queuePlayerEvent.getPlayerIds()) {
                platformWrapper.messagePlayer(player, ColorUtils.color("&aYou have left the queue '" + queue.getName() + "'"));
            }
        }
    }
}
