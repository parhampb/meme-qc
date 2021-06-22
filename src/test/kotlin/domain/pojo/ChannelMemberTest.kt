package domain.pojo

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec

class ChannelMemberTest: ShouldSpec ({
    should("have invalid constructors") {
        shouldThrow<IllegalArgumentException> { ChannelMember("", "test") }
        shouldThrow<IllegalArgumentException> { ChannelMember(" ", "test") }
        shouldThrow<IllegalArgumentException> { ChannelMember("test", "") }
        shouldThrow<IllegalArgumentException> { ChannelMember("test", " ") }
    }
})