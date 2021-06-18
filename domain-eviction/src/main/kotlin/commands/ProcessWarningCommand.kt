package com.siliconatom.commands

import java.time.ZonedDateTime

data class ProcessWarningCommand(
    val groupId: String,
    val from: ZonedDateTime,
    val to: ZonedDateTime,
    val daysNotice: Int,
) {
    init {
        require(groupId.isNotBlank())
        require(to.isAfter(from))
        require(daysNotice > 0)
    }
}
