package container

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/** @author: nbness2 <nbness1337@gmail.com> */

/**
 *
 * A functional way to handle [ContainerResult]
 */
inline fun ContainerResult.onFailure(block: ContainerResult.Failure.() -> Unit): ContainerResult {
    contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
    if (this is ContainerResult.Failure) block(this)
    return this
}

inline fun <reified T: ContainerResult.Failure> ContainerResult.onFailureType(block: T.() -> Unit): ContainerResult {
    contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
    if (this is T) block(this)
    return this
}

inline fun ContainerResult.onSuccess(block:  ContainerResult.Success.() -> Unit): ContainerResult {
    contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
    if (this is ContainerResult.Success) block(this)
    return this
}

inline fun <reified T: ContainerResult.Success> ContainerResult.onSuccessType(block: T.() -> Unit): ContainerResult {
    contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
    if (this is T) block(this)
    return this
}
