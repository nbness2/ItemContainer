package nbness.Item

/**
 * Mock ItemDefinition class
 */
data class ItemDefinition (
    val itemId: Int = -1, val isStackable: Boolean = itemId % 2 == 0
) {
    val itemName: String
        get() = "Item $itemId"
}

/**
 * Mock ItemDefinition list.
 */
class ItemDefinitionList(size: Int) {
    private val internalDefs = Array<ItemDefinition>(size) { ItemDefinition(it) }
    operator fun get(index: Int) = if (index > -1) ItemDefinition(index) else INVALID_DEF
}

val INVALID_DEF = ItemDefinition(-1, false)

val itemDefinitionList = ItemDefinitionList(1_000)