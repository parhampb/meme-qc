package comms

import domain.interfaces.CommsProcessor
import domain.pojo.ChannelMember
import com.slack.api.Slack

class SlackCommsProcessor(
    botToken: String
): CommsProcessor {

    private val slackMethods = Slack.getInstance().methods(botToken)

    override fun sendWarningMessage(daysLeft: Int, members: List<ChannelMember>) {
        if (members.isNotEmpty()) {
            val channelId = members.first().channelId

            val membersString = members.joinToString(separator = ", ") { "<@${it.id}>" }

            slackMethods.chatPostMessage {
                it.channel(channelId)
                    .mrkdwn(true)
                    .text("""
                        # Eviction Notice
                        Attention attention, the following members have $daysLeft day${if (daysLeft > 1) "s" else ""} to post a juicy meme or surrender their rights to use this channel

                        **You have been warned $membersString**
                    """.trimIndent())
            }
        }
    }

    override fun sendEvictionMessage(members: List<ChannelMember>) {
        if (members.isNotEmpty()) {
            val channelId = members.first().channelId

            slackMethods.chatPostMessage {
                it.channel(channelId)
                    .mrkdwn(true)
                    .text("""
                        # Eviction Update
                        Some members have been evicted since they didn't post any memes in the week...

                        Feels bad man
                    """.trimIndent())
            }
        }
    }
}