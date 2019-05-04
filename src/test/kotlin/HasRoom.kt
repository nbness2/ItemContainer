import itemstuff.*
import container.Container

fun hasRoomTest() {
    val items = BaseItemContainer(false, INVALID_ITEM, Item(1, 1), Item(2, 5), INVALID_ITEM, Item(3, 1))
    println("Has room tests")
    hasRoomNotAlwaysStackable(items)
    hasRoomAlwaysStackable(items)
}

fun hasRoomNotAlwaysStackable(items: Container<BaseItem>) {
    val testName = "Has Room (not always stackable)"
    val container = items.copy(alwaysStackable = false)
    with(container) {
        assert(!hasRoomFor(Item(1, 3))) { expectedGot(testName, false, true) }
        addItem(Item(1, 2))
        assert(hasRoomFor(Item(2, 100))) { expectedGot(testName, true, false) }
        addItem(Item(2, 100))
        assert(!hasRoomFor(Item(3))) { expectedGot(testName, false, true) }
    }
    println("\t$testName passed")
}

fun hasRoomAlwaysStackable(items: Container<BaseItem>) {
    val testName = "Has Room (always stackable)"
    val container = items.copy(alwaysStackable = true)
    with(container) {
        assert(hasRoomFor(Item(1, 500))) { expectedGot(testName, true, false) }
        addItem(Item(1, 500))
        assert(hasRoomFor(Item(4, 2))) { expectedGot(testName, true, false) }
        addItem(Item(4, 2))
    }
    println("\t$testName passed")
}