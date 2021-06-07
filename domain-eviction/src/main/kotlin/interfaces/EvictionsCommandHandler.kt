package interfaces

import commands.ProcessEvictionCommand
import commands.ProcessWarningCommand
import pojo.ChannelMember

interface EvictionsCommandHandler {

    fun processEvictions(command: ProcessEvictionCommand): List<ChannelMember>

    fun processWarnings(command: ProcessWarningCommand): List<ChannelMember>

}