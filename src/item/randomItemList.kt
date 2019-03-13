package nbness.Item

import kotlin.random.Random
/**
 * @author: nbness2 <nbness1337@gmail.com>
 *
 * Generate a random [List] of [Item]
 *
 * Example: generates a size 10 [List] of [Item] whose itemId can range from 0 to 100 and whose amounts can range from 0 to 5000
 * @sample: randomItemList(10, 0 to 100, 0 to 5000)
 */
fun randomItemList(amountOfItems: Int, idRange: Pair<Int, Int>, amountRange: Pair<Int, Int>) =
    List(amountOfItems) { Item(Random.nextInt(idRange.first, idRange.second).toShort(), Random.nextInt(amountRange.first, amountRange.second)) }