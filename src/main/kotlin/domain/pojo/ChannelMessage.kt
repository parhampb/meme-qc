package domain.pojo

import java.time.ZonedDateTime

data class ChannelMessage(
    val id: String,
    val fromUserId: String,
    val sentAt: ZonedDateTime,
) {
    init {
        require(id.isNotBlank())
        require(fromUserId.isNotBlank())
    }
}