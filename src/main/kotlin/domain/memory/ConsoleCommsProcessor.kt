package domain.memory

import domain.interfaces.CommsProcessor
import domain.pojo.ChannelMember
import domain.pojo.Statistic

class ConsoleCommsProcessor: CommsProcessor {

    override fun sendWarningMessage(daysLeft: Int, members: List<ChannelMember>) {
        println("The following members has $daysLeft to send a message before being evicted:")
        members.forEach { println("    ${it.id}") }
    }

    override fun sendEvictionMessage(members: List<ChannelMember>) {
        println("The following members have been evicted:")
        members.forEach { println("    ${it.id}") }
    }

    override fun publishStatistics(channelId: String, stats: List<Statistic>) {
        println("The statistics are as follows for $channelId:")
        stats.forEach {
            println("reactions: ${it.highestReaction}")
            println("replies: ${it.highestRepliesTotal}")
            println("unique replies: ${it.highestRepliesUnique}")
        }
    }
}