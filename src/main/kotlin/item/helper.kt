package item

class IAWBuilder<T> {
    var getItemIdFunction: (T) -> Short by SingleSetVar()
    var getItemAmountFunction: (T) -> Int by SingleSetVar()
    var getIsStackableFunction: (T) -> Boolean by SingleSetVar()
    var createItemFunction: (Short, Int) -> T by SingleSetVar()
    fun getItemId(func: (T) -> Short) { getItemIdFunction = func }
    fun getItemAmount(func: (T) -> Int) { getItemAmountFunction = func }
    fun getIsStackable(func: (T) -> Boolean) { getIsStackableFunction = func }
    fun createItem(func: (Short, Int) -> T) { createItemFunction = func }
    fun build(): ItemAccessWrapper<T> =
            object: ItemAccessWrapper<T> {
                override val getItemId = getItemIdFunction
                override val getItemAmount = getItemAmountFunction
                override val getIsStackable = getIsStackableFunction
                override val createItem = createItemFunction
                override val INVALID_ITEM = createItem(-1, -1)
            }
}

fun <T> itemAccessWrapper(builder: IAWBuilder<T>.() -> Unit): ItemAccessWrapper<T> =
        IAWBuilder<T>().apply(builder).build()