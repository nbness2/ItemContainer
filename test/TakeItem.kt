import nbness.Container.ContainerResult
import nbness.Item.INVALID_ITEM
import nbness.Item.Item

fun takeItemTest() {
    val testName = "Take item"
    val itemsToVerify = listOf(Item(4151, 5), Item(4153, 1), INVALID_ITEM, Item(1337, 2))
        .toContainer()

    with(itemsToVerify) {
        val take1 = takeFromSlot(0, Item(4151, 3))
        val take1Expected = ContainerResult.Success.FullTakeItem(Item(4151, 3))
        val take2 = takeFromSlot(0, Item(4151, 3))
        val take2Expected = ContainerResult.Success.PartialTakeItem(Item(4151, 2), Item(4151, 1))
        val take3 = takeFromSlot(1)
        val take3Expected = ContainerResult.Success.FullTakeItem(Item(4153, 1))
        val take4 = takeFromSlot(4)
        val take4Expected = ContainerResult.Failure.BadIndex(4)

        assert(take1 is ContainerResult.Success.FullTakeItem && take1.containedItem == take1Expected.containedItem) {
            expectedGot(testName, take1Expected, take1)
        }
        assert(take2 is ContainerResult.Success.PartialTakeItem && take2.containedItem == take2Expected.containedItem && take2Expected.leftoverItem == Item(4151, 1)) {
            expectedGot(testName, take2Expected, take2)
        }
        assert(take3 is ContainerResult.Success.FullTakeItem && take3.containedItem == take3Expected.containedItem) {
            expectedGot(testName, take3Expected, take3)
        }
        assert(take4 is ContainerResult.Failure.BadIndex && take4.index == take4Expected.index) {
            expectedGot(testName, take4Expected, take4)
        }
    }
    println("$testName passed")
}