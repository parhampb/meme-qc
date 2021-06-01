package interfaces

import commands.ProcessEvictionCommand
import pojo.ChannelMember

interface EvictionsCommandHandler {

    fun processEvictions(command: ProcessEvictionCommand): List<ChannelMember>

}