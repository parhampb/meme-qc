package storage

import interfaces.EvictionRepository
import pojo.ChannelMember
import pojo.ChannelMessage
import java.time.ZonedDateTime

class InMemoryEvictionRepository: EvictionRepository {

    private val channelMembers = mutableMapOf<String, MutableList<ChannelMember>>()
    private val channelMessages = mutableMapOf<String, MutableList<ChannelMessage>>()

    override fun getMessages(channelId: String, from: ZonedDateTime, to: ZonedDateTime): List<ChannelMessage> {
        val messages = (channelMessages[channelId] ?: emptyList())
        return messages.filter {
            (it.sentAt.isBefore(to) and it.sentAt.isAfter(from)) or
            (it.sentAt.compareTo(from) == 0) or
            (it.sentAt.compareTo(to) == 0)
        }
    }

    override fun getGroupMembers(channelId: String): List<ChannelMember> {
        return channelMembers[channelId] ?: emptyList()
    }

    fun addGroupMember(member: ChannelMember) {
        val channelMembersList = channelMembers.computeIfAbsent(member.channelId) { mutableListOf() }
        channelMembersList.add(member)
    }

    fun addChannelMessage(channelId: String, message: ChannelMessage) {
        val channelMessagesList = channelMessages.computeIfAbsent(channelId) { mutableListOf() }
        channelMessagesList.add(message)
    }

}