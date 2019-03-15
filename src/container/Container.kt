package nbness.Container

import nbness.Container.ContainerResult.*
import nbness.util.array.partitionJoin
import nbness.util.boolean.*
import nbness.Item.*

/**
 * @author: nbness2 <nbness1337@gmail.com>
 *
 * Mutable container of Immutable items.
 * Provides both safe and unsafe ways of operating on this.
 */
class Container(
    /** @property size */
    @JvmField val size: Int,
    /** @property alwaysStackable */
    @JvmField val alwaysStackable: Boolean,
    /** @param[initList] Initializes the [Container] with [initList]*/
    initList: Array<BaseItem> = emptyArray()
) {

    constructor(vararg items: BaseItem) : this(items.size, false, items as Array<BaseItem>)
    constructor(alwaysStackable: Boolean, vararg items: BaseItem) : this(items.size, alwaysStackable, items as Array<BaseItem>)
    constructor(size: Int, alwaysStackable: Boolean = false, init: (Int) -> BaseItem) : this(size, alwaysStackable, Array(size, init))

    private val internalItems: Array<BaseItem> =
        if (initList.isEmpty()) {
            Array(size) { INVALID_ITEM }
        } else {
            if (initList.size != size) {
                throw Exception("initList size [${initList.size}] does not correspond with given size [$size]")
            }
            initList
        }

    /**
     * @property unsafe Lets you access operations in an "unsafe" manner
     * @see [UnsafeAccess]
     */
    @JvmField val unsafe: UnsafeAccess = UnsafeAccess(this, internalItems)

    /**
     * @param[alwaysStackable] Creates a new [Container] with the given [alwaysStackable] value
     */
    @JvmOverloads fun copy(alwaysStackable: Boolean = false) = Container(size, alwaysStackable, internalItems.copyOf())

    /**
     * @return [Success.FindSlot] or [Failure.NoFreeSlots]
     */
    val firstFreeSlot: ContainerResult
        get() {
            val index = internalItems.indexOfFirst { it.isInvalidItem }
            return if (index < 0) {
                Failure.NoFreeSlots
            } else {
                Success.FindSlot(index)
            }
        }

    /**
     * The amount of free slots (how many items resolve to true for [BaseItem.isInvalidItem]) this [Container] has
     * @return [Int]
     */
    val freeSlotAmount: Int
        get() = internalItems.count { it.isInvalidItem }


    private fun Int.orOnInvalid(other: Int): Int = if (this < 0) other else this

    /**
     * Checks if the given item shares an item id with any other item in this [Container]
     *
     * @param[item] The [BaseItem] you are checking is contained in [internalItems]
     *
     * @return [Boolean]
     */
    operator fun contains(item: BaseItem): Boolean = internalItems.any { it.sharesItemIdWith(item) }

    /**
     * Gets a [ContainerResult] for the given [slotIndex]
     *
     * @param[slotIndex] The index of the slot you are attempting to get from
     *
     * @return [Success.GetItem] or [Failure.BadIndex]
     */
    operator fun get(slotIndex: Int): ContainerResult {
        return if (isValidSlot(slotIndex))
            Success.GetItem(unsafe[slotIndex])
        else
            Failure.BadIndex(slotIndex)
    }

    /**
     * Attempts to set a [slotIndex] to [itemToSet]
     *
     * @param[slotIndex] The index of the slot you are attempting to set to
     * @param[itemToSet] the [BaseItem] you are attempting to put in slot [slotIndex]
     *
     * @return [Success.SetItem], [Failure.BadIndex] or [Failure.SlotOccupied]
     */
    operator fun set(slotIndex: Int, itemToSet: BaseItem): ContainerResult = this[slotIndex, itemToSet]

    /** @see [Container.set] */
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
     * Verifies that [itemToVerify] has the same itemId as the [BaseItem] in [slotIndex] meets [verificationCondition]
     *
     * @param[itemToVerify] The item you are verifying meets the conditions
     * @param[slotIndex] The index of the [BaseItem] you are comparing [itemToVerify] to
     * @param [verificationCondition] The condition that [itemToVerify] must meet.
     *
     * @return [Success.VerifyItem], [Failure.BadIndex] or [Failure.ItemIdMismatch]
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
     * Verifies that there are exactly [itemToVerify.itemAmount] of [itemToVerify.itemId] in [internalItems] slot [slotIndex]
     *
     * @param[itemToVerify] The item that you are comparing the item in [internalItems] to
     * @param[slotIndex] The slot index of [internalItems] you are comparing to [itemToVerify]
     *
     * @return [Success.VerifyItem], [Failure.BadIndex], [Failure.ItemIdMismatch] or [Failure.NotExactItemAmount]
     */
    fun verifyExact(itemToVerify: BaseItem, slotIndex: Int): ContainerResult =
        verify(itemToVerify, slotIndex) {
            if (!it.sharesItemIdWith(itemToVerify)) {
                return@verify false
            }
            if (it.itemAmount != itemToVerify.itemAmount) {
                return Failure.NotExactItemAmount
            }
            return@verify true
        }

    /**
     * Verifies that there is at least [itemToVerify.itemAmount] of [itemToVerify.itemId] in [internalItems] slot [slotIndex]
     *
     * @param[itemToVerify] The item that you are comparing the item in [internalItems] to
     * @param[slotIndex] The slot index of [internalItems] you are comparing to [itemToVerify]
     *
     * @return [Success.VerifyItem], [Failure.BadIndex], [Failure.ItemIdMismatch] or [Failure.NotEnoughItemAmount]
     */
    fun verifyAtLeast(itemToVerify: BaseItem, slotIndex: Int): ContainerResult =
        verify(itemToVerify, slotIndex) {
            if (!it.sharesItemIdWith(itemToVerify)) {
                return@verify false
            }
            if (it.itemAmount < itemToVerify.itemAmount) {
                return Failure.NotEnoughItemAmount
            }
            return@verify true
        }

    /**
     * Verifies that there is at least 1 of [itemToVerify.itemId] in [internalItems] slot [slotIndex]
     *
     * @param[itemToVerify] The item you are comparing the item in [internalItems] to
     * @param[slotIndex] The slot index of [internalItems] you are comparing to [itemToVerify]
     *
     * @return [Success.VerifyItem], [Failure.BadIndex], [Failure.ItemIdMismatch] or [Failure.NotEnoughItemAmount]
     */
    fun verifyOne(itemToVerify: BaseItem, slotIndex: Int): ContainerResult =
        verify(itemToVerify, slotIndex) { it.sharesItemIdWith(itemToVerify) }

    /**
     * Check if the slot [slotIndex] is occupied
     *
     * @param[slotIndex] The index of the slot you are checking is occupied
     *
     * @return [Boolean]
     */
    fun slotIsOccupied(slotIndex: Int): Boolean = internalItems[slotIndex].isValidItem

    /**
     * Checks if the given [slotIndex] is valid
     *
     * @param[slotIndex] The index of the sot you are checking is valid
     *
     * @return [Boolean]
     */
    fun isValidSlot(slotIndex: Int): Boolean = slotIndex in internalItems.indices

    /**
     * Checks if the [Container] is full
     *
     * @return [Boolean]
     */
    fun containerIsFull(): Boolean = firstFreeSlot is Failure.NoFreeSlots

    /**
     * Checks if the [Container] has any space
     *
     * @return [Boolean]
     */
    fun containerHasSpace(): Boolean = firstFreeSlot is Success.FindSlot

    /**
     * Checks if the [Container] has space for [item]. Takes in to account [alwaysStackable] and if [item] is stackable or not.
     *
     * @return [Boolean]
     */
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
     * Finds the slot index for at least [itemToFind.itemAmount] [itemToFind]
     *
     * @param[itemToFind] The [BaseItem] to find the slot for.
     *
     * @return [Success.FindSlot], [Failure.ItemNotFound] or [Failure.NotEnoughItemAmount]
     */
    fun findSlotForAtLeast(itemToFind: BaseItem): ContainerResult {
        val foundSlot = internalItems.indexOfFirst { itemToFind.sharesItemIdWith(it) }
        return this[foundSlot]
            .onFailure { return Failure.ItemNotFound(itemToFind) }
            .onSuccessType<Success.GetItem> {
                containedItem
                    .hasAtLeast(itemToFind.itemAmount)
                    .onFalse { return Failure.NotEnoughItemAmount }
                    .otherwise { Success.FindSlot(foundSlot) }
            }
    }

    /**
     * Finds the slot index for [itemIdToFind]
     *
     * @param[itemIdToFind] The item id to find the slot for.
     *
     * @return [Success.FindSlot] or [Failure.ItemNotFound]
     */
    fun findSlotForId(itemIdToFind: Number): ContainerResult = findSlotForAtLeast(Item(itemIdToFind.toShort(), 1))

    /**
     * Finds the slot index for at least 1 [itemToFind]
     *
     * @param[itemToFind] The [BaseItem] to find the slot for.
     *
     * @return [Success.FindSlot] or [Failure.ItemNotFound]
     */
    fun findSlotForItem(itemToFind: BaseItem): ContainerResult = findSlotForAtLeast(Item(itemToFind.itemId))

    /**
     * Takes the item from slot [slotIndex] and sets [slotIndex] to [INVALID_ITEM]
     *
     * @param[slotIndex] The index to take the item from.
     *
     * @return [Success.FullTakeItem] or [Failure.BadIndex]
     */
    fun takeFromSlot(slotIndex: Int): ContainerResult =
        this[slotIndex]
            .onSuccessType<Success.GetItem> {
                internalItems[slotIndex] = INVALID_ITEM
                return Success.FullTakeItem(containedItem) }

    /**
     * Attempts to take all of [itemToTake] from [slotIndex] and sets [slotIndex] to [INVALID_ITEM].
     * Will only ever take a maximum of [itemToTake.itemAmount].
     * Will return a smaller amount but still take if the itemId in [slotIndex] matches [itemToTake.itemId]
     *
     * @param[slotIndex] The index to attempt take the item from.
     * @param[itemToTake] The item
     *
     * @return [Success.FullTakeItem], [Success.PartialTakeItem] or [Failure.BadIndex]
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
     * Attempts to swap the slot contents of [fromSlot] and [toSlot]
     *
     * @param[fromSlot] One of the slots in which the content is getting swapped.
     * @param[toSlot] One of the slots in which the content is getting swapped.
     *
     * @return [Success.SwapItem], [Failure.BadFromIndex], [Failure.BadToIndex] or [Failure.SameToFromIndex]
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
                        return Success.SwapItem
                    }
            }
    }

    /**
     * Attempts to add [itemToAdd] to the slot [slotIndex].
     * Will either place it in the specified slot or the first [itemToAdd.itemAmount] slots it can find.
     * Will add as much as it can, if it cannot add all it will return [Success.PartialAddItem] with the remaining items in [Success.PartialAddItem.leftoverItem]
     *
     * @return [Success.FullAddItem], [Success.PartialAddItem], [Failure.AddBadIndex], [Failure.ContainerFull], [Failure.AddSlotOccupied] or [Failure.InvalidItemAddition]
     */
    fun addItem(itemToAdd: BaseItem, slotIndex: Int): ContainerResult {
        if (containerIsFull()) {
            if (alwaysStackable) {
                if (itemToAdd !in this) {
                    return Failure.ContainerFull(itemToAdd)
                }
            } else {
                if (!itemToAdd.isStackable) {
                    return Failure.ContainerFull(itemToAdd)
                }
            }
        }

        if (itemToAdd.isInvalidItem) return Failure.InvalidItemAddition

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

    /**
     * Will attempt to add [itemToAdd] in the first available slot.
     * @see [addItem]
     * @return [Success.FullAddItem], [Success.PartialAddItem], [Failure.AddBadIndex], [Failure.ContainerFull], [Failure.AddSlotOccupied] or [Failure.InvalidItemAddition]
     */

    fun addItem(itemToAdd: BaseItem): ContainerResult = firstFreeSlot
        .onSuccessType<Success.FindSlot>{ return addItem(itemToAdd, index) }
        .onFailure { return addItem(itemToAdd, -1) }

    /**
     * Shifts all items "left" or "right" depending on [shiftLeft] in a memory friendly way.
     * [memoryFriendlyShift] uses less memory than [fastShift] but performs worse on average on [Container] over 128 items.
     *
     * @param[shiftLeft] Shifting the items left(true) or right(false)
     */
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

    /**
     * Shifts all items "left" or "right" depending on [shiftLeft] in a fast way.
     * [fastShift] uses more memory than [memoryFriendlyShift] but performs better on average on [Container] over 128 items.
     *
     * @param[shiftLeft] Shifting the items left(true) or right(false)
     */
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

    /**
     * Checks if the contents of this [Container] and [other] are equal if [other] is a [Container]
     *
     * @param[other] The other object to check equality with.
     *
     * @return [Boolean]
     */
    override operator fun equals(other: Any?): Boolean {
        return when (other) {
            is Container -> internalItems.contentEquals(other.internalItems)
            else -> false
        }
    }

    override fun toString(): String = internalItems.contentToString()
    override fun hashCode(): Int = (31 * size) + internalItems.contentHashCode()
}
