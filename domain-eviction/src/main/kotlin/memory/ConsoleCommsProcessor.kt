package memory

import interfaces.CommsProcessor
import pojo.ChannelMember

class ConsoleCommsProcessor: CommsProcessor {

    override fun sendWarningMessage(daysLeft: Int, members: List<ChannelMember>) {
        println("The following members has $daysLeft to send a message before being evicted:")
        members.forEach { println("    ${it.id}") }
    }
}