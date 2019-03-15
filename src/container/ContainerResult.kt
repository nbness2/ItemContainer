package nbness.Container

import nbness.Item.*


/**
 * @author: nbness2 <nbness1337@gmail.com>
 *
 * The main return type for all functions in [Container]
 */
sealed class ContainerResult {

    internal interface ContainsIndex { val index: Int }
    internal interface ContainsItem { val containedItem: BaseItem }
    internal interface ContainsLeftoverItem { val leftoverItem: BaseItem }
    internal interface ContainsFoundItem { val foundItem: BaseItem }

    /**
     * Signifies a successful [Container] operation
     */
    sealed class Success : ContainerResult() {
        override fun toString(): String = "Success.${this::class.simpleName}"
        class GetItem(override val containedItem: BaseItem) : Success(), ContainsItem
        object SetItem : Success()
        object FullAddItem : Success()
        class PartialAddItem(override val leftoverItem: BaseItem) : Success(), ContainsLeftoverItem
        class VerifyItem(override val containedItem: BaseItem) : Success(), ContainsItem
        object SwapItem : Success()
        class FindSlot(override val index: Int) : Success(), ContainsIndex
        class FullTakeItem(override val containedItem: BaseItem) : Success(), ContainsItem
        class PartialTakeItem(override val containedItem: BaseItem, override val leftoverItem: BaseItem) : Success(), ContainsItem, ContainsLeftoverItem {
            override fun toString(): String = "Success.PartialTakeItem($containedItem, $leftoverItem)"
        }
    }

    /**
     * Signifies a failed [Container] operation
     */
    sealed class Failure : ContainerResult() {
        override fun toString(): String = "Failure.${this::class.simpleName}"
        class BadIndex(override val index: Int) : Failure(), ContainsIndex
        class AddBadIndex(override val index: Int, override val containedItem: BaseItem) : Failure(), ContainsIndex, ContainsItem
        class BadFromIndex(override val index: Int) : Failure(), ContainsIndex
        class BadToIndex(override val index: Int) : Failure(), ContainsIndex
        class SameToFromIndex(override val index: Int) : Failure(), ContainsIndex
        class SlotOccupied(override val index: Int) : Failure(), ContainsIndex
        class AddSlotOccupied(override val index: Int, override val containedItem: BaseItem) : Failure(), ContainsIndex, ContainsItem
        class ItemIdMismatch(override val containedItem: BaseItem, override val foundItem: BaseItem) : Failure(), ContainsItem, ContainsFoundItem
        class ContainerFull(override val containedItem: BaseItem) : Failure(), ContainsItem
        class ItemNotFound(override val containedItem: BaseItem) : Failure(), ContainsItem
        object NoFreeSlots : Failure()
        object InvalidItemAddition : Failure()
        object NotEnoughItemAmount : Failure()
        object NotExactItemAmount : Failure()
    }

    override fun toString(): String = this::class.qualifiedName.toString()
}


