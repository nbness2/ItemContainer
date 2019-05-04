import itemstuff.*
import container.Container

fun shiftTest() {
    println("Shift tests")
    val items = BaseItemContainer(false, INVALID_ITEM, Item(1, 1), INVALID_ITEM, INVALID_ITEM, Item(3, 2), INVALID_ITEM)
    val expected = BaseItemContainer(false, Item(1, 1), Item(3, 2), INVALID_ITEM, INVALID_ITEM, INVALID_ITEM, INVALID_ITEM)
    fastShiftTest(items, expected)
    memShiftTest(items, expected)

}

fun fastShiftTest(items: Container<BaseItem>, expected: Container<BaseItem>) {
    val itemsBefore = items.copy()
    itemsBefore.fastShift()
    assert(itemsBefore == expected) { "Fast shift failed" }
    println("\tFast shift passed")
}

fun memShiftTest(items: Container<BaseItem>, expected: Container<BaseItem>) {
    val itemsBefore = items.copy()
    itemsBefore.memoryFriendlyShift()
    assert(itemsBefore == expected) { "Memory friendly shift failed" }
    println("\tMemory friendly shift passed")
}