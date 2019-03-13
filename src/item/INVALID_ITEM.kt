package nbness.Item

/**
 * @author: nbness2 <nbness1337@gmail.com>
 *
 * INVALID_ITEM signifies an empty space in a [Container].
 * Equivalent to other servers' [null]
 */
object INVALID_ITEM: BaseItem {
    override val itemId: Short = -1
    override val itemAmount: Int = 0
    override val isInvalidItem: Boolean = true
    override val isValidItem: Boolean = false
    override fun toString(): String = "INVALID_ITEM"
    override fun copy(): BaseItem = INVALID_ITEM
}