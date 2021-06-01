package interfaces

import pojo.ChannelMember
import pojo.ChannelMessage
import java.time.ZonedDateTime

interface EvictionRepository {

    fun getMessages(channelId: String, from: ZonedDateTime, to: ZonedDateTime): List<ChannelMessage>
    fun getGroupMembers(channelId: String): List<ChannelMember>

}