package domain.pojo

data class Statistic(
    val userId: String,
    val highestReaction: Pair<String, UInt>,
    val highestRepliesTotal: Pair<String, UInt>,
    val highestRepliesUnique: Pair<String, UInt>,
)
