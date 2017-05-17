package org.dnwiebe.respectfulrspec

import java.text.DecimalFormat
import Coin._
import Inventory._

/**
  * Created by dnwiebe on 4/23/17.
  */
class VendingMachine extends VendingMachineDescription {
  private var pending: List[Coin] = Nil
  private var returnedCoins: List[String] = Nil
  private var binContents: List[Inventory] = Nil
  private var inventory = Map (SODA -> 0, CHIPS -> 0, CANDY -> 0)
  private var coinBox = Map (QUARTER -> 0, DIME -> 0, NICKEL -> 0)
  private var priorityMessage: Option[String] = None

  override def addInventory (item: Inventory, count: Int): VendingMachine = {
    inventory = addSome (inventory, (0 until count).map {_ => item}.toList)
    this
  }

  override def display: String = {
    if (priorityMessage.nonEmpty) {
      priorityMessage.get
    }
    else if (pending.nonEmpty) {
      showPending
    }
    else {
      "INSERT COIN"
    }
  }

  override def insertCoin (coinName: String): Unit = {
    priorityMessage = None
    try {
      pending = Coin.valueOf (coinName) :: pending
    }
    catch {
      case _: IllegalArgumentException =>
        returnedCoins = coinName :: returnedCoins
    }
  }

  override def select (item: Inventory): Unit = {
    if (pendingValue < item.price) {return}
    if (inventory (item) == 0) {priorityMessage = Some ("SOLD OUT"); return}

    val availableChange = addSome (coinBox, pending)
    val change = makeChange (availableChange, pendingValue - item.price) match {
      case Some (coins) => {
        returnedCoins = coins.map (_.name)
        inventory = removeSome (inventory, List (item))
        binContents = item :: binContents
        priorityMessage = None
        coins
      }
      case None => {
        returnedCoins = pending.map (_.name)
        priorityMessage = Some ("EXACT CHANGE")
        Nil
      }
    }

    coinBox = addSome (coinBox, pending)
    pending = Nil
    coinBox = removeSome (coinBox, change)
  }

  override def harvestBin (): List[Inventory] = {
    val result = binContents
    binContents = Nil
    result
  }

  override def returnCoins (): Unit = {
    returnedCoins = returnedCoins ++ pending.map {_.name}
    pending = Nil
    priorityMessage = None
  }

  override def harvestCoinReturn (): List[String] = {
    val result = returnedCoins
    returnedCoins = Nil
    result
  }

  private def showPending: String = {
    val format = new DecimalFormat ("$#0.00")
    val result = format.format (pendingValue / 100.0)
    result
  }

  private def pendingValue: Int = {
    val result = pending.foldLeft (0) {(soFar, elem) => soFar + elem.value}
    result
  }

  private def addSome[T] (initial: Map[T, Int], toAdd: List[T]): Map[T, Int] = {
    modifyCountMap (initial, toAdd, _ + 1)
  }

  private def removeSome[T] (initial: Map[T, Int], toRemove: List[T]): Map[T, Int] = {
    modifyCountMap (initial, toRemove, _ - 1)
  }

  private def modifyCountMap[T] (initial: Map[T, Int], delta: List[T], modifier: Int => Int): Map[T, Int] = {
    delta.foldLeft (initial) {(soFar, item) => soFar + (item -> modifier (soFar (item)))}
  }

  private def makeChange (availableChange: Map[Coin, Int], changeNeeded: Int): Option[List[Coin]] = {
    if (changeNeeded == 0) {return Some (Nil)}
    Coin.values.find {coin => (availableChange (coin) > 0) && (coin.value <= changeNeeded)} match {
      case None => None
      case Some (coin) => {
        makeChange (removeSome (availableChange, List (coin)), changeNeeded - coin.value) match {
          case None => None
          case Some (tail) => Some (coin :: tail)
        }
      }
    }
  }
}
