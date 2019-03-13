import nbness.Container.Container
import nbness.Item.BaseItem
import nbness.Item.itemDefinition.itemDefinitionPath

/**
 * @author: nbness2 <nbness1337@gmail.com>
 *
 * Tests for [Container]s Safe Access
 */
fun main() {
    itemDefinitionPath = "src/test/resources/ItemDefinitions.idf"
    // No tests for get and set specifically because they will propagate through some of these tests and make them wrong.
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

fun List<BaseItem>.toContainer(alwaysStackable: Boolean = false): Container =
    Container(alwaysStackable = alwaysStackable, initList = this)
