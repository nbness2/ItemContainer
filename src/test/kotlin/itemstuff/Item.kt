package itemstuff

/**
 * @author: nbness2 <nbness1337@gmail.com>
 *
 * Signifies a non empty Item in a [Container]
 * Equivalent to other servers' [Item] (lol)
 */
data class Item(override val itemId: Short, override val itemAmount: Int): BaseItem {
    constructor(itemId: Short): this(itemId, 1)
}