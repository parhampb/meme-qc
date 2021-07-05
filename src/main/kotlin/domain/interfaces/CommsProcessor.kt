package domain.interfaces

import domain.pojo.ChannelMember
import domain.pojo.Statistic

interface CommsProcessor {
    fun sendWarningMessage(daysLeft: Int, members: List<ChannelMember>)
    fun sendEvictionMessage(members: List<ChannelMember>)
    fun publishStatistics(channelId: String, stats: List<Statistic>)
}