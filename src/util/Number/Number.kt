package nbness.util.Number

/**
 * @author: nbness2 <nbness1337@gmail.com>
 *
 * Gets the [index]th bit of the given [Number]
 */

fun Number.getBit(index: Int): Boolean = (this.toLong() and (1 shl index).toLong()) != 0L