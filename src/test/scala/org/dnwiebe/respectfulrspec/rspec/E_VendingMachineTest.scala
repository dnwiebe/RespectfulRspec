package org.dnwiebe.respectfulrspec.rspec

import org.dnwiebe.respectfulrspec.Inventory.{CANDY, CHIPS, SODA}
import org.dnwiebe.respectfulrspec.{Inventory, VendingMachine}
import org.scalatest.path
import org.dnwiebe.respectfulrspec.utils.TestUtils._

/**
  * Created by dnwiebe on 4/23/17.
  */
class E_VendingMachineTest extends path.FunSpec {

  describe ("When a VendingMachine is created and given some inventory") {
    val subject = new VendingMachine ()
      .addInventory (SODA, 1)
      .addInventory (CHIPS, 1)
      .addInventory (CANDY, 2)

    checkDisplay (subject, "INSERT COIN")
    checkCoinReturn (subject, Nil)

    describe ("and a nickel is inserted") {
      subject.insertCoin ("NICKEL")

      checkDisplay (subject, "$0.05")
      checkCoinReturn (subject, List ("NICKEL"))

      describe ("and a dime is inserted") {
        subject.insertCoin ("DIME")

        checkDisplay (subject, "$0.15")
        checkCoinReturn (subject, List ("NICKEL", "DIME"))

        describe ("and a shekel is inserted") {
          subject.insertCoin ("SHEKEL")

          checkDisplay (subject, "$0.15")
          checkCoinReturn (subject, List ("NICKEL", "DIME", "SHEKEL"))

          describe ("and two quarters are inserted") {
            subject.insertCoin ("QUARTER")
            subject.insertCoin ("QUARTER")

            checkDisplay (subject, "$0.65")
            checkCoinReturn (subject, List ("QUARTER", "QUARTER", "SHEKEL", "DIME", "NICKEL"))

            describe ("and again") {
              checkCoinReturn (subject, Nil)
            }
          }
        }
      }
    }

    describe ("and $0.65 for CANDY is inserted") {
      subject.insertCoin ("QUARTER")
      subject.insertCoin ("QUARTER")
      subject.insertCoin ("NICKEL")
      subject.insertCoin ("DIME")

      describe ("and SODA ($1.00) is selected") {
        subject.select (SODA)

        checkDisplay (subject, "$0.65")
        checkBin (subject, Nil)

        describe ("and CHIPS ($0.50) is selected") {
          subject.select (CHIPS)

          checkBin (subject, List (CHIPS))
          subject.harvestBin ()
          checkDisplay (subject, "INSERT COIN")
          checkCoinReturn (subject, List ("NICKEL", "DIME"))

          describe ("and $0.50 for CHIPS is inserted") {
            subject.insertCoin ("QUARTER")
            subject.insertCoin ("QUARTER")

            describe ("and CHIPS is selected") {
              subject.select (CHIPS)

              checkBin (subject, Nil)
              checkDisplay (subject, "SOLD OUT")
              checkCoinReturn (subject, List ("QUARTER", "QUARTER"))

              describe ("and after the coin return") {
                subject.returnCoins()
                val result = subject.display

                it ("the display is back to INSERT COIN") {
                  assert (result === "INSERT COIN")
                }
              }
            }
          }
        }

        describe ("and CANDY is selected") {
          subject.select (CANDY)

          checkBin (subject, List (CANDY))
          checkDisplay (subject, "INSERT COIN")
          checkCoinReturn (subject, Nil)
        }
      }
    }

    describe ("and three quarters are inserted") {
      subject.insertCoin ("QUARTER")
      subject.insertCoin ("QUARTER")
      subject.insertCoin ("QUARTER")

      describe ("and CANDY is selected") {
        subject.select (CANDY)

        checkBin (subject, Nil);
        checkDisplay (subject, "EXACT CHANGE")
        checkCoinReturn (subject, List("QUARTER", "QUARTER", "QUARTER"))
      }
    }

    describe ("and CANDY is purchased to put a DIME and a NICKEL in the coin box") {
      subject.insertCoin ("QUARTER")
      subject.insertCoin ("QUARTER")
      subject.insertCoin ("DIME")
      subject.insertCoin ("NICKEL")
      subject.select (CANDY)
      subject.harvestBin ()
      subject.harvestCoinReturn ()

      describe ("and three quarters (10c too much) are inserted") {
        subject.insertCoin ("QUARTER")
        subject.insertCoin ("QUARTER")
        subject.insertCoin ("QUARTER")

        describe ("and CANDY is selected") {
          subject.select (CANDY)

          checkBin (subject, List (CANDY))
          checkDisplay (subject, "INSERT COIN")
          checkCoinReturn (subject, List ("DIME"))
        }
      }
    }
  }

  private def checkBin (subject: VendingMachine, expectedBin: List[Inventory]): Unit = {
    describe ("and the bin is harvested") {
      val result = subject.harvestBin ()

      it (inventoryListToMsg (expectedBin)) {
        assert (result === expectedBin)
      }
    }
  }

  private def checkDisplay (subject: VendingMachine, expectedDisplay: String): Unit = {
    describe ("and the display is examined") {
      val result = subject.display

      it (s"says $expectedDisplay") {
        assert (result === expectedDisplay)
      }
    }
  }

  private def checkCoinReturn (subject: VendingMachine, expectedCoinReturn: List[String]): Unit = {
    describe ("and the coin return lever is pressed") {
      subject.returnCoins ()

      checkDisplay (subject, "INSERT COIN")

      describe ("and the coin return is examined") {
        val result = subject.harvestCoinReturn ()

        it (s"shows ${coinListToMsg (expectedCoinReturn)}") {
          assertBagsEqual (result, expectedCoinReturn)
        }
      }
    }
  }

  private def coinListToMsg (list: List[String]): String = {
    list match {
      case Nil => "nothing"
      case nonEmpty => nonEmpty.mkString (", ")
    }
  }

  private def inventoryListToMsg (list: List[Inventory]): String = {
    list match {
      case Nil => "there's nothing there"
      case item :: Nil => s"there's $item"
      case items => s"there's ${items.take (items.size - 1).map {_.toString}.mkString}, and ${items.last}"
    }
  }
}
