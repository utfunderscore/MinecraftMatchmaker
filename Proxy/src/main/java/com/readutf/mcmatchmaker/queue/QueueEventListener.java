package com.readutf.mcmatchmaker.queue;

import com.readutf.matchmaker.queue.QueueEventHandler;
import com.readutf.matchmaker.shared.queue.QueueEvent;
import com.readutf.matchmaker.shared.queue.events.*;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class QueueEventListener implements QueueEventHandler {

    private final QueueManager queueManager;

    @Override
    public void onEvent(QueueEvent queueEvent) {

        System.out.println("queue: " + queueEvent);

        switch (queueEvent) {

            case QueueUpdateEvent queueUpdateEvent:

                queueManager.updateQueue(queueUpdateEvent.getQueue());
                break;

            case QueueDeleteEvent queueDeleteEvent:

                queueManager.deleteQueue(queueDeleteEvent.getQueueName());
                break;

            case QueueErrorEvent queueErrorEvent:

                queueManager.handleError(queueErrorEvent.getQueueId(), queueErrorEvent.getError());
                break;

            case QueueResultEvent queueResultEvent:

                queueManager.handleQueueEvent(queueResultEvent);
                break;

            case QueuePlayerEvent queuePlayerEvent:

                queueManager.handlePlayerEvent(queuePlayerEvent);
                break;

            default:
                break;
        }

    }
}
