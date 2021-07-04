package domain.interfaces

import domain.pojo.ChannelMember
import domain.pojo.ChannelMessage
import java.time.ZonedDateTime

interface EvictionRepository {

    fun getMessages(channelId: String, from: ZonedDateTime, to: ZonedDateTime): List<ChannelMessage>
    fun getGroupIds(): List<String>
    fun getGroupMembers(channelId: String): List<ChannelMember>
    fun removeGroupMember(member: ChannelMember)

}