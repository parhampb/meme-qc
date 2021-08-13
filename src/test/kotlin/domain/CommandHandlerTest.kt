package domain

import domain.commands.ProcessEvictionCommand
import domain.memory.ConsoleCommsProcessor
import domain.memory.InMemoryEvictionRepository
import domain.pojo.ChannelMember
import domain.pojo.ChannelMessage
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContainAll
import java.time.ZonedDateTime

class CommandHandlerTest: ShouldSpec ({
    should("return only users not within message period") {
        val repo = InMemoryEvictionRepository()
        val comms = ConsoleCommsProcessor()
        val handler = CommandHandler(repo, comms)

        val now = ZonedDateTime.now()

        repo.addChannelMessage("chnl1", ChannelMessage("test1", false, "userA", now.minusSeconds(10), 0u, 0u, 0u))
        repo.addChannelMessage("chnl1", ChannelMessage("test2", false, "userB", now.minusSeconds(9), 0u, 0u, 0u))
        repo.addChannelMessage("chnl1", ChannelMessage("test3", false, "userC", now, 0u, 0u, 0u))
        repo.addChannelMessage("chnl1", ChannelMessage("test4", false, "userA", now.plusSeconds(1), 0u, 0u, 0u))
        repo.addChannelMessage("chnl1", ChannelMessage("test5", false, "userC", now.plusSeconds(2), 0u, 0u, 0u))
        repo.addChannelMessage("chnl1", ChannelMessage("test6", false, "userD", now.plusSeconds(3), 0u, 0u, 0u))
        repo.addChannelMessage("chnl1", ChannelMessage("test7", false, "userE", now.plusMinutes(10), 0u, 0u, 0u))
        repo.addChannelMessage("chnl1", ChannelMessage("test8", false, "userF", now.minusMinutes(11), 0u, 0u, 0u))
        repo.addChannelMessage("chnl1", ChannelMessage("test9", false, "userF", now.minusMinutes(12), 0u, 0u, 0u))
        repo.addChannelMessage("chnl1", ChannelMessage("test10", false, "userG", now.minusHours(1), 0u, 0u, 0u))

        repo.addGroupMember(ChannelMember("userA", "chnl1"))
        repo.addGroupMember(ChannelMember("userB", "chnl1"))
        repo.addGroupMember(ChannelMember("userC", "chnl1"))
        repo.addGroupMember(ChannelMember("userD", "chnl1"))
        repo.addGroupMember(ChannelMember("userE", "chnl1"))
        repo.addGroupMember(ChannelMember("userF", "chnl1"))
        repo.addGroupMember(ChannelMember("userG", "chnl1"))

        val command = ProcessEvictionCommand(now, now.plusMinutes(10))
        val evictionList = handler.processEvictions(command)

        val expectedEvictionList = listOf(
            ChannelMember("userB", "chnl1"),
            ChannelMember("userF", "chnl1"),
            ChannelMember("userG", "chnl1"),
        )

        evictionList shouldContainAll expectedEvictionList
    }
})