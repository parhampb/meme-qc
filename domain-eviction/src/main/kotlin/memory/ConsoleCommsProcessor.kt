package com.siliconatom.memory

import com.siliconatom.interfaces.CommsProcessor
import com.siliconatom.pojo.ChannelMember

class ConsoleCommsProcessor: CommsProcessor {

    override fun sendWarningMessage(daysLeft: Int, members: List<ChannelMember>) {
        println("The following members has $daysLeft to send a message before being evicted:")
        members.forEach { println("    ${it.id}") }
    }

    override fun sendEvictionMessage(members: List<ChannelMember>) {
        println("The following members have been evicted:")
        members.forEach { println("    ${it.id}") }
    }
}