package domain.pojo

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import java.util.*

class ChannelTest: ShouldSpec ({
    should("have invalid constructors") {
        shouldThrow<IllegalArgumentException> { Channel("", listOf(), listOf()) }
        shouldThrow<IllegalArgumentException> { Channel(" ", listOf(), listOf()) }
    }
})