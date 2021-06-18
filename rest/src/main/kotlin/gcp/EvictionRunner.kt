package com.siliconatom.gcp

import com.google.cloud.functions.HttpFunction
import com.google.cloud.functions.HttpRequest
import com.google.cloud.functions.HttpResponse
import com.siliconatom.EvictionsCommandHandler
import com.siliconatom.SlackCommsProcessor
import com.siliconatom.SlackRepository
import com.siliconatom.commands.ProcessEvictionCommand
import java.io.IOException
import java.time.*
import kotlin.jvm.Throws

class EvictionRunner: HttpFunction {

    @Throws(IOException::class)
    override fun service(request: HttpRequest, response: HttpResponse) {
        /// The conditions we want to currently hardcode
        ///     1. Make sure the method is POST
        ///     2. Make sure that a "botToken" exists in the requests query
        ///     3. Make sure that a "channelId" exists in the requests query
        ///     4. Make sure the eviction script can only run on a Friday
        if (request.method == "POST") {
            val botToken = request.getFirstQueryParameter("botToken")
            val channelId = request.getFirstQueryParameter("channelId")
            if (botToken.isPresent && channelId.isPresent) {
                val nowInPerth = ZonedDateTime.now(ZoneId.of("Australia/Perth"))
                if (nowInPerth.dayOfWeek == DayOfWeek.FRIDAY) {
                    val slackRepo = SlackRepository(botToken.get())
                    val slackComms = SlackCommsProcessor(botToken.get())
                    val handler = EvictionsCommandHandler(slackRepo, slackComms)

                    val previousSaturday = nowInPerth
                        .minusDays(6)
                        .withHour(0)
                        .withMinute(0)
                        .withSecond(0)
                        .withNano(0)
                    val command = ProcessEvictionCommand(channelId.get(), previousSaturday, nowInPerth)

                    handler.processEvictions(command)
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
        response.writer.write("Invalid inputs sorry")
    }
}