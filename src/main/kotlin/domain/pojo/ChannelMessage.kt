package domain.pojo

import java.time.ZonedDateTime

const val CHANNEL_MESSAGE_INVALID_ID = "invalid_message"

data class ChannelMessage(
    val id: String,
    val fromBot: Boolean,
    val fromUserId: String,
    val sentAt: ZonedDateTime,
    val reactionsCount: UInt,
    val repliesCount: UInt,
    val uniqueRepliesCount: UInt,
) {
    init {
        require(id.isNotBlank())
        require(fromUserId.isNotBlank())
    }
}