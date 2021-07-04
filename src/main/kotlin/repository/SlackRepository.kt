package repository

import com.slack.api.Slack
import com.slack.api.model.ConversationType
import domain.interfaces.EvictionRepository
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

        val messages = slackMethods.conversationsHistory { req ->
            req.channel(channelId)
                .inclusive(true)
                .latest(to.toEpochSecond().toString())
                .oldest(from.toEpochSecond().toString())
                .limit(1000)
        }

        return messages.messages.filter {
            it.subtype.isNullOrBlank()
        }.map {
            ChannelMessage(
                "N/A",
                it.user,
                ZonedDateTime.ofInstant(
                    Instant.ofEpochSecond(
                        it.ts.replaceAfter(".", "")
                            .replace(".", "").toLong()
                    ), ZoneId.systemDefault()
                )
            )
        }
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