package nbness.Item

import nbness.Item.itemDefinition.*
/**
 * @author: nbness2 <nbness1337@gmail.com>
 *
 * The Base Item class
 */
interface BaseItem {
    val itemId: Short
    val itemAmount: Int
    val itemDefinition: ItemDefinition
        get() = itemDefinitionList[itemId]

    val isInvalidItem: Boolean
        get() = this is INVALID_ITEM ||itemId < 0 || itemDefinition == INVALID_DEF
    val isValidItem: Boolean
        get() = !isInvalidItem

    val itemName: String
        get() = itemDefinition.itemName
    val itemExamine: String
        get() = itemDefinition.itemExamine

    val isStackable: Boolean
        get() = itemDefinition.isStackable || itemDefinition.isNoted

    val hasOpponote: Boolean
        get() = itemDefinition.opponoteId > -1
    val opponoteId: Short
        get() = itemDefinition.opponoteId

    val highAlchValue: Int
        get() = itemDefinition.highAlchValue
    val lowAlchValue: Int
        get() = itemDefinition.lowAlchValue

    fun copy(): BaseItem
    override fun toString(): String

    operator fun plus(other: BaseItem): BaseItem =
        if (sharesItemIdWith(other)) BaseItem(itemId, itemAmount + other.itemAmount)
        else other

    operator fun minus(other: BaseItem): BaseItem =
        if (sharesItemIdWith(other)) BaseItem(itemId, itemAmount - other.itemAmount)
        else other

    fun sharesItemIdWith(other: BaseItem): Boolean = itemId == other.itemId
    fun hasAtLeast(amount: Int): Boolean = itemAmount >= amount
}

fun BaseItem(itemId: Short, itemAmount: Int): BaseItem = if (itemId < 0) INVALID_ITEM else Item(itemId, itemAmount)
fun Item(): BaseItem = INVALID_ITEM