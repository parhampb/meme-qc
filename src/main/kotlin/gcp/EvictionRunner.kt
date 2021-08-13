package gcp

import com.google.cloud.functions.HttpFunction
import com.google.cloud.functions.HttpRequest
import com.google.cloud.functions.HttpResponse
import domain.CommandHandler
import comms.SlackCommsProcessor
import repository.SlackRepository
import domain.commands.ProcessEvictionCommand
import java.io.IOException
import java.time.ZonedDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import kotlin.jvm.Throws

class EvictionRunner: HttpFunction {

    @Throws(IOException::class)
    override fun service(request: HttpRequest, response: HttpResponse) {
        if (request.method == "POST") {
            val nowInPerth = ZonedDateTime.now(ZoneId.of("Australia/Perth"))
            val currentDay = nowInPerth.dayOfWeek.value
            val saturdayInt = 6
            val deltaToLastSaturday = if (currentDay >= saturdayInt) currentDay - saturdayInt else currentDay + 1
            val previousSaturday = nowInPerth.minusDays(deltaToLastSaturday.toLong()).truncatedTo(ChronoUnit.DAYS)

            val botToken = System.getenv(Constants.SLACK_BOT_TOKEN)
            val slackDomain = System.getenv(Constants.SLACK_DOMAIN)

            val slackRepo = SlackRepository(botToken)
            val slackComms = SlackCommsProcessor(botToken, slackDomain)
            val handler = CommandHandler(slackRepo, slackComms)
            val command = ProcessEvictionCommand(previousSaturday, nowInPerth)
            handler.processEvictions(command)
        } else {
            response.setStatusCode(400)
            response.writer.write("Invalid inputs sorry (eviction runner)")
        }
    }
}