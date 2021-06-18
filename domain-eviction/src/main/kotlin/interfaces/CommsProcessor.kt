package com.siliconatom.interfaces

import com.siliconatom.pojo.ChannelMember

interface CommsProcessor {
    fun sendWarningMessage(daysLeft: Int, members: List<ChannelMember>)
    fun sendEvictionMessage(members: List<ChannelMember>)
}