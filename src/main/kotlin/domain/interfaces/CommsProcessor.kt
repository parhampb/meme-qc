package domain.interfaces

import domain.pojo.ChannelMember

interface CommsProcessor {
    fun sendWarningMessage(daysLeft: Int, members: List<ChannelMember>)
    fun sendEvictionMessage(members: List<ChannelMember>)
}