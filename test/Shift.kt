import nbness.Container.Container
import nbness.Item.BaseItem
import nbness.Item.INVALID_ITEM
import nbness.Item.Item

fun shiftTest() {
    println("Shift tests")
    val items = listOf(INVALID_ITEM, Item(4151, 1), INVALID_ITEM, INVALID_ITEM, Item(1337, 2), INVALID_ITEM)
    val expected = listOf(Item(4151, 1), Item(1337, 2), INVALID_ITEM, INVALID_ITEM, INVALID_ITEM, INVALID_ITEM)
        .toContainer()
    fastShiftTest(items, expected)
    memShiftTest(items, expected)

}

fun fastShiftTest(items: List<BaseItem>, expected: Container) {
    val itemsBefore = items.toContainer()
    itemsBefore.fastShift()
    assert(itemsBefore == expected) { "Fast shift failed" }
    println("\tFast shift passed")
}

fun memShiftTest(items: List<BaseItem>, expected: Container) {
    val itemsBefore = items.toContainer()
    itemsBefore.memoryFriendlyShift()
    assert(itemsBefore == expected) { "Memory friendly shift failed" }
    println("\tMemory friendly shift passed")
}