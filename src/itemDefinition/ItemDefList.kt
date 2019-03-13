package nbness.Item.itemDefinition

/**
 * @author: nbness2 <nbness1337@gmail.com>
 *
 * A [List] of [ItemDefinition].
 * I made this SPECIFICALLY to override the get operators and return an [INVALID_DEF] if the index was invalid.
 */
class ItemDefList(size: Int, listInit: (Int) -> ItemDefinition): List<ItemDefinition> {
    constructor(other: List<ItemDefinition>): this(other.size, {other[it]})
    private val internalList: List<ItemDefinition> = List(size, listInit)
    override val size: Int = internalList.size
    override fun subList(fromIndex: Int, toIndex: Int): List<ItemDefinition> = internalList.subList(fromIndex, toIndex)
    override fun listIterator(index: Int): ListIterator<ItemDefinition> = internalList.listIterator(index)
    override fun listIterator(): ListIterator<ItemDefinition> = internalList.listIterator()
    override fun lastIndexOf(element: ItemDefinition): Int = internalList.lastIndexOf(element)
    override fun iterator(): Iterator<ItemDefinition> = internalList.iterator()
    override fun isEmpty(): Boolean = internalList.isEmpty()
    override fun indexOf(element: ItemDefinition): Int = internalList.indexOf(element)
    override fun containsAll(elements: Collection<ItemDefinition>): Boolean = internalList.containsAll(elements)
    override operator fun contains(element: ItemDefinition): Boolean = internalList.contains(element)
    operator fun get(index: Short): ItemDefinition = this.get(index.toInt())
    override operator fun get(index: Int): ItemDefinition = if (index == -1) INVALID_DEF else internalList[index]
}