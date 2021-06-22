package gcp

import com.google.cloud.functions.HttpFunction
import com.google.cloud.functions.HttpRequest
import com.google.cloud.functions.HttpResponse
import domain.EvictionsCommandHandler
import comms.SlackCommsProcessor
import repository.SlackRepository
import domain.commands.ProcessWarningCommand
import java.io.IOException
import java.time.DayOfWeek
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.jvm.Throws

class WarningRunner: HttpFunction {

    @Throws(IOException::class)
    override fun service(request: HttpRequest, response: HttpResponse) {
        /// The conditions we want to currently hardcode
        ///     1. Make sure the method is POST
        ///     2. Make sure that a "botToken" exists in the requests query
        ///     3. Make sure that a "channelId" exists in the requests query
        ///     4. Make sure the warning script can only run on wednesdays and thursdays
        if (request.method == "POST") {
            val botToken = request.getFirstQueryParameter("botToken")
            val channelId = request.getFirstQueryParameter("channelId")
            if (botToken.isPresent && channelId.isPresent) {
                val nowInPerth = ZonedDateTime.now(ZoneId.of("Australia/Perth"))
                if (nowInPerth.dayOfWeek == DayOfWeek.WEDNESDAY) {
                    val previousSaturday = nowInPerth
                        .minusDays(4)
                        .withHour(0)
                        .withMinute(0)
                        .withSecond(0)
                        .withNano(0)

                    sendSlackWarning(botToken.get(), channelId.get(), previousSaturday, nowInPerth, 2)
                } else if (nowInPerth.dayOfWeek == DayOfWeek.THURSDAY) {
                    val previousSaturday = nowInPerth
                        .minusDays(5)
                        .withHour(0)
                        .withMinute(0)
                        .withSecond(0)
                        .withNano(0)

                    sendSlackWarning(botToken.get(), channelId.get(), previousSaturday, nowInPerth, 1)
                }
            } else {
                failResponse(response)
            }
        } else {
            failResponse(response)
        }
    }

    private fun failResponse(response: HttpResponse) {
        response.setStatusCode(400)
        response.writer.write("Invalid inputs sorry (warning runner)")
    }

    private fun sendSlackWarning(botToken: String, channelId: String, from: ZonedDateTime, to: ZonedDateTime, days: Int) {
        val slackRepo = SlackRepository(botToken)
        val slackComms = SlackCommsProcessor(botToken)
        val handler = EvictionsCommandHandler(slackRepo, slackComms)
        val command = ProcessWarningCommand(channelId, from, to, days)

        handler.processWarnings(command)
    }
}