package domain.pojo

data class Channel(
    val id: String,
    val members: List<ChannelMember>,
    val messages: List<ChannelMessage>,
) {
    init {
        require(id.isNotBlank())
    }
}
