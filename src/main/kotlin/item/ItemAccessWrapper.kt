package item

interface ItemAccessWrapper<T> {
    val getItemId: (T) -> Short
    val getItemAmount: (T) -> Int
    val getIsStackable: (T) -> Boolean
    val createItem: (Short, Int) -> T

    val T.itemId: Short get() = getItemId(this)
    val T.itemAmount: Int get() = getItemAmount(this)
    val T.isStackable: Boolean get() = getIsStackable(this)
    val INVALID_ITEM get() = createItem(-1, -1)

    fun T.hasAtLeast(amount: Int): Boolean = this.itemAmount >= amount
    fun T.isValidItem(): Boolean = getItemId(this) > -1
    fun T.isInvalidItem(): Boolean = !this.isValidItem()
    fun T.sharesItemIdWith(other: T): Boolean = this.itemId == other.itemId

    operator fun T.plus(other: T): T =
        if (sharesItemIdWith(other))
            createItem(itemId, itemAmount + other.itemAmount)
        else
            other

    operator fun T.minus(other: T): T =
        if (sharesItemIdWith(other))
            createItem(itemId, itemAmount - other.itemAmount)
        else
            other
}