package nbness.util.Number.Byte

import nbness.util.Number.getBit

/**
 * @author: nbness2 <nbness1337@gmail.com>
 *
 * This breaks up a byte in to 8 [Boolean]ized bits in descructurization
 */
operator fun Byte.component1(): Boolean = this.getBit(0)
operator fun Byte.component2(): Boolean = this.getBit(1)
operator fun Byte.component3(): Boolean = this.getBit(2)
operator fun Byte.component4(): Boolean = this.getBit(3)
operator fun Byte.component5(): Boolean = this.getBit(4)
operator fun Byte.component6(): Boolean = this.getBit(5)
operator fun Byte.component7(): Boolean = this.getBit(6)
operator fun Byte.component8(): Boolean = this.getBit(7)