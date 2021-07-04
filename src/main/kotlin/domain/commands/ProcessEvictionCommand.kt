package domain.commands

import java.time.ZonedDateTime

data class ProcessEvictionCommand(
    val from: ZonedDateTime,
    val to: ZonedDateTime,
) {
    init {
        require(to.isAfter(from))
    }
}
