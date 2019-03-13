package nbness.Container

import nbness.Container.ContainerResult.*
import nbness.util.array.partitionJoin
import nbness.util.boolean.*
import nbness.Item.*

/**
 * @author: nbness2 <nbness1337@gmail.com>
 *
 * Mutable container of Immutable items.
 * Provides both safe and dangerous ways of operating on this.
 */
class Container(
    @JvmField val size: Int,
    @JvmField val alwaysStackable: Boolean = false,
    initList: List<BaseItem> = emptyList())
{
    constructor(alwaysStackable: Boolean, vararg items: BaseItem) : this(alwaysStackable, items.toList())

    @JvmOverloads constructor(alwaysStackable: Boolean = false, initList: List<BaseItem>) :
            this(size = initList.size, alwaysStackable = alwaysStackable, initList = initList)

    fun withAlwaysStackable(newAlwaysStackable: Boolean): Container =
        Container(size, newAlwaysStackable, internalItems.toList())

    private val internalItems: Array<BaseItem> =
        if (initList.isEmpty()) {
            Array(size) { INVALID_ITEM }
        } else {
            if (initList.size != size) {
                throw Exception("initList size [${initList.size}] does not correspond with given size [$size]")
            }
            initList.toTypedArray()
        }

    @JvmField val unsafe: UnsafeAccess = UnsafeAccess(this, internalItems)

    @JvmOverloads fun copy(alwaysStackable: Boolean = false) =
        Container(alwaysStackable, internalItems.toList())

    /**
     * Returns: Success.FindSlot or Failure.NoFreeSlots
     */
    val firstFreeSlot: ContainerResult
        get() {
            val index = internalItems.indexOfFirst { it.isInvalidItem }
            return if (index < 0) Failure.NoFreeSlots
            else Success.FindSlot(index)
        }

    val freeSlotAmount: Int
        get() = internalItems.count { it.isInvalidItem }

    private val indexRange = 0 until size

    private fun Int.orOnInvalid(other: Int): Int = if (this < 0) other else this

    operator fun contains(item: BaseItem): Boolean = internalItems.any { it.sharesItemIdWith(item) }

    /**
     * Returns: Success.GetItem or Failure.BadIndex
     */
    operator fun get(slotIndex: Int): ContainerResult {
        return if (isValidSlot(slotIndex)) Success.GetItem(unsafe[slotIndex])
        else Failure.BadIndex(slotIndex)
    }

    /**
     * Returns: Success.SetItem, Failure.BadIndex or Failure.SlotOccupied
     */
    operator fun set(slotIndex: Int, itemToSet: BaseItem): ContainerResult = this[slotIndex, itemToSet]

    /**
     * Actually a set, but allows you to get a result from it. Assignment set (Container[slotIndex] = BaseItem) doesn't allow you to capture a return value.
     * Usage: val setResult = Container[slotIndex, BaseItem]
     */
    operator fun get(slotIndex: Int, itemToSet: BaseItem): ContainerResult =
        if (isValidSlot(slotIndex)) {
            if (slotIsOccupied(slotIndex)) {
                Failure.SlotOccupied(slotIndex)
            } else {
                internalItems[slotIndex] = itemToSet
                Success.SetItem
            }
        } else {
            Failure.BadIndex(slotIndex)
        }

    /**
     * Returns: Success.VerifyItem, Failure.BadIndex or Failure.ItemIdMismatch
     */
    inline fun verify(itemToVerify: BaseItem, slotIndex: Int, verificationCondition: (comparedItem: BaseItem) -> Boolean): ContainerResult =
        this[slotIndex]
            .onSuccessType<Success.GetItem> {
                return if (verificationCondition(containedItem)) {
                    Success.VerifyItem(itemToVerify)
                } else {
                    Failure.ItemIdMismatch(itemToVerify, containedItem)
                }
            }

    /**
     * Returns: Success.VerifyItem, Failure.BadIndex, Failure.ItemIdMismatch or Failure.NotExactItemAmount
     */
    fun verifyExact(itemToVerify: BaseItem, slotIndex: Int): ContainerResult =
        verify(itemToVerify, slotIndex) {
            if (!it.sharesItemIdWith(itemToVerify)) return@verify false
            if (it.itemAmount != itemToVerify.itemAmount) return Failure.NotExactItemAmount
            true }

    /**
     * Returns: Success.VerifyItem, Failure.BadIndex, Failure.ItemIdMismatch or Failure.NotEnoughItemAmount
     */
    fun verifyAtLeast(itemToVerify: BaseItem, slotIndex: Int): ContainerResult =
        verify(itemToVerify, slotIndex) {
            if (!it.sharesItemIdWith(itemToVerify)) return@verify false
            if (it.itemAmount < itemToVerify.itemAmount) return Failure.NotEnoughItemAmount
            true }

    fun verifyOne(itemToVerify: BaseItem, slotIndex: Int): ContainerResult =
        verify(itemToVerify, slotIndex) { it.sharesItemIdWith(itemToVerify) }

    fun slotIsOccupied(slotIndex: Int): Boolean = internalItems[slotIndex].isValidItem
    fun isValidSlot(slotIndex: Int): Boolean = slotIndex in indexRange
    fun containerIsFull(): Boolean = firstFreeSlot is Failure.NoFreeSlots
    fun containerHasSpace(): Boolean = firstFreeSlot is Success.FindSlot

    fun hasRoomFor(item: BaseItem): Boolean {
        val maxAddAmount: Int = if (alwaysStackable || item.isStackable) {
            if (item in this) {
                val targetSlot = unsafe.findSlotForId(item.itemId)
                Int.MAX_VALUE - unsafe[targetSlot].itemAmount
            } else {
                return freeSlotAmount > 0
            }
        } else {
            freeSlotAmount
        }
        return item.itemAmount <= maxAddAmount
    }

    /**
     * Returns: Success.FindSlot, Failure.ItemNotfound or Failure.NotEnoughItemAmount
     */
    fun findSlotForAtLeast(itemToVerify: BaseItem): ContainerResult {
        val foundSlot = internalItems.indexOfFirst { itemToVerify.sharesItemIdWith(it) }
        return this[foundSlot]
            .onFailure { return Failure.ItemNotFound(itemToVerify) }
            .onSuccessType<Success.GetItem> {
                containedItem
                    .hasAtLeast(itemToVerify.itemAmount)
                    .onFalse { return Failure.NotEnoughItemAmount }
                    .otherwise { Success.FindSlot(foundSlot) } }
    }

    fun findSlotForId(itemIdToFind: Number): ContainerResult = findSlotForAtLeast(Item(itemIdToFind.toShort(), 1))
    fun findSlotForItem(itemToFind: BaseItem): ContainerResult = findSlotForAtLeast(Item(itemToFind.itemId))

    /**
     * Returns: Success.FullTakeItem or Failure.BadIndex
     */
    fun takeFromSlot(slotIndex: Int): ContainerResult =
        this[slotIndex]
            .onSuccessType<Success.GetItem> {
                internalItems[slotIndex] = INVALID_ITEM
                return Success.FullTakeItem(containedItem) }

    /**
     * Returns: Success.FullTakeItem, Success.PartialTakeItem or Failure.BadIndex
     */
    fun takeFromSlot(slotIndex: Int, itemToTake: BaseItem): ContainerResult =
        verifyAtLeast(itemToTake, slotIndex)
            .onSuccessType<Success.VerifyItem> {
                return get(slotIndex)
                    .onSuccessType<Success.GetItem> {
                        internalItems[slotIndex] = containedItem - itemToTake
                        return Success.FullTakeItem(itemToTake) } }
            .onFailureType<Failure.NotEnoughItemAmount> {
                return takeFromSlot(slotIndex)
                    .onSuccessType<Success.FullTakeItem> {
                        return Success.PartialTakeItem(containedItem, itemToTake - containedItem) } }

    /**
     * Returns: Success.SwapItem, Failure.BadFromIndex, Failure.BadToIndex or Failure.SameToFromIndex
     */
    fun swapSlotContents(fromSlot: Int, toSlot: Int): ContainerResult {
        if (!isValidSlot(fromSlot)) return Failure.BadFromIndex(fromSlot)
        if (!isValidSlot(toSlot)) return Failure.BadToIndex(toSlot)
        if (fromSlot == toSlot) return Failure.SameToFromIndex(fromSlot)
        return takeFromSlot(fromSlot).
            onSuccessType<Success.FullTakeItem> {
                val temp: BaseItem = containedItem
                takeFromSlot(toSlot)
                    .onSuccessType<Success.FullTakeItem> {
                        this@Container[fromSlot] = containedItem
                        internalItems[toSlot] = temp
                        return Success.SwapItem }
            }
    }

    /**
     * Returns: Success.FullAddItem, Success.PartialAddItem, Failure.AddBadIndex, Failure.ContainerFull, Failure.AddSlotOccupied or Failure.InvalidItemAddition
     */
    fun addItem(itemToAdd: BaseItem, slotIndex: Int): ContainerResult {
        if (itemToAdd !in this && !itemToAdd.isStackable) {
            if (containerIsFull()) return Failure.ContainerFull(itemToAdd)
            if (!isValidSlot(slotIndex)) return Failure.AddBadIndex(slotIndex, itemToAdd)
            if (itemToAdd.isInvalidItem) return Failure.InvalidItemAddition
            if (slotIsOccupied(slotIndex)) return Failure.AddSlotOccupied(slotIndex, itemToAdd)
        }

        val targetSlot =
            if (alwaysStackable || itemToAdd.isStackable) {
                unsafe.findSlotForId(itemToAdd.itemId).orOnInvalid(unsafe.firstFreeSlot)
            } else {
                slotIndex
            }

        val leftoverItemAmount: Int
        if (alwaysStackable || itemToAdd.isStackable) {
            val containedItem: BaseItem = unsafe[targetSlot]
            val maxAddAmount: Int = Int.MAX_VALUE - containedItem.itemAmount
            val newItemAmount = containedItem.itemAmount + itemToAdd.itemAmount.coerceAtMost(maxAddAmount)
            leftoverItemAmount = -((maxAddAmount - itemToAdd.itemAmount).coerceAtMost(0))
            unsafe[targetSlot] = Item(itemToAdd.itemId, newItemAmount)
        } else {
            val amountToAdd: Int = minOf(freeSlotAmount, itemToAdd.itemAmount).coerceAtMost(freeSlotAmount)
            leftoverItemAmount = -((amountToAdd - itemToAdd.itemAmount).coerceAtMost(0))
            repeat(amountToAdd) { unsafe[unsafe.firstFreeSlot] = Item(itemToAdd.itemId, 1) }
        }

        if (leftoverItemAmount > 0) {
            return Success.PartialAddItem(Item(itemToAdd.itemId, leftoverItemAmount))
        }

        return Success.FullAddItem
    }

    fun addItem(itemToAdd: BaseItem): ContainerResult = firstFreeSlot
        .onSuccessType<Success.FindSlot>{ return addItem(itemToAdd, index) }
        .onFailure { return addItem(itemToAdd, -1) }

    fun memoryFriendlyShift(shiftLeft: Boolean = true) {
        var nextSlotToFill = -1
        val defaultCurrentSlotValue = if (shiftLeft) 0 else size - 1
        var currentSlot = defaultCurrentSlotValue
        var temporaryItem: BaseItem
        while (if (shiftLeft) currentSlot < this.size else currentSlot > -1) {
            temporaryItem = internalItems[currentSlot]
            if (temporaryItem.isInvalidItem) {
                if (nextSlotToFill < 0) {
                    nextSlotToFill = currentSlot
                }
                currentSlot += if (shiftLeft) 1 else -1
                continue
            }
            if (nextSlotToFill >= 0) {
                internalItems[nextSlotToFill] = unsafe.takeFromSlot(currentSlot)
                currentSlot = nextSlotToFill + if (shiftLeft) 1 else -1
                nextSlotToFill = -1
                continue
            }
            currentSlot += if (shiftLeft) 1 else -1
        }
    }

    fun fastShift(shiftLeft: Boolean = true) {
        internalItems
            .partitionJoin(shiftLeft) { it.isValidItem }
            .forEachIndexed { slotIndex, item -> unsafe[slotIndex] = item }
    }

    /**
     * Collapses all items in to as little stacks as they can be in.
     * This enables the idea of multiple stacks of the same item, inside say the bank, but doesnt promote it as it is not RS-y.
     * This SHOULDN'T need to be used but I guess if you really want to use it, here it is.
     */
    fun shiftCollapse() {
        val finalConsolidated = Array<BaseItem>(internalItems.size) { INVALID_ITEM }
        val itemIds = internalItems.groupBy { it.itemId }
        var finalIndex = 0
        for ((itemId, items) in itemIds) {
            val currentConsolidated: MutableList<BaseItem> = mutableListOf()
            var nextAmount = 0
            var leftoverAmount = 0
            var maxAddAmount: Int
            currentConsolidated.clear()
            for ((index, item) in items.withIndex()) {
                maxAddAmount = Int.MAX_VALUE - nextAmount
                if (item.itemAmount > maxAddAmount) {
                    leftoverAmount = item.itemAmount - maxAddAmount
                    nextAmount += maxAddAmount
                } else {
                    nextAmount += item.itemAmount
                }
                if (leftoverAmount > 0) {
                    currentConsolidated.add(Item(itemId, nextAmount))
                    nextAmount = leftoverAmount
                    leftoverAmount = 0
                }
                if (index == items.lastIndex) {
                    currentConsolidated.add(Item(itemId, nextAmount))
                }
            }
            repeat(currentConsolidated.size) {
                finalConsolidated[finalIndex] = currentConsolidated[it]
                finalIndex++
            }
        }
        finalConsolidated
            .forEachIndexed { index, item -> internalItems[index] = item }
    }

    override operator fun equals(other: Any?): Boolean {
        return when (other) {
            is Container -> internalItems.contentEquals(other.internalItems)
            else -> false
        }
    }

    override fun toString(): String = internalItems.contentToString()
    override fun hashCode(): Int = (31 * size) + internalItems.contentHashCode()
}
