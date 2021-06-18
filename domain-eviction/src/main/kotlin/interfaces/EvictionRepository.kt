package com.siliconatom.interfaces

import com.siliconatom.pojo.ChannelMember
import com.siliconatom.pojo.ChannelMessage
import java.time.ZonedDateTime

interface EvictionRepository {

    fun getMessages(channelId: String, from: ZonedDateTime, to: ZonedDateTime): List<ChannelMessage>
    fun getGroupMembers(channelId: String): List<ChannelMember>
    fun removeGroupMember(member: ChannelMember)

}