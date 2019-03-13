package nbness.util.array

/**
 * Partitions the Array in to 2 parts decided by [predicate] and joins them together.
 */
inline fun <T> Array<T>.partitionJoin(joinLeft: Boolean=true, predicate: (T) -> Boolean): List<T> {
    val results = partition(predicate)
    return if (joinLeft) results.first + results.second
    else results.second + results.first
}
