package domain

import domain.commands.ProcessEvictionCommand
import domain.commands.ProcessStatisticsCommand
import domain.commands.ProcessWarningCommand
import domain.interfaces.CommsProcessor
import domain.interfaces.EvictionRepository
import domain.pojo.CHANNEL_MESSAGE_INVALID_ID
import domain.pojo.ChannelMember
import domain.pojo.ChannelMessage
import domain.pojo.Statistic
import java.time.ZonedDateTime

class CommandHandler(
    private val repository: EvictionRepository,
    private val comms: CommsProcessor,
) {

    fun processEvictions(command: ProcessEvictionCommand): List<ChannelMember> {
        val evictedMembers = getIdleUsers(command.from, command.to)
        evictedMembers.forEach { repository.removeGroupMember(it) }
        comms.sendEvictionMessage(evictedMembers)

        return evictedMembers
    }

    fun processWarnings(command: ProcessWarningCommand): List<ChannelMember> {
        val warningUsers = getIdleUsers(command.from, command.to)
        comms.sendWarningMessage(command.daysNotice, warningUsers)

        return warningUsers
    }

    fun processStatistics(command: ProcessStatisticsCommand) {
        repository.getGroupIds().forEach { groupId ->
            val messages = repository.getMessages(groupId, command.from, command.to)
            val groupedMessages = messages.filter { it.id != CHANNEL_MESSAGE_INVALID_ID && !it.fromBot }
                .groupBy { it.fromUserId }
                .values
                .filter { it.isNotEmpty() }

            val groupStatistics = groupedMessages.map { usrMsgs ->
                var highestReaction: Pair<String, UInt> = Pair("", 0u)
                var highestRepliesTotal: Pair<String, UInt> = Pair("", 0u)
                var highestRepliesUnique: Pair<String, UInt> = Pair("", 0u)

                usrMsgs.forEach { msg ->
                    if (msg.reactionsCount > highestReaction.second) {
                        highestReaction = Pair(msg.id, msg.reactionsCount)
                    }
                    if (msg.repliesCount > highestRepliesTotal.second) {
                        highestRepliesTotal = Pair(msg.id, msg.repliesCount)
                    }
                    if (msg.uniqueRepliesCount > highestRepliesUnique.second) {
                        highestRepliesUnique = Pair(msg.id, msg.uniqueRepliesCount)
                    }
                }
                return@map Statistic(usrMsgs.first().fromUserId, highestReaction, highestRepliesTotal, highestRepliesUnique)
            }.sortedByDescending { it.highestReaction.second }

            comms.publishStatistics(groupId, groupStatistics)
        }
    }

    private fun getIdleUsers(from: ZonedDateTime, to: ZonedDateTime): List<ChannelMember> {
        val idleMembers = mutableListOf<ChannelMember>()
        repository.getGroupIds().forEach { groupId ->
            val messages = repository.getMessages(groupId, from, to)
            val channelMembers = repository.getGroupMembers(groupId)
            val messagesSentUserIds = messages.distinctBy { msg -> msg.fromUserId }.map { it.fromUserId }
            idleMembers.addAll(channelMembers.filter { !messagesSentUserIds.contains(it.id) })
        }
        return idleMembers
    }
}