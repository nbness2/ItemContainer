import nbness.Container.ContainerResult
import nbness.Item.Item

fun verifyTest() {
    val testName = "Verification"
    val itemsToVerify = listOf(Item(4151), Item(1337, 5), Item(4153, 250))
        .toContainer()

    with(itemsToVerify) {
        val verify1 = verifyOne(Item(4151), 0)
        val verify1Expected = ContainerResult.Success.VerifyItem(Item(4151))
        val verify2 = verifyAtLeast(Item(4151, 2), 0)
        val verify3 = verifyExact(Item(1337, 5), 1)
        val verify3Expected = ContainerResult.Success.VerifyItem(Item(1337, 5))
        val verify4 = verifyExact(Item(1234, 250), 2)
        val verify4Expected = ContainerResult.Failure.ItemIdMismatch(Item(1234, 250), Item(4153, 250))
        val verify5 = verify(Item(), 5) { it.isValidItem }
        val verify5Expected = ContainerResult.Failure.BadIndex(5)

        assert(verify1 is ContainerResult.Success.VerifyItem && verify1.containedItem == verify1Expected.containedItem) {
            expectedGot(testName, verify1Expected, verify1)
        }
        assert(verify2 is ContainerResult.Failure.NotEnoughItemAmount) {
            expectedGot(
                testName,
                ContainerResult.Failure.NotEnoughItemAmount,
                verify2
            )
        }
        assert(verify3 is ContainerResult.Success.VerifyItem && verify3.containedItem == verify3Expected.containedItem) {
            expectedGot(testName, verify3Expected, verify3)
        }
        assert(verify4 is ContainerResult.Failure.ItemIdMismatch && verify4.containedItem == verify4Expected.containedItem && verify4.foundItem == verify4Expected.foundItem) {
            expectedGot(testName, verify4Expected, verify4)
        }
        assert(verify5 is ContainerResult.Failure.BadIndex && verify5.index == verify5Expected.index) {
            expectedGot(testName, verify5Expected, verify5)
        }
    }
    println("$testName passed")
}