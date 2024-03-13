package com.readutf.mcmatchmaker.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import com.readutf.mcmatchmaker.platform.PlatformWrapper;
import com.readutf.mcmatchmaker.queue.QueueManager;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class QueueCommand extends BaseCommand {

    private final QueueManager queueManager;
    private final PlatformWrapper platformWrapper;

    @CommandAlias("queue")
    public void onQueueCommand(CommandIssuer commandSource, String queueId) {

        queueManager.addToQueue(queueId, commandSource.getUniqueId());
    }

}
