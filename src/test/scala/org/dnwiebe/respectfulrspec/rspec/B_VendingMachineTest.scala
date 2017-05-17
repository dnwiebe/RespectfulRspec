package org.dnwiebe.respectfulrspec.rspec

import org.dnwiebe.respectfulrspec.Inventory.{CANDY, CHIPS, SODA}
import org.dnwiebe.respectfulrspec.VendingMachine
import org.dnwiebe.respectfulrspec.utils.TestUtils._
import org.junit.Assert._
import org.scalatest.path

/**
  * Created by dnwiebe on 5/11/17.
  */

class B_VendingMachineTest extends path.FunSpec {

  describe ("VendingMachine") {

    it ("initial display is INSERT_COIN") {
      val subject = new VendingMachine ()
        .addInventory (SODA, 1)
        .addInventory (CHIPS, 1)
        .addInventory (CANDY, 2)

      val result = subject.display

      assertEquals ("INSERT COIN", result)
    }

    it ("initial coin return produces nothing") {
      val subject = new VendingMachine ()
        .addInventory (SODA, 1)
        .addInventory (CHIPS, 1)
        .addInventory (CANDY, 2)

      subject.returnCoins ()

      assertEquals (List (), subject.harvestCoinReturn ())
    }

    it ("adding a nickel displays five cents and coin return is still empty") {
      val subject = new VendingMachine ()
        .addInventory (SODA, 1)
        .addInventory (CHIPS, 1)
        .addInventory (CANDY, 2)

      subject.insertCoin ("NICKEL")

      assertEquals ("$0.05", subject.display)
      assertEquals (List (), subject.harvestCoinReturn ())
    }

    it ("adding a nickel and then returning coins produces the nickel") {
      val subject = new VendingMachine ()
        .addInventory (SODA, 1)
        .addInventory (CHIPS, 1)
        .addInventory (CANDY, 2)
      subject.insertCoin ("NICKEL")

      subject.returnCoins ()

      assertBagsEqual (subject.harvestCoinReturn (), List ("NICKEL"))
    }

    it ("adding a nickel and a dime displays fifteen cents and returning coins returns them") {
      val subject = new VendingMachine ()
        .addInventory (SODA, 1)
        .addInventory (CHIPS, 1)
        .addInventory (CANDY, 2)

      subject.insertCoin ("NICKEL")
      subject.insertCoin ("DIME")

      assertEquals ("$0.15", subject.display)
      subject.returnCoins ()
      assertBagsEqual (subject.harvestCoinReturn (), List ("NICKEL", "DIME"))
    }

    it ("unrecognized coins are handled correctly") {
      val subject = new VendingMachine ()
        .addInventory (SODA, 1)
        .addInventory (CHIPS, 1)
        .addInventory (CANDY, 2)
      subject.insertCoin ("NICKEL")
      subject.insertCoin ("DIME")

      subject.insertCoin ("SHEKEL")

      assertEquals ("$0.15", subject.display)
      assertBagsEqual (subject.harvestCoinReturn (), List ("SHEKEL"))
    }

    it ("harvesting the coin return leaves it empty") {
      val subject = new VendingMachine ()
        .addInventory (SODA, 1)
        .addInventory (CHIPS, 1)
        .addInventory (CANDY, 2)
      subject.insertCoin ("NICKEL")
      subject.insertCoin ("DIME")

      subject.harvestCoinReturn ()

      assertBagsEqual (subject.harvestCoinReturn (), Nil)
    }

    it ("selecting an item that is too expensive has no effect") {
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
      subject.returnCoins ()
      assertBagsEqual (subject.harvestCoinReturn (), List ("QUARTER", "QUARTER", "NICKEL", "DIME"))
      assertBagsEqual (subject.harvestBin (), Nil)
    }

    it ("selecting an item that is too expensive and then one that is not works okay") {
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

    it ("buying entire inventory means none is left") {
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
      subject.returnCoins ()
      assertEquals ("INSERT COIN", subject.display)
      assertBagsEqual (subject.harvestCoinReturn (), List ("QUARTER", "QUARTER"))
    }

    it ("exact change works as expected") {
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
      subject.returnCoins ()
      assertBagsEqual (subject.harvestCoinReturn (), Nil)
    }

    it ("change maker can break a quarter") {
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
      assertBagsEqual (subject.harvestCoinReturn (), List ("DIME"))
    }
  }
}
