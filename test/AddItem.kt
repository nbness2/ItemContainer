import nbness.Container.ContainerResult
import nbness.Item.INVALID_ITEM
import nbness.Item.Item

fun addItemTest() {
    println("AddItem tests")
    notAlwaysStackableTest()
    alwaysStackableTest()
}

fun notAlwaysStackableTest() {
    val testName = "Add item (not always stackable)"
    val itemsBefore = listOf(
        INVALID_ITEM, INVALID_ITEM, INVALID_ITEM, Item(4151), Item(4151), Item(4152, 2), INVALID_ITEM
    ).toContainer()

    val expectedAfter = listOf(
        Item(4151), Item(4151), Item(1337), Item(4151), Item(4151), Item(4152, 4), Item(1337)
    ).toContainer()

    with(itemsBefore) {
        val add1 = addItem(Item(4151, 2), 1)
        val add2 = addItem(Item(1337, 3))
        val add2Expected = ContainerResult.Success.PartialAddItem(Item(1337))
        val add3 = addItem(Item(4152, 2))
        val add4 = addItem(Item(4151))
        val add4Expected = ContainerResult.Failure.ContainerFull(Item(4151))

        assert(add1 is ContainerResult.Success.FullAddItem) {
            expectedGot(
                testName,
                ContainerResult.Success.FullAddItem,
                add1
            )
        }
        assert(add2 is ContainerResult.Success.PartialAddItem && add2.leftoverItem == add2Expected.leftoverItem) {
            expectedGot(
                testName,
                add2Expected,
                add2
            )
        }
        assert(add3 is ContainerResult.Success.FullAddItem) {
            expectedGot(
                testName,
                ContainerResult.Success.FullAddItem,
                add3
            )
        }
        assert(add4 is ContainerResult.Failure.ContainerFull && add4.containedItem == add4Expected.containedItem) {
            expectedGot(
                testName,
                add4Expected,
                add4
            )
        }
        assert(itemsBefore == expectedAfter) { "$testName failed" }
    }

    println("\t$testName passed")
}

fun alwaysStackableTest() {
    val testName = "Add item (always stackable)"
    val itemsBefore = listOf(Item(4151, 1), INVALID_ITEM, Item(4152, 2))
        .toContainer()
        .withAlwaysStackable(true)

    val expectedAfter = listOf(Item(4151, 4), Item(1337, 5), Item(4152, 3))
        .toContainer()
        .withAlwaysStackable(true)

    with(itemsBefore) {
        val add1 = addItem(Item(4151, 3), 2)
        val add2 = addItem(Item(4152, 1), 0)
        val add3 = addItem(Item(1337, 5), 1)
        val add4 = addItem(Item(14484))
        val add4Expected = ContainerResult.Failure.ContainerFull(Item(14484))

        assert(add1 is ContainerResult.Success.FullAddItem) {
            expectedGot(
                testName,
                ContainerResult.Success.FullAddItem,
                add1
            )
        }
        assert(add2 is ContainerResult.Success.FullAddItem) {
            expectedGot(
                testName,
                ContainerResult.Success.FullAddItem,
                add2
            )
        }
        assert(add3 is ContainerResult.Success.FullAddItem) {
            expectedGot(
                testName,
                ContainerResult.Success.FullAddItem,
                add3
            )
        }
        assert(add4 is ContainerResult.Failure.ContainerFull && add4.containedItem == add4Expected.containedItem) {
            expectedGot(
                testName,
                add4Expected,
                add4
            )
        }

        assert(itemsBefore == expectedAfter) { "$testName failed\n$itemsBefore\n$expectedAfter" }
    }
    println("\t$testName passed")
}