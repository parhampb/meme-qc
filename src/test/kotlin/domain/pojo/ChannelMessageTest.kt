package domain.pojo

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import java.time.ZonedDateTime

class ChannelMessageTest: ShouldSpec ({
    should("have invalid constructors") {
        shouldThrow<IllegalArgumentException> { ChannelMessage("", "test", ZonedDateTime.now()) }
        shouldThrow<IllegalArgumentException> { ChannelMessage(" ", "test", ZonedDateTime.now()) }
        shouldThrow<IllegalArgumentException> { ChannelMessage("test", "", ZonedDateTime.now()) }
        shouldThrow<IllegalArgumentException> { ChannelMessage("test", " ", ZonedDateTime.now()) }
    }
})