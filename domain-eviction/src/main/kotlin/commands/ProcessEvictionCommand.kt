package com.siliconatom.commands

import java.time.ZonedDateTime

data class ProcessEvictionCommand(
    val groupId: String,
    val from: ZonedDateTime,
    val to: ZonedDateTime,
) {
    init {
        require(groupId.isNotBlank())
        require(to.isAfter(from))
    }
}
