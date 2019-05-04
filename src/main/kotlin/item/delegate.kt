package item

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

internal class DelegateAlreadySet(override val message: String): Exception(message)
internal class DelegateNotSet(override val message: String): Exception(message)

internal class SingleSetVar<T: Any>: ReadWriteProperty<Any?, T> {

    var isSet: Boolean = false
        private set

    private lateinit var value: T

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        if (isSet) {
            throw DelegateAlreadySet("This delegate has already been set and cannot be set again")
        }
        this.value = value
        this.isSet = true
    }

    /**
     * Returns the value of the property for the given object.
     * @param thisRef the object for which the value is requested.
     * @param property the metadata for the property.
     * @return the property value.
     */
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (isSet) {
            return this.value
        }
        throw DelegateNotSet("This delegate has not been set and cannot be gotten")
    }
}