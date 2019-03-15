import nbness.Container.Container
import nbness.Container.ContainerResult
import nbness.Item.INVALID_ITEM
import nbness.Item.Item

fun swapSlotTest() {
    val testName = "Swap item"
    val itemsToVerify = Container(Item(1, 2), Item(1, 4), INVALID_ITEM)

    with(itemsToVerify) {

        val swap1 = swapSlotContents(0, 2)
        assert(swap1 is ContainerResult.Success.SwapItem && (unsafe[0] == INVALID_ITEM && unsafe[2] == Item(1, 2))) {
            println(itemsToVerify)
            expectedGot(testName, ContainerResult.Success.SwapItem, swap1)
        }

        val swap2 = swapSlotContents(1, 2)
        assert(swap2 is ContainerResult.Success.SwapItem && (unsafe[1] == Item(1, 2) && unsafe[2] == Item(1, 4))) {
            expectedGot(testName, ContainerResult.Success.SwapItem, swap2)
        }

        val swap3 = swapSlotContents(1, 3)
        val swap3Expected = ContainerResult.Failure.BadToIndex(3)
        val swap4 = swapSlotContents(1, 1)
        val swap4Expected = ContainerResult.Failure.SameToFromIndex(1)

        assert(swap3 is ContainerResult.Failure.BadToIndex && swap3.index == swap3Expected.index) {
            expectedGot(testName, swap3Expected, swap3)
        }
        assert(swap4 is ContainerResult.Failure.SameToFromIndex && swap4.index == swap4Expected.index) {
            expectedGot(testName, swap4Expected, swap4)
        }
    }
    println("$testName passed")
}