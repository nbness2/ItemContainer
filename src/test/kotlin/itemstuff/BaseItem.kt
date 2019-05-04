package itemstuff

/**
 * @author: nbness2 <nbness1337@gmail.com>
 *
 * The Base Item class
 */
interface BaseItem {
    /** @property itemId */
    val itemId: Short

    /** @property itemAmount */
    val itemAmount: Int

    /** @property itemDefinition */
    val itemDefinition: ItemDefinition
        get() = itemDefinitionList[itemId.toInt()]


    /** @property isInvalidItem */
    val isInvalidItem: Boolean
        get() = this is INVALID_ITEM ||itemId < 0 || itemDefinition == INVALID_DEF

    /** @property isValidItem */
    val isValidItem: Boolean
        get() = !isInvalidItem


    /** @property isStackable */
    val isStackable: Boolean
        get() = itemDefinition.isStackable

    /** @property itemName */
    val itemName: String
        get() = itemDefinition.itemName


    /**
     * Copies this instance of [BaseItem]
     */
    override fun toString(): String

    /**
     * Adds the amount of this [BaseItem] to [other] if the [itemId] does match.
     * Will return [other] if the [itemId] does not match
     *
     * @param[other] The [BaseItem] to add to this.
     *
     * @return [BaseItem]
     */
    operator fun plus(other: BaseItem): BaseItem =
        if (sharesItemIdWith(other)) BaseItem(itemId, itemAmount + other.itemAmount)
        else other

    /**
     * Subtracts the amount of this [other] from [itemAmount] if the [itemId] does match.
     * Will return [other] if the [itemId] does not match
     *
     * @param[other] The [BaseItem] to subtract from this.
     *
     * @return [BaseItem]
     */
    operator fun minus(other: BaseItem): BaseItem =
        if (sharesItemIdWith(other)) BaseItem(itemId, itemAmount - other.itemAmount)
        else other

    /**
     * Checks if [itemId] matches [other]s [itemId]
     *
     * @param[other] The [BaseItem] that contains the [itemId] that is being compared to this instance of [BaseItem]
     */
    fun sharesItemIdWith(other: BaseItem): Boolean = itemId == other.itemId


    /**
     * Checks if [itemAmount] is greater than or equal to [amount]
     *
     * @param[amount] The amount [itemAmount] is being compared to.
     */
    fun hasAtLeast(amount: Int): Boolean = itemAmount >= amount
}

fun BaseItem(itemId: Short, itemAmount: Int): BaseItem =
    if (itemId < 0)
        INVALID_ITEM
    else
        Item(itemId, itemAmount)