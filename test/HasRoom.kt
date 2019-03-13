import nbness.Item.BaseItem
import nbness.Item.INVALID_ITEM
import nbness.Item.Item

fun hasRoomTest() {
    val items = listOf(INVALID_ITEM, Item(4151, 1), Item(4152, 5), INVALID_ITEM, Item(1337, 1))
    println("Has room tests")
    hasRoomNotAlwaysStackable(items)
    hasRoomAlwaysStackable(items)
}

fun hasRoomNotAlwaysStackable(items: List<BaseItem>) {
    val testName = "Has Room (not always stackable)"
    val container = items.toContainer(alwaysStackable = false)
    with(container) {
        assert(!hasRoomFor(Item(4151, 3))) { expectedGot(testName, false, true) }
        addItem(Item(4151, 2))
        assert(hasRoomFor(Item(4152, 100))) { expectedGot(testName, true, false) }
        addItem(Item(4152, 100))
        assert(!hasRoomFor(Item(1337))) { expectedGot(testName, false, true) }
    }
    println("\t$testName passed")
}

fun hasRoomAlwaysStackable(items: List<BaseItem>) {
    val testName = "Has Room (always stackable)"
    val container = items.toContainer(alwaysStackable = true)
    with(container) {
        assert(hasRoomFor(Item(4151, 500))) { expectedGot(testName, true, false) }
        addItem(Item(4151, 500))
        assert(hasRoomFor(Item(1338, 2))) { expectedGot(testName, true, false) }
        addItem(Item(1338, 2))
    }
    println("\t$testName passed")
}