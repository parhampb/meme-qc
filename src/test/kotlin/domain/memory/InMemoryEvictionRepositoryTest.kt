package domain.memory

import domain.pojo.ChannelMember
import domain.pojo.ChannelMessage
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe
import java.time.ZonedDateTime

class InMemoryEvictionRepositoryTest: ShouldSpec({
    should("return an empty groups list") {
        val repo = InMemoryEvictionRepository()

        val groupMembers = repo.getGroupMembers("test")
        groupMembers shouldBe(emptyList())
    }

    should("return members in the correct channel") {
        val repo = InMemoryEvictionRepository()

        repo.addGroupMember(ChannelMember("test1", "chnl1"))
        repo.addGroupMember(ChannelMember("test2", "chnl1"))
        repo.addGroupMember(ChannelMember("test3", "chnl1"))
        repo.addGroupMember(ChannelMember("test1", "chnl2"))
        repo.addGroupMember(ChannelMember("test2", "chnl2"))

        val groupMembers = repo.getGroupMembers("chnl1")
        groupMembers.size shouldBeExactly 3

        /// Putting it in reverse order since the order should not matter
        val expectedGroupMembers = listOf(
            ChannelMember("test3", "chnl1"),
            ChannelMember("test2", "chnl1"),
            ChannelMember("test1", "chnl1"),
        )

        groupMembers shouldContainAll expectedGroupMembers
    }

    should("return an empty message list") {
        val repo = InMemoryEvictionRepository()

        val channelMessages = repo.getMessages("test", ZonedDateTime.now(), ZonedDateTime.now())
        channelMessages shouldBe emptyList()
    }

    should("return messages within the specific channel only") {
        val repo = InMemoryEvictionRepository()

        val now = ZonedDateTime.now()

        repo.addChannelMessage("chnl1", ChannelMessage("test1", "user1", now.plusSeconds(1)))
        repo.addChannelMessage("chnl1", ChannelMessage("test2", "user2", now.plusSeconds(2)))
        repo.addChannelMessage("chnl1", ChannelMessage("test3", "user1", now.plusSeconds(3)))
        repo.addChannelMessage("chnl2", ChannelMessage("test4", "user3", now.plusSeconds(4)))
        repo.addChannelMessage("chnl2", ChannelMessage("test5", "user3", now.plusSeconds(5)))

        val channelMessages = repo.getMessages("chnl2", now, now.plusMinutes(1))

        val expectedChannelMessages = listOf(
            ChannelMessage("test5", "user3", now.plusSeconds(5)),
            ChannelMessage("test4", "user3", now.plusSeconds(4)),
        )

        channelMessages shouldContainAll expectedChannelMessages
    }
})