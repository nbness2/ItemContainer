import nbness.Container.Container
import nbness.Item.BaseItem
import nbness.Item.INVALID_ITEM
import nbness.Item.Item

fun shiftTest() {
    println("Shift tests")
    val items = Container(INVALID_ITEM, Item(1, 1), INVALID_ITEM, INVALID_ITEM, Item(3, 2), INVALID_ITEM)
    val expected = Container(Item(1, 1), Item(3, 2), INVALID_ITEM, INVALID_ITEM, INVALID_ITEM, INVALID_ITEM)
    fastShiftTest(items, expected)
    memShiftTest(items, expected)

}

fun fastShiftTest(items: Container, expected: Container) {
    val itemsBefore = items.copy()
    itemsBefore.fastShift()
    assert(itemsBefore == expected) { "Fast shift failed" }
    println("\tFast shift passed")
}

fun memShiftTest(items: Container, expected: Container) {
    val itemsBefore = items.copy()
    itemsBefore.memoryFriendlyShift()
    assert(itemsBefore == expected) { "Memory friendly shift failed" }
    println("\tMemory friendly shift passed")
}