package interfaces

import pojo.ChannelMember

interface CommsProcessor {
    fun sendWarningMessage(daysLeft: Int, members: List<ChannelMember>)
}