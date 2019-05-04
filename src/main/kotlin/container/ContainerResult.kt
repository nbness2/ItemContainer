package container

/**
 * @author: nbness2 <nbness1337@gmail.com>
 *
 * The main return type for all functions in [Container]
 */
sealed class ContainerResult {

    internal interface ContainsIndex { val index: Int }
    internal interface ContainsItem<T> { val containedItem: T }
    internal interface ContainsLeftoverItem<T> { val leftoverItem: T }
    internal interface ContainsFoundItem<T> { val foundItem: T }

    /**
     * Signifies a successful [Container] operation
     */
    sealed class Success : ContainerResult() {
        override fun toString(): String = "Success.${this::class.simpleName}"
        class GetItem<T>(override val containedItem: T) : Success(), ContainsItem<T>
        object SetItem : Success()
        object FullAddItem : Success()
        class PartialAddItem<T>(override val leftoverItem: T) : Success(), ContainsLeftoverItem<T>
        class VerifyItem<T>(override val containedItem: T) : Success(), ContainsItem<T>
        object SwapItem : Success()
        class FindSlot(override val index: Int) : Success(), ContainsIndex
        class FullTakeItem<T>(override val containedItem: T) : Success(), ContainsItem<T>
        class PartialTakeItem<T>(override val containedItem: T, override val leftoverItem: T) : Success(), ContainsItem<T>, ContainsLeftoverItem<T> {
            override fun toString(): String = "Success.PartialTakeItem($containedItem, $leftoverItem)"
        }
    }

    /**
     * Signifies a failed [Container] operation
     */
    sealed class Failure : ContainerResult() {
        override fun toString(): String = "Failure.${this::class.simpleName}"
        class BadIndex(override val index: Int) : Failure(), ContainsIndex
        class AddBadIndex<T>(override val index: Int, override val containedItem: T) : Failure(), ContainsIndex, ContainsItem<T>
        class BadFromIndex(override val index: Int) : Failure(), ContainsIndex
        class BadToIndex(override val index: Int) : Failure(), ContainsIndex
        class SameToFromIndex(override val index: Int) : Failure(), ContainsIndex
        class SlotOccupied(override val index: Int) : Failure(), ContainsIndex
        class AddSlotOccupied<T>(override val index: Int, override val containedItem: T) : Failure(), ContainsIndex, ContainsItem<T>
        class ItemIdMismatch<T>(override val containedItem: T, override val foundItem: T) : Failure(), ContainsItem<T>, ContainsFoundItem<T>
        class ContainerFull<T>(override val containedItem: T) : Failure(), ContainsItem<T>
        class ItemNotFound<T>(override val containedItem: T) : Failure(), ContainsItem<T>
        object NoFreeSlots : Failure()
        object InvalidItemAddition : Failure()
        object NotEnoughItemAmount : Failure()
        object NotExactItemAmount : Failure()
    }

    override fun toString(): String = this::class.qualifiedName.toString()
}


