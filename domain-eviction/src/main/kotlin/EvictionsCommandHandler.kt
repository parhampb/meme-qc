package com.siliconatom

import com.siliconatom.commands.ProcessEvictionCommand
import com.siliconatom.commands.ProcessWarningCommand
import com.siliconatom.interfaces.CommsProcessor
import com.siliconatom.interfaces.EvictionRepository
import com.siliconatom.pojo.ChannelMember
import java.time.ZonedDateTime

class EvictionsCommandHandler(
    private val repository: EvictionRepository,
    private val comms: CommsProcessor,
) {

    fun processEvictions(command: ProcessEvictionCommand): List<ChannelMember> {
        val messages = repository.getMessages(command.groupId, command.from, command.to)
        val channelMembers = repository.getGroupMembers(command.groupId)
        val messagesSentUserIds = messages.distinctBy { msg -> msg.fromUserId }.map { it.fromUserId }
        val evictedMembers = channelMembers.filter { !messagesSentUserIds.contains(it.id) }

        evictedMembers.forEach { repository.removeGroupMember(it) }

        comms.sendEvictionMessage(evictedMembers )

        return evictedMembers
    }

    fun processWarnings(command: ProcessWarningCommand): List<ChannelMember> {
        val warningUsers = getIdleUsers(command.groupId, command.from, command.to)
        comms.sendWarningMessage(command.daysNotice, warningUsers)

        return warningUsers
    }

    private fun getIdleUsers(groupId: String, from: ZonedDateTime, to: ZonedDateTime): List<ChannelMember> {
        val messages = repository.getMessages(groupId, from, to)
        val channelMembers = repository.getGroupMembers(groupId)
        val messagesSentUserIds = messages.distinctBy { msg -> msg.fromUserId }.map { it.fromUserId }
        return channelMembers.filter { !messagesSentUserIds.contains(it.id) }
    }
}