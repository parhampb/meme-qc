package com.siliconatom.pojo

data class ChannelMember(
    val id: String,
    val channelId: String,
) {
    init {
        require(id.isNotBlank())
        require(channelId.isNotBlank())
    }
}
