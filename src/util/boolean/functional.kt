package nbness.util.boolean

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * @author: nbness2 <nbness1337@gmail.com>
 *
 * Im not 100% sure why I created this, but I'm sure there was a reason.
 */

inline fun Boolean.onTrue(block: () -> Unit): Boolean {
    contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
    if (this) block()
    return this
}

inline fun Boolean.onFalse(block: () -> Unit): Boolean {
    contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
    if (!this) block()
    return this
}

inline fun <T> Boolean.otherwise(block: () -> T): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return block()
}

val ILLEGAL_EXECUTION_BOOLEAN =
    """
        The signature for these functional functions are `Boolean`.
        Kotlin's `Boolean` compiles to JVM `boolean`
        According to JLS (https://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html) [4.2]: "The boolean type has exactly two values: true and false."
        Almost ANY language spec does not allow 3 values for their version of a `Boolean`.
        So my question for you is how the did you manage to get to this Boolean.otherwise() block?
        You cannot modify this object by referncing `this` in these execution blocks.
        This doesn't work with Kotlin's delegates.
        I simply don't understand...
        Well, be on your way, I guess.
    """.trimIndent()
