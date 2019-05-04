package container

import item.ItemAccessWrapper
import kotlin.math.min

/**
 * @author: nbness2 <nbness1337@gmail.com>
 *
 * The "unsafe" way to use [Container]
 */
class UnsafeAccess<T>(
    private val parent: Container<T>,
    private val internalItems: MutableList<T>,
    private val itemAccessWrapper: ItemAccessWrapper<T>
): ItemAccessWrapper<T> by itemAccessWrapper {

    private fun Int.orOnInvalid(other: Int): Int = if (this < 0) other else this

    val firstFreeSlot: Int
        get() = internalItems.indexOfFirst { it.isInvalidItem() }

    operator fun get(slotIndex: Int): T = internalItems[slotIndex]
    operator fun set(slotIndex: Int, itemToSet: T) { internalItems[slotIndex] = itemToSet }

    fun addItem(itemToAdd: T, slotIndex: Int): T {
        val targetSlot =
            if (parent.alwaysStackable) {
                findSlotForId(itemToAdd.itemId).orOnInvalid(firstFreeSlot)
            } else {
                slotIndex
            }

        val leftoverItemAmount: Int
        if (parent.alwaysStackable || itemToAdd.isStackable) {
            val containedItem: T = internalItems[targetSlot]
            val maxAddAmount: Int = Int.MAX_VALUE - containedItem.itemAmount
            val newItemAmount = containedItem.itemAmount + itemToAdd.itemAmount.coerceAtMost(maxAddAmount)
            leftoverItemAmount = -((maxAddAmount - itemToAdd.itemAmount).coerceAtMost(0))
            internalItems[targetSlot] = createItem(itemToAdd.itemId, newItemAmount)
        } else {
            val amountToAdd: Int = min(parent.freeSlotAmount, itemToAdd.itemAmount)
            leftoverItemAmount = -((amountToAdd - itemToAdd.itemAmount).coerceAtMost(0))
            repeat(amountToAdd) { internalItems[firstFreeSlot] = createItem(itemToAdd.itemId, 1) }
        }

        return if (leftoverItemAmount > 0) createItem(itemToAdd.itemId, leftoverItemAmount)
        else INVALID_ITEM
    }

    fun swapSlotContents(fromSlot: Int, toSlot: Int) {
        val temp: T = internalItems[fromSlot]
        internalItems[fromSlot] = internalItems[toSlot]
        internalItems[toSlot] = temp
    }

    fun takeFromSlot(slotIndex: Int, itemToTake: T): T {
        val takenItem: T = internalItems[slotIndex] - itemToTake
        internalItems[slotIndex] -= itemToTake
        return takenItem
    }

    fun takeFromSlot(slotIndex: Int): T {
        val takenItem: T = internalItems[slotIndex]
        internalItems[slotIndex] = INVALID_ITEM
        return takenItem
    }

    fun findSlotForAtLeast(itemToVerify: T): Int = internalItems
        .indexOfFirst { it.sharesItemIdWith(itemToVerify) && it.hasAtLeast(itemToVerify.itemAmount) }

    fun findSlotForId(idToVerify: Short): Int = findSlotForAtLeast(createItem(idToVerify, 1))

    fun findSlotForId(idToVerify: Int): Int = findSlotForAtLeast(createItem(idToVerify.toShort(), 1))
}