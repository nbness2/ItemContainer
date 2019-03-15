package nbness.Item

data class ItemDefinition (
    val itemId: Short = -1, val isStackable: Boolean = itemId % 2 == 0
) {
    val itemName: String
        get() = "Item $itemId"
}

object ItemDefinitionList {
    operator fun get(index: Short) = if (index > -1) ItemDefinition(index) else INVALID_DEF
}

val INVALID_DEF = ItemDefinition(-1, false)

val itemDefinitionList = ItemDefinitionList