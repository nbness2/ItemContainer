import itemstuff.*
import container.ContainerResult

fun addItemTest() {
    println("AddItem tests")
    notAlwaysStackableTest()
    alwaysStackableTest()
}

fun notAlwaysStackableTest() {
    val testName = "Add item (not always stackable)"
    val itemsBefore = BaseItemContainer(false, INVALID_ITEM, INVALID_ITEM, INVALID_ITEM, Item(1), Item(1), Item(2, 2), INVALID_ITEM)

    val expectedAfter = BaseItemContainer(false, Item(1), Item(1), Item(3), Item(1), Item(1), Item(2, 4), Item(3))

    with(itemsBefore) {
        val add1 = addItem(Item(1, 2), 1)
        val add2 = addItem(Item(3, 3))
        val add2Expected = ContainerResult.Success.PartialAddItem(Item(3))
        val add3 = addItem(Item(2, 2))
        val add4 = addItem(Item(1))
        val add4Expected = ContainerResult.Failure.ContainerFull(Item(1))
        /**
         * This test was because a logical error allowed an IndexError to fall through.
         * This exception arose when adding a stackable item to a full container that was not alwaysStackable that didnt contain any of that item.
         */
        val add5 = addItem(Item(4))
        val add5Expected = ContainerResult.Failure.ContainerFull(Item(4))

        assert(add1 is ContainerResult.Success.FullAddItem) {
            expectedGot(testName, ContainerResult.Success.FullAddItem, add1)
        }
        assert(add2 is ContainerResult.Success.PartialAddItem<*> && add2.leftoverItem == add2Expected.leftoverItem) {
            expectedGot(testName, add2Expected, add2)
        }
        assert(add3 is ContainerResult.Success.FullAddItem) {
            expectedGot(testName, ContainerResult.Success.FullAddItem, add3)
        }
        assert(add4 is ContainerResult.Failure.ContainerFull<*> && add4.containedItem == add4Expected.containedItem) {
            expectedGot(testName, add4Expected, add4)
        }
        assert(add5 is ContainerResult.Failure.ContainerFull<*> && add5.containedItem == add5Expected.containedItem) {
            expectedGot(testName, add5Expected, add5)
        }
        assert(itemsBefore == expectedAfter) { "$testName failed" }
    }

    println("\t$testName passed")
}

fun alwaysStackableTest() {
    val testName = "Add item (always stackable)"
    val itemsBefore = BaseItemContainer(true, Item(1, 1), INVALID_ITEM, Item(2, 2))
    val expectedAfter = BaseItemContainer(true, Item(1, 4), Item(3, 5), Item(2, 3))

    with(itemsBefore) {
        val add1 = addItem(Item(1, 3), 2)
        val add2 = addItem(Item(2, 1), 0)
        val add3 = addItem(Item(3, 5), 1)
        val add4 = addItem(Item(5))
        val add4Expected = ContainerResult.Failure.ContainerFull(Item(5))
        val add5 = addItem(Item(4))
        val add5Expected = ContainerResult.Failure.ContainerFull(Item(4))

        assert(add1 is ContainerResult.Success.FullAddItem) {
            expectedGot(testName, ContainerResult.Success.FullAddItem, add1)
        }
        assert(add2 is ContainerResult.Success.FullAddItem) {
            expectedGot(testName, ContainerResult.Success.FullAddItem, add2)
        }
        assert(add3 is ContainerResult.Success.FullAddItem) {
            expectedGot(testName, ContainerResult.Success.FullAddItem, add3)
        }
        assert(add4 is ContainerResult.Failure.ContainerFull<*> && add4.containedItem == add4Expected.containedItem) {
            expectedGot(testName, add4Expected, add4)
        }
        assert(add5 is ContainerResult.Failure.ContainerFull<*> && add5.containedItem == add5Expected.containedItem) {
            expectedGot(testName, add5Expected, add5)
        }

        assert(itemsBefore == expectedAfter) { "$testName failed\n$itemsBefore\n$expectedAfter" }
    }
    println("\t$testName passed")
}