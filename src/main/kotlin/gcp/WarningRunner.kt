package gcp

import com.google.cloud.functions.HttpFunction
import com.google.cloud.functions.HttpRequest
import com.google.cloud.functions.HttpResponse
import domain.EvictionsCommandHandler
import comms.SlackCommsProcessor
import repository.SlackRepository
import domain.commands.ProcessWarningCommand
import java.io.IOException
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import kotlin.jvm.Throws

class WarningRunner: HttpFunction {

    @Throws(IOException::class)
    override fun service(request: HttpRequest, response: HttpResponse) {
        if (request.method == "POST") {
            val nowInPerth = ZonedDateTime.now(ZoneId.of("Australia/Perth"))
            val currentDay = nowInPerth.dayOfWeek.value
            val fridayInt = 5
            val fridayNextWeekInt = 12
            val saturdayInt = 6
            val deltaToNextFriday = if (currentDay <= fridayInt) fridayInt - currentDay else fridayNextWeekInt - currentDay
            val deltaToLastSaturday = if (currentDay >= saturdayInt) currentDay - saturdayInt else currentDay + 1
            val previousSaturday = nowInPerth.minusDays(deltaToLastSaturday.toLong()).truncatedTo(ChronoUnit.DAYS)

            sendSlackWarning(previousSaturday, nowInPerth, deltaToNextFriday)
        } else {
            response.setStatusCode(400)
            response.writer.write("Invalid inputs sorry (warning runner)")
        }
    }

    private fun sendSlackWarning(from: ZonedDateTime, to: ZonedDateTime, days: Int) {
        val botToken = System.getenv(Constants.SLACK_BOT_TOKEN)

        val slackRepo = SlackRepository(botToken)
        val slackComms = SlackCommsProcessor(botToken)
        val handler = EvictionsCommandHandler(slackRepo, slackComms)
        val command = ProcessWarningCommand(from, to, days)

        handler.processWarnings(command)
    }
}