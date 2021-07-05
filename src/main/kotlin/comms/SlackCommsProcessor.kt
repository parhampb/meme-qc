package comms

import domain.interfaces.CommsProcessor
import domain.pojo.ChannelMember
import com.slack.api.Slack
import com.slack.api.model.block.*
import com.slack.api.model.block.composition.MarkdownTextObject
import com.slack.api.model.block.composition.PlainTextObject
import com.slack.api.model.block.composition.TextObject
import domain.pojo.Statistic

class SlackCommsProcessor(
    botToken: String,
    private val slackDomain: String,
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
                        *Eviction Notice*
                        Attention attention, the following members have $daysLeft day${if (daysLeft > 1) "s" else ""} to post a juicy meme or surrender their rights to use this channel

                        *You have been warned $membersString*
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
                        *Eviction Update*
                        Some members have been evicted since they didn't post any memes in the week...

                        Feels bad man
                    """.trimIndent())
            }
        }
    }

    override fun publishStatistics(channelId: String, stats: List<Statistic>) {

        if (stats.isNotEmpty()) {

            val blocks = mutableListOf<LayoutBlock>(
                HeaderBlock.builder().text(PlainTextObject("Weekly Stats Incoming :smiling_imp:", true)).build(),
            )

            stats.forEach {
                blocks.add(DividerBlock())
                blocks.add(SectionBlock
                    .builder()
                    .text(MarkdownTextObject("""
                        *<@${it.userId}>*
                        Highest Reacts Msg: ${it.highestReaction.second} ${if (it.highestReaction.first.isNotBlank()) "<${slackDomain}/archives/${channelId}/${it.highestReaction.first}|Reference>" else ""}
                        Highest Replies Msg: ${it.highestRepliesTotal.second} ${if (it.highestRepliesTotal.first.isNotBlank()) "<${slackDomain}/archives/${channelId}/${it.highestRepliesTotal.first}|Reference>" else ""}
                        Highest Replies Unique Msg: ${it.highestRepliesUnique.second} ${if (it.highestRepliesUnique.first.isNotBlank()) "<${slackDomain}/archives/${channelId}/${it.highestRepliesUnique.first}|Reference>" else ""}
                    """.trimIndent(), false))
                    .build()
                )
            }

            slackMethods.chatPostMessage {
                it.channel(channelId)
                    .blocks(blocks)
            }
        }
    }
}