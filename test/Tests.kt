import nbness.Container.Container
import nbness.Item.BaseItem

/**
 * @author: nbness2 <nbness1337@gmail.com>
 *
 * Tests for [Container]s Safe Access
 */
fun main() {
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

