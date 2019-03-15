package nbness.Container

import nbness.Item.BaseItem
import nbness.Item.INVALID_ITEM
import nbness.Item.Item
import kotlin.math.min

/**
 * @author: nbness2 <nbness1337@gmail.com>
 *
 * The "unsafe" way to use [Container]
 */
class UnsafeAccess(
    private val parent: Container,
    private val internalItems: Array<BaseItem>
) {

    private fun Int.orOnInvalid(other: Int): Int = if (this < 0) other else this

    val firstFreeSlot: Int
        get() = internalItems.indexOfFirst { it.isInvalidItem }

    operator fun get(slotIndex: Int): BaseItem = internalItems[slotIndex]
    operator fun set(slotIndex: Int, itemToSet: BaseItem) { internalItems[slotIndex] = itemToSet }

    fun addItem(itemToAdd: BaseItem, slotIndex: Int): BaseItem {
        val targetSlot =
            if (parent.alwaysStackable) {
                findSlotForId(itemToAdd.itemId).orOnInvalid(firstFreeSlot)
            } else {
                slotIndex
            }

        val leftoverItemAmount: Int
        if (parent.alwaysStackable || itemToAdd.isStackable) {
            val containedItem: BaseItem = internalItems[targetSlot]
            val maxAddAmount: Int = Int.MAX_VALUE - containedItem.itemAmount
            val newItemAmount = containedItem.itemAmount + itemToAdd.itemAmount.coerceAtMost(maxAddAmount)
            leftoverItemAmount = -((maxAddAmount - itemToAdd.itemAmount).coerceAtMost(0))
            internalItems[targetSlot] = Item(itemToAdd.itemId, newItemAmount)
        } else {
            val amountToAdd: Int = min(parent.freeSlotAmount, itemToAdd.itemAmount)
            leftoverItemAmount = -((amountToAdd - itemToAdd.itemAmount).coerceAtMost(0))
            repeat(amountToAdd) { internalItems[firstFreeSlot] = Item(itemToAdd.itemId, 1) }
        }

        return if (leftoverItemAmount > 0) Item(itemToAdd.itemId, leftoverItemAmount)
        else INVALID_ITEM
    }

    fun swapSlotContents(fromSlot: Int, toSlot: Int) {
        val temp: BaseItem = internalItems[fromSlot]
        internalItems[fromSlot] = internalItems[toSlot]
        internalItems[toSlot] = temp
    }

    fun takeFromSlot(slotIndex: Int, itemToTake: BaseItem): BaseItem {
        val takenItem: BaseItem = internalItems[slotIndex] - itemToTake
        internalItems[slotIndex] -= itemToTake
        return takenItem
    }

    fun takeFromSlot(slotIndex: Int): BaseItem {
        val takenItem: BaseItem = internalItems[slotIndex]
        internalItems[slotIndex] = INVALID_ITEM
        return takenItem
    }

    fun findSlotForAtLeast(itemToVerify: BaseItem): Int = internalItems
        .indexOfFirst { it.sharesItemIdWith(itemToVerify) && it.hasAtLeast(itemToVerify.itemAmount) }

    fun findSlotForId(idToVerify: Int): Int = findSlotForAtLeast(Item(idToVerify, 1))
}