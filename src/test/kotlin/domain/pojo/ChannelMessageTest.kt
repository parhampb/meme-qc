package domain.pojo

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import java.time.ZonedDateTime

class ChannelMessageTest: ShouldSpec ({
    should("have invalid constructors") {
        shouldThrow<IllegalArgumentException> { ChannelMessage("", false, "test", ZonedDateTime.now(), 0u, 0u, 0u) }
        shouldThrow<IllegalArgumentException> { ChannelMessage(" ", false, "test", ZonedDateTime.now(), 0u, 0u, 0u) }
        shouldThrow<IllegalArgumentException> { ChannelMessage("test", false, "", ZonedDateTime.now(), 0u, 0u, 0u) }
        shouldThrow<IllegalArgumentException> { ChannelMessage("test", false, " ", ZonedDateTime.now(), 0u, 0u, 0u) }
    }
})