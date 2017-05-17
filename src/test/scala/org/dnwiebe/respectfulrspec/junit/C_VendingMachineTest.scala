package org.dnwiebe.respectfulrspec.junit

import org.dnwiebe.respectfulrspec.Inventory.{CANDY, CHIPS, SODA}
import org.dnwiebe.respectfulrspec.{Inventory, VendingMachine}
import org.dnwiebe.respectfulrspec.utils.TestUtils._
import org.junit.Assert._
import org.junit.{Before, Test}

/**
  * Created by dnwiebe on 5/11/17.
  */

class C_VendingMachineTest {
  private var subject: VendingMachine = _

  @Before
  def setup (): Unit = {
    subject = new VendingMachine ()
      .addInventory (SODA, 1)
      .addInventory (CHIPS, 1)
      .addInventory (CANDY, 2)
  }

  @Test
  def initialDisplayIsINSERT_COIN (): Unit = {

    val result = subject.display

    assertEquals ("INSERT COIN", result)
  }

  @Test
  def initialCoinReturnProducesNothing (): Unit = {

    subject.returnCoins ()

    assertEquals (List (), subject.harvestCoinReturn ())
  }

  @Test
  def addingANickelDisplaysFiveCentsAndCoinReturnIsStillEmpty (): Unit = {

    subject.insertCoin ("NICKEL")

    assertEquals ("$0.05", subject.display)
    assertEquals (List (), subject.harvestCoinReturn ())
  }

  @Test
  def addingANickelAndThenReturningCoinsProducesTheNickel (): Unit = {
    subject.insertCoin ("NICKEL")

    subject.returnCoins ()

    assertBagsEqual (subject.harvestCoinReturn (), List ("NICKEL"))
  }

  @Test
  def addingANickelAndADimeDisplaysFifteenCentsAndReturningCoinsReturnsThem (): Unit = {

    subject.insertCoin ("NICKEL")
    subject.insertCoin ("DIME")

    assertEquals ("$0.15", subject.display)
    subject.returnCoins()
    assertBagsEqual (subject.harvestCoinReturn (), List("NICKEL", "DIME"))
  }

  @Test
  def unrecognizedCoinsAreHandledCorrectly (): Unit = {
    subject.insertCoin ("NICKEL")
    subject.insertCoin ("DIME")

    subject.insertCoin ("SHEKEL")

    assertEquals ("$0.15", subject.display)
    assertBagsEqual (subject.harvestCoinReturn (), List("SHEKEL"))
  }

  @Test
  def harvestingTheCoinReturnLeavesItEmpty (): Unit = {
    subject.insertCoin ("NICKEL")
    subject.insertCoin ("DIME")

    subject.harvestCoinReturn ()

    assertBagsEqual (subject.harvestCoinReturn (), Nil)
  }

  @Test
  def selectingAnItemThatIsTooExpensiveHasNoEffect (): Unit = {
    add65Cents (subject)

    subject.select (SODA)

    verifyBin (subject, Nil)
    verifyDisplay (subject, "$0.65")
    verifyCoinReturn (subject, Nil)
    verifyBin (subject, Nil)
    verifyDisplay (subject, "INSERT COIN")
    verifyCoinReturn (subject, Nil)
  }

  @Test
  def selectingAnItemThatIsTooExpensiveAndThenOneThatIsNotWorksOkay (): Unit = {
    add65Cents (subject)
    subject.select (SODA)

    subject.select (CHIPS)

    verifyBin (subject, Nil)
    verifyDisplay (subject, "INSERT COIN")
    verifyCoinReturn (subject, List ("NICKEL", "DIME"))
  }

  @Test
  def buyingEntireInventoryMeansNoneIsLeft (): Unit = {
    subject.insertCoin ("QUARTER")
    subject.insertCoin ("QUARTER")
    subject.select (CHIPS)
    subject.insertCoin ("QUARTER")
    subject.insertCoin ("QUARTER")

    subject.select (CHIPS)

    verifyBin (subject, Nil)
    verifyDisplay (subject, "SOLD OUT")
    verifyCoinReturn (subject, List ("QUARTER", "QUARTER"))
    verifyBin (subject, Nil)
    verifyDisplay (subject, "INSERT COIN")
    verifyCoinReturn (subject, Nil)
  }

  @Test
  def exactChangeWorksAsExpected (): Unit = {
    add65Cents (subject)

    subject.select (CANDY)

    verifyBin (subject, List (CANDY))
    verifyDisplay (subject, "INSERT COIN")
    verifyCoinReturn (subject, Nil)
    verifyBin (subject, Nil)
    verifyDisplay (subject, "INSERT COIN")
    verifyCoinReturn (subject, Nil)
  }

  @Test
  def changeMakerCanBreakAQuarter (): Unit = {
    add65Cents (subject)
    subject.select (CANDY)
    subject.harvestBin ()
    subject.harvestCoinReturn ()
    subject.insertCoin ("QUARTER")
    subject.insertCoin ("QUARTER")
    subject.insertCoin ("QUARTER")

    subject.select (CANDY)

    verifyBin (subject, List (CANDY))
    verifyDisplay (subject, "INSERT COIN")
    verifyCoinReturn (subject, List ("DIME"))
  }

  private def add65Cents (subject: VendingMachine): Unit = {
    subject.insertCoin ("QUARTER")
    subject.insertCoin ("QUARTER")
    subject.insertCoin ("DIME")
    subject.insertCoin ("NICKEL")
  }

  private def verifyBin (subject: VendingMachine, bin: List[Inventory]): Unit = {
    assertBagsEqual (subject.harvestBin(), bin)
  }

  private def verifyDisplay (subject: VendingMachine, display: String): Unit = {
    assertEquals (display, subject.display)
  }

  private def verifyCoinReturn (subject: VendingMachine, coinReturn: List[String]): Unit = {
    subject.returnCoins ()
    assertBagsEqual (subject.harvestCoinReturn (), coinReturn)
  }
}
