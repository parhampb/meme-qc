package com.siliconatom.commands

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import java.time.*


class ProcessWarningCommandTest: ShouldSpec({
    should("have invalid constructors") {
        val startDate = ZonedDateTime.now()
        val endDateValid = startDate.plusSeconds(1)
        val endDateInvalid = startDate.minusSeconds(1)

        shouldThrow<IllegalArgumentException> { ProcessWarningCommand("", startDate, endDateValid, 1) }
        shouldThrow<IllegalArgumentException> { ProcessWarningCommand(" ", startDate, endDateValid, 1) }
        shouldThrow<IllegalArgumentException> { ProcessWarningCommand("test", startDate, endDateInvalid, 1) }
        shouldThrow<IllegalArgumentException> { ProcessWarningCommand("test", startDate, endDateValid, 0) }
        shouldThrow<IllegalArgumentException> { ProcessWarningCommand("test", startDate, endDateValid, -1) }
    }
})