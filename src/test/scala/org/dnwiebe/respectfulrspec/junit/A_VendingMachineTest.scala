package org.dnwiebe.respectfulrspec.junit

import org.dnwiebe.respectfulrspec.Inventory.{CANDY, CHIPS, SODA}
import org.dnwiebe.respectfulrspec.VendingMachine
import org.dnwiebe.respectfulrspec.utils.TestUtils._
import org.junit.Assert._
import org.junit.Test

/**
  * Created by dnwiebe on 5/11/17.
  */

class A_VendingMachineTest {

  @Test
  def initialDisplayIsINSERT_COIN (): Unit = {
    val subject = new VendingMachine ()
      .addInventory (SODA, 1)
      .addInventory (CHIPS, 1)
      .addInventory (CANDY, 2)

    val result = subject.display

    assertEquals ("INSERT COIN", result)
  }

  @Test
  def initialCoinReturnProducesNothing (): Unit = {
    val subject = new VendingMachine ()
      .addInventory (SODA, 1)
      .addInventory (CHIPS, 1)
      .addInventory (CANDY, 2)

    subject.returnCoins ()

    assertEquals (List (), subject.harvestCoinReturn ())
  }

  @Test
  def addingANickelDisplaysFiveCentsAndCoinReturnIsStillEmpty (): Unit = {
    val subject = new VendingMachine ()
      .addInventory (SODA, 1)
      .addInventory (CHIPS, 1)
      .addInventory (CANDY, 2)

    subject.insertCoin ("NICKEL")

    assertEquals ("$0.05", subject.display)
    assertEquals (List (), subject.harvestCoinReturn ())
  }

  @Test
  def addingANickelAndThenReturningCoinsProducesTheNickel (): Unit = {
    val subject = new VendingMachine ()
      .addInventory (SODA, 1)
      .addInventory (CHIPS, 1)
      .addInventory (CANDY, 2)
    subject.insertCoin ("NICKEL")

    subject.returnCoins ()

    assertBagsEqual (subject.harvestCoinReturn (), List ("NICKEL"))
  }

  @Test
  def addingANickelAndADimeDisplaysFifteenCentsAndReturningCoinsReturnsThem (): Unit = {
    val subject = new VendingMachine ()
      .addInventory (SODA, 1)
      .addInventory (CHIPS, 1)
      .addInventory (CANDY, 2)

    subject.insertCoin ("NICKEL")
    subject.insertCoin ("DIME")

    assertEquals ("$0.15", subject.display)
    subject.returnCoins()
    assertBagsEqual (subject.harvestCoinReturn (), List("NICKEL", "DIME"))
  }

  @Test
  def unrecognizedCoinsAreHandledCorrectly (): Unit = {
    val subject = new VendingMachine ()
      .addInventory (SODA, 1)
      .addInventory (CHIPS, 1)
      .addInventory (CANDY, 2)
    subject.insertCoin ("NICKEL")
    subject.insertCoin ("DIME")

    subject.insertCoin ("SHEKEL")

    assertEquals ("$0.15", subject.display)
    assertBagsEqual (subject.harvestCoinReturn (), List("SHEKEL"))
  }

  @Test
  def harvestingTheCoinReturnLeavesItEmpty (): Unit = {
    val subject = new VendingMachine ()
      .addInventory (SODA, 1)
      .addInventory (CHIPS, 1)
      .addInventory (CANDY, 2)
    subject.insertCoin ("NICKEL")
    subject.insertCoin ("DIME")

    subject.harvestCoinReturn ()

    assertBagsEqual (subject.harvestCoinReturn (), Nil)
  }

  @Test
  def selectingAnItemThatIsTooExpensiveHasNoEffect (): Unit = {
    val subject = new VendingMachine ()
      .addInventory (SODA, 1)
      .addInventory (CHIPS, 1)
      .addInventory (CANDY, 2)
    subject.insertCoin ("QUARTER")
    subject.insertCoin ("QUARTER")
    subject.insertCoin ("NICKEL")
    subject.insertCoin ("DIME")

    subject.select (SODA)

    assertEquals ("$0.65", subject.display)
    assertBagsEqual (subject.harvestCoinReturn (), Nil)
    subject.returnCoins()
    assertBagsEqual (subject.harvestCoinReturn (), List ("QUARTER", "QUARTER", "NICKEL", "DIME"))
    assertBagsEqual (subject.harvestBin (), Nil)
  }

  @Test
  def selectingAnItemThatIsTooExpensiveAndThenOneThatIsNotWorksOkay (): Unit = {
    val subject = new VendingMachine ()
      .addInventory (SODA, 1)
      .addInventory (CHIPS, 1)
      .addInventory (CANDY, 2)
    subject.insertCoin ("QUARTER")
    subject.insertCoin ("QUARTER")
    subject.insertCoin ("NICKEL")
    subject.insertCoin ("DIME")
    subject.select (SODA)

    subject.select (CHIPS)

    assertEquals ("INSERT COIN", subject.display)
    assertBagsEqual (subject.harvestCoinReturn (), List ("NICKEL", "DIME"))
    assertBagsEqual (subject.harvestBin (), List (CHIPS))
  }

  @Test
  def buyingEntireInventoryMeansNoneIsLeft (): Unit = {
    val subject = new VendingMachine ()
      .addInventory (SODA, 1)
      .addInventory (CHIPS, 1)
      .addInventory (CANDY, 2)
    subject.insertCoin ("QUARTER")
    subject.insertCoin ("QUARTER")
    subject.select (CHIPS)
    subject.insertCoin ("QUARTER")
    subject.insertCoin ("QUARTER")

    subject.select (CHIPS)

    assertBagsEqual (subject.harvestBin (), Nil)
    assertEquals ("SOLD OUT", subject.display)
    assertBagsEqual (subject.harvestCoinReturn (), Nil)
    subject.returnCoins()
    assertEquals ("INSERT COIN", subject.display)
    assertBagsEqual (subject.harvestCoinReturn (), List ("QUARTER", "QUARTER"))
  }

  @Test
  def exactChangeWorksAsExpected (): Unit = {
    val subject = new VendingMachine ()
      .addInventory (SODA, 1)
      .addInventory (CHIPS, 1)
      .addInventory (CANDY, 2)
    subject.insertCoin ("QUARTER")
    subject.insertCoin ("QUARTER")
    subject.insertCoin ("DIME")
    subject.insertCoin ("NICKEL")

    subject.select (CANDY)

    assertBagsEqual (subject.harvestBin (), List (CANDY))
    assertEquals ("INSERT COIN", subject.display)
    assertBagsEqual (subject.harvestCoinReturn (), Nil)
    subject.returnCoins()
    assertBagsEqual (subject.harvestCoinReturn (), Nil)
  }

  @Test
  def changeMakerCanBreakAQuarter (): Unit = {
    val subject = new VendingMachine ()
      .addInventory (SODA, 1)
      .addInventory (CHIPS, 1)
      .addInventory (CANDY, 2)
    subject.insertCoin ("QUARTER")
    subject.insertCoin ("QUARTER")
    subject.insertCoin ("DIME")
    subject.insertCoin ("NICKEL")
    subject.select (CANDY)
    subject.harvestBin ()
    subject.harvestCoinReturn ()
    subject.insertCoin ("QUARTER")
    subject.insertCoin ("QUARTER")
    subject.insertCoin ("QUARTER")

    subject.select (CANDY)

    assertEquals ("INSERT COIN", subject.display)
    assertBagsEqual (subject.harvestBin (), List (CANDY))
    assertBagsEqual (subject.harvestCoinReturn(), List ("DIME"))
  }
}
