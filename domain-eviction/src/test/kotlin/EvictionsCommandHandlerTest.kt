package com.siliconatom

import com.siliconatom.commands.ProcessEvictionCommand
import com.siliconatom.memory.ConsoleCommsProcessor
import com.siliconatom.memory.InMemoryEvictionRepository
import com.siliconatom.pojo.ChannelMember
import com.siliconatom.pojo.ChannelMessage
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContainAll
import java.time.ZonedDateTime

class EvictionsCommandHandlerTest: ShouldSpec ({
    should("return only users not within message period") {
        val repo = InMemoryEvictionRepository()
        val comms = ConsoleCommsProcessor()
        val handler = EvictionsCommandHandler(repo, comms)

        val now = ZonedDateTime.now()

        repo.addChannelMessage("chnl1", ChannelMessage("test1", "userA", now.minusSeconds(10)))
        repo.addChannelMessage("chnl1", ChannelMessage("test2", "userB", now.minusSeconds(9)))
        repo.addChannelMessage("chnl1", ChannelMessage("test3", "userC", now))
        repo.addChannelMessage("chnl1", ChannelMessage("test4", "userA", now.plusSeconds(1)))
        repo.addChannelMessage("chnl1", ChannelMessage("test5", "userC", now.plusSeconds(2)))
        repo.addChannelMessage("chnl1", ChannelMessage("test6", "userD", now.plusSeconds(3)))
        repo.addChannelMessage("chnl1", ChannelMessage("test7", "userE", now.plusMinutes(10)))
        repo.addChannelMessage("chnl1", ChannelMessage("test8", "userF", now.minusMinutes(11)))
        repo.addChannelMessage("chnl1", ChannelMessage("test9", "userF", now.minusMinutes(12)))
        repo.addChannelMessage("chnl1", ChannelMessage("test10", "userG", now.minusHours(1)))

        repo.addGroupMember(ChannelMember("userA", "chnl1"))
        repo.addGroupMember(ChannelMember("userB", "chnl1"))
        repo.addGroupMember(ChannelMember("userC", "chnl1"))
        repo.addGroupMember(ChannelMember("userD", "chnl1"))
        repo.addGroupMember(ChannelMember("userE", "chnl1"))
        repo.addGroupMember(ChannelMember("userF", "chnl1"))
        repo.addGroupMember(ChannelMember("userG", "chnl1"))

        val command = ProcessEvictionCommand("chnl1", now, now.plusMinutes(10))
        val evictionList = handler.processEvictions(command)

        val expectedEvictionList = listOf(
            ChannelMember("userB", "chnl1"),
            ChannelMember("userF", "chnl1"),
            ChannelMember("userG", "chnl1"),
        )

        evictionList shouldContainAll expectedEvictionList
    }
})