import commands.ProcessEvictionCommand
import interfaces.EvictionsCommandHandler
import interfaces.EvictionRepository
import pojo.ChannelMember

class EvictionsCommandHandlerImpl(
    private val repository: EvictionRepository,
): EvictionsCommandHandler {

    override fun processEvictions(command: ProcessEvictionCommand): List<ChannelMember> {
        val messages = repository.getMessages(command.groupId, command.from, command.to)
        val channelMembers = repository.getGroupMembers(command.groupId)
        val messagesSentUserIds = messages.distinctBy { msg -> msg.fromUserId }.map { it.fromUserId }
        val evictedMembers = channelMembers.filter { !messagesSentUserIds.contains(it.id) }

        evictedMembers.forEach { repository.removeGroupMember(it) }

        return evictedMembers
    }
}