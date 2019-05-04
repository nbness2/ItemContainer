import itemstuff.BaseItem
import container.Container
import item.itemAccessWrapper

val baseItemAccess = itemAccessWrapper<BaseItem> {
    getItemId(BaseItem::itemId)
    getItemAmount(BaseItem::itemAmount)
    getIsStackable(BaseItem::isStackable)
    createItem(::BaseItem)
}

fun BaseItemContainer(alwaysStackable: Boolean = false, vararg items: BaseItem): Container<BaseItem> =
        Container(items.size, alwaysStackable, baseItemAccess, items::get)

/**
 * @author: nbness2 <nbness1337@gmail.com>
 *
 * Tests for [Container]s Safe Access
 */
fun main() {
    // No tests for getFromSlot and set specifically because they will propagate through some of these tests and make them wrong.
    shiftTest()
    addItemTest()
    hasRoomTest()
    verifyTest()
    swapSlotTest()
    takeItemTest()
}


/**
 * @author: nbness2 <nbness1337@gmail.com>
 *
 *
 */
fun expectedGot(testName: String, expected: Any, got: Any): String =
    "Test($testName): Expected $expected -- got $got"

