package nbness.Item.itemDefinition

/**
 * @author: nbness2 <nbness1337@gmail.com>
 *
 * The Definition of an Item. Contains most if not all relevant data needed.
 */
data class ItemDefinition(
    val itemId: Short = -1, val itemName: String = "null", val itemExamine: String = "null", val itemWeight: Double = 0.0,
    val isStackable: Boolean = false, val isNoted: Boolean = false, val opponoteId: Short = -1, val isTradable: Boolean = false,
    val isEquippable: Boolean = false, val equipmentSlot: Byte = -1, val secondarySlot: Byte = -1, val equipId: Short = -1,
    val highAlchValue: Int = 1, val lowAlchValue: Int = (highAlchValue * 0.6).toInt(),
    val storePrice: Int = (highAlchValue * 1.666666).toInt(), val grandExchangePrice: Int = storePrice,
    val renderEmote: Short = -1, val attackSpeed: Byte = 10, val itemBonuses: List<Short> = emptyList(),
    val requiredSkillIds: List<Byte> = emptyList(), val requiredSkillLevels: List<Byte> = emptyList()
)