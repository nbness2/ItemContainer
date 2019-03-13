package nbness.Item.itemDefinition

import nbness.util.Number.Byte.*
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

/**
 * @author: nbness2 <nbness1337@gmail.com>
 *
 * All the logic to load the Item Definitions from the specified file.
 */

private var isInitialized: Boolean = false

/**
 * Just give the path to this guy and he'll take care of the loading for you
 */
var itemDefinitionPath: String = ""
    set(value) {
        if (isInitialized) return
        field = value
        itemDefinitionList = loadStore(value)
    }

var itemDefinitionList: ItemDefList = ItemDefList(listOf())
    set(value) {
        if (!isInitialized){
            field = value
            isInitialized = true
        }
    }

/**
 * These extensions to [ByteBuffer] make [loadItemDef] code a more readable
 */
private fun ByteBuffer.readBoolean(): Boolean = readByte().toInt() != 0
private fun ByteBuffer.readByte(): Byte = get()
private fun ByteBuffer.readShort(): Short = short
private fun ByteBuffer.readInt(): Int = int
private fun ByteBuffer.readDouble(): Double = double
private fun ByteBuffer.readRs2String(): String {
    var rs2String = ""
    var currentByte: Byte
    while (remaining() > 0) {
        currentByte = get()
        if (currentByte.toInt() == 0)
            break
        rs2String += currentByte.toChar()
    }
    return rs2String
}

/**
 * Attempts to load a single [ItemDefinition] from the given [ByteBuffer]
 */
private fun loadItemDef(buffer: ByteBuffer): ItemDefinition {
    val itemId = buffer.readShort()
    val itemName = buffer.readRs2String()
    val itemExamine = buffer.readRs2String()

    /** See [util.Number.Byte] */
    val (hasWeight, isStackable, isTradable, isNoted, isEquippable, hasHighAlch, hasReqs, hasBonuses) = buffer.readByte()

    val opponoteId = buffer.readShort()

    val itemWeight: Double = if (hasWeight) buffer.readDouble() else 0.0
    val highAlchValue: Int = if (hasHighAlch) buffer.readInt() else 0
    var itemEquipId: Short = -1
    var renderEmote: Short = -1
    var equipmentSlot: Byte = -1
    var secondarySlot: Byte = -1
    var attackSpeed: Byte = 10
    var itemBonuses: List<Short> = emptyList()
    var requiredSkillIds: List<Byte> = emptyList()
    var requiredSkillLevels: List<Byte> = emptyList()

    if (isEquippable) {
        itemEquipId = buffer.readShort()

        val hasRenderEmote = buffer.readBoolean()
        if (hasRenderEmote) renderEmote = buffer.readShort()

        equipmentSlot = buffer.readByte()
        secondarySlot = buffer.readByte()
        attackSpeed = buffer.readByte()

        if (hasBonuses) {
            val bonusesAcc = ShortArray(15)
            repeat(15) { bonusesAcc[it] = buffer.readShort() }
            itemBonuses = bonusesAcc.toList()
        }

        if (hasReqs) {
            val reqAmount = buffer.readByte().toInt()
            val requiredIds = ByteArray(reqAmount)
            val requiredLevels = ByteArray(reqAmount)
            repeat(reqAmount) {
                requiredIds[it] = buffer.readByte()
                requiredLevels[it] = buffer.readByte()
            }
            requiredSkillIds = requiredIds.toList()
            requiredSkillLevels = requiredLevels.toList()
        }
    }
    return ItemDefinition(
        itemId = itemId, itemName = itemName, itemExamine = itemExamine, equipId = itemEquipId, renderEmote = renderEmote,
        itemBonuses = itemBonuses, isStackable = isStackable, isNoted = isNoted, isTradable = isTradable, requiredSkillIds = requiredSkillIds,
        requiredSkillLevels = requiredSkillLevels, itemWeight = itemWeight, highAlchValue = highAlchValue, attackSpeed = attackSpeed,
        equipmentSlot = equipmentSlot, opponoteId = opponoteId, secondarySlot = secondarySlot
    )
}

/**
 * Attempts to load the whole ItemDef file from the given path
 */
private fun loadStore(pathString: String): ItemDefList {
    val itemDefs: MutableList<ItemDefinition> = mutableListOf()
    val channel = RandomAccessFile(pathString, "r").channel ?: throw Exception("Couldn't getUnsafe channel for path -> $pathString")
    val buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()).asReadOnlyBuffer() ?: throw Exception("Couldn't getUnsafe MappedBuffer for path -> $pathString")
    val itemDefLen = buffer.readShort().toInt()
    repeat(itemDefLen) { itemDefs.add(loadItemDef(buffer)) }
    return ItemDefList(itemDefs)
}
