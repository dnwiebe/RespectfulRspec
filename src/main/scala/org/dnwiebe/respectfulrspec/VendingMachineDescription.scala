package org.dnwiebe.respectfulrspec

/**
  * Created by dnwiebe on 5/13/17.
  */

trait VendingMachineDescription {
  def addInventory (item: Inventory, count: Int): VendingMachine
  def display: String
  def insertCoin (coinName: String): Unit
  def select (item: Inventory): Unit
  def harvestBin (): List[Inventory]
  def returnCoins (): Unit
  def harvestCoinReturn (): List[String]
}
