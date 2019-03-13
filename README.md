# ItemContainer

Latest release: [HERE](https://github.com/nbness2/ItemContainer/releases/latest)

ItemContainer is a Container class created (in Kotlin) to be more verbose and flexible than the container classes you might expect to see in RSPS'. The recommended use is in Kotlin.

Almost every operation on a `Container` will result in a `ContainerResult`. `ContainerResult` is somewhat inspired by `Option` and `Result`. `ContainerResult` extensions lets you handle what you want to handle, and catch the rest of the `Success` or `Failure` if you choose to do so.

`Container` was written this way to allow full expressivity and maximum safety while operating on a `Container`.

If you need to find the possible return types of a function, look at the source function. Either the docstring will contain them or, if I fail to update that for whatever reason, the function code will contain them.
