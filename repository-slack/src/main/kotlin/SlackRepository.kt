import com.slack.api.Slack
import interfaces.EvictionRepository
import pojo.ChannelMember
import pojo.ChannelMessage
import java.time.*

class SlackRepository(
    private val botToken: String
): EvictionRepository {

    private val slackMethods = Slack.getInstance().methods()

    override fun getMessages(channelId: String, from: ZonedDateTime, to: ZonedDateTime): List<ChannelMessage> {

        val messages = slackMethods.conversationsHistory { req ->
            req.token(botToken)
                .channel(channelId)
                .inclusive(true)
                .latest(to.toEpochSecond().toString())
                .oldest(from.toEpochSecond().toString())
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

    override fun getGroupMembers(channelId: String): List<ChannelMember> {
        val members = slackMethods.conversationsMembers { req ->
            req.token(botToken)
                .channel(channelId)
        }

        return members.members.map {
            ChannelMember(it, channelId)
        }
    }
}