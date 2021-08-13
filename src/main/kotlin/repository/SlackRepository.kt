package repository

import com.slack.api.Slack
import com.slack.api.model.ConversationType
import domain.interfaces.EvictionRepository
import domain.pojo.CHANNEL_MESSAGE_INVALID_ID
import domain.pojo.ChannelMember
import domain.pojo.ChannelMessage
import java.time.ZonedDateTime
import java.time.Instant
import java.time.ZoneId

class SlackRepository(
    botToken: String,
): EvictionRepository {

    private val slackMethods = Slack.getInstance().methods(botToken)

    override fun getMessages(channelId: String, from: ZonedDateTime, to: ZonedDateTime): List<ChannelMessage> {
        val messages = mutableListOf<ChannelMessage>()
        var cursor: String? = null
        do {
            val res = slackMethods.conversationsHistory { req ->
                req.channel(channelId)
                    .inclusive(true)
                    .latest(to.toEpochSecond().toString())
                    .oldest(from.toEpochSecond().toString())
                    .limit(100)
                    .cursor(cursor.orEmpty())
            }
            messages.addAll(res.messages.filter { it.subtype.isNullOrBlank() }
                .map {
                    ChannelMessage(
                        it.ts ?: CHANNEL_MESSAGE_INVALID_ID,
                        it.botId != null,
                        it.user,
                        ZonedDateTime.ofInstant(
                            Instant.ofEpochSecond(
                                it.ts.replaceAfter(".", "")
                                    .replace(".", "").toLong()
                            ), ZoneId.systemDefault()
                        ),
                        (it.reactions ?: listOf()).map { r -> r.count }.ifEmpty { listOf(0) }.reduce { s, e -> s + e }.toUInt(),
                        (it.replyCount ?: 0).toUInt(),
                        (it.replyUsersCount ?: 0).toUInt(),
                    )
                }
            )
            if (res.isHasMore) {
                cursor = res.responseMetadata.nextCursor
            }
        } while (res.isHasMore)

        return messages
    }

    override fun getGroupIds(): List<String> {
        return slackMethods.conversationsList { req ->
            req.excludeArchived(true)
                .types(listOf(ConversationType.PRIVATE_CHANNEL))
        }.channels.map { it.id }
    }

    override fun getGroupMembers(channelId: String): List<ChannelMember> {
        val members = slackMethods.conversationsMembers { req ->
            req.channel(channelId)
                .limit(1000)
        }

        return members.members.map {
            ChannelMember(it, channelId)
        }
    }

    override fun removeGroupMember(member: ChannelMember) {
        slackMethods.conversationsKick { it.channel(member.channelId).user(member.id) }
    }
}