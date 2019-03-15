package nbness.Item

/**
 * @author: nbness2 <nbness1337@gmail.com>
 *
 * Signifies a non empty Item in a [Container]
 * Equivalent to other servers' [Item] (lol)
 */
data class Item(override val itemId: Int, override val itemAmount: Int): BaseItem {
    constructor(itemId: Int): this(itemId, 1)
}