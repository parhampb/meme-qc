package domain.commands

import java.time.ZonedDateTime

data class ProcessWarningCommand(
    val from: ZonedDateTime,
    val to: ZonedDateTime,
    val daysNotice: Int,
) {
    init {
        require(to.isAfter(from))
        require(daysNotice > 0)
    }
}
