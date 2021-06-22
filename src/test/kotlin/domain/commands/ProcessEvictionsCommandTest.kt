package domain.commands

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import java.time.*


class ProcessEvictionsCommandTest: ShouldSpec({
    should("have invalid constructors") {
        val startDate = ZonedDateTime.now()
        val endDateValid = startDate.plusSeconds(1)
        val endDateInvalid = startDate.minusSeconds(1)

        shouldThrow<IllegalArgumentException> { ProcessEvictionCommand("", startDate, endDateValid) }
        shouldThrow<IllegalArgumentException> { ProcessEvictionCommand(" ", startDate, endDateValid) }
        shouldThrow<IllegalArgumentException> { ProcessEvictionCommand("test", startDate, endDateInvalid) }
    }
})