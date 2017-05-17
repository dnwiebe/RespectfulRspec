package org.dnwiebe.respectfulrspec.rspec

import org.dnwiebe.respectfulrspec.Inventory.{CANDY, CHIPS, SODA}
import org.dnwiebe.respectfulrspec.VendingMachine
import org.dnwiebe.respectfulrspec.utils.TestUtils._
import org.scalatest.path

/**
  * Created by dnwiebe on 4/23/17.
  */
class D_VendingMachineTest extends path.FunSpec {

  describe ("When a VendingMachine is created and given some inventory") {
    val subject = new VendingMachine ()
      .addInventory (SODA, 1)
      .addInventory (CHIPS, 1)
      .addInventory (CANDY, 2)

    describe ("and the display is examined") {
      val result = subject.display

      it ("says INSERT COIN") {
        assert (result === "INSERT COIN")
      }
    }

    describe ("and the coin return lever is pressed") {
      subject.returnCoins ()

      describe ("and the display is examined") {
        val result = subject.display

        it ("says INSERT COIN") {
          assert (result === "INSERT COIN")
        }
      }

      describe ("and the coin return is examined") {
        val result = subject.harvestCoinReturn ()

        it ("shows Nil") {
          assertBagsEqual (result, Nil)
        }
      }
    }

    describe ("and a nickel is inserted") {
      subject.insertCoin ("NICKEL")

      describe ("and the display is examined") {
        val result = subject.display

        it ("says $0.05") {
          assert (result === "$0.05")
        }
      }

      describe ("and the coin return lever is pressed") {
        subject.returnCoins ()

        describe ("and the display is examined") {
          val result = subject.display

          it ("says INSERT COIN") {
            assert (result === "INSERT COIN")
          }
        }

        describe ("and the coin return is examined") {
          val result = subject.harvestCoinReturn ()

          it ("shows NICKEL") {
            assertBagsEqual (result, List ("NICKEL"))
          }
        }
      }

      describe ("and a dime is inserted") {
        subject.insertCoin ("DIME")

        describe ("and the display is examined") {
          val result = subject.display

          it ("says $0.15") {
            assert (result === "$0.15")
          }
        }

        describe ("and the coin return lever is pressed") {
          subject.returnCoins ()

          describe ("and the display is examined") {
            val result = subject.display

            it ("says INSERT COIN") {
              assert (result === "INSERT COIN")
            }
          }

          describe ("and the coin return is examined") {
            val result = subject.harvestCoinReturn ()

            it ("shows NICKEL, DIME") {
              assertBagsEqual (result, List ("NICKEL", "DIME"))
            }
          }
        }

        describe ("and a shekel is inserted") {
          subject.insertCoin ("SHEKEL")

          describe ("and the display is examined") {
            val result = subject.display

            it ("says $0.15") {
              assert (result === "$0.15")
            }
          }

          describe ("and the coin return lever is pressed") {
            subject.returnCoins ()

            describe ("and the display is examined") {
              val result = subject.display

              it ("says INSERT COIN") {
                assert (result === "INSERT COIN")
              }
            }

            describe ("and the coin return is examined") {
              val result = subject.harvestCoinReturn ()

              it ("shows NICKEL, DIME, SHEKEL") {
                assertBagsEqual (result, List ("NICKEL", "DIME", "SHEKEL"))
              }
            }
          }

          describe ("and two quarters are inserted") {
            subject.insertCoin ("QUARTER")
            subject.insertCoin ("QUARTER")

            describe ("and the display is examined") {
              val result = subject.display

              it ("says $0.65") {
                assert (result === "$0.65")
              }
            }

            describe ("and the coin return lever is pressed") {
              subject.returnCoins ()

              describe ("and the display is examined") {
                val result = subject.display

                it ("says INSERT COIN") {
                  assert (result === "INSERT COIN")
                }
              }

              describe ("and the coin return is examined") {
                val result = subject.harvestCoinReturn ()

                it ("shows QUARTER, QUARTER, SHEKEL, DIME, NICKEL") {
                  assertBagsEqual (result, List ("QUARTER", "QUARTER", "SHEKEL", "DIME", "NICKEL"))
                }
              }

              describe ("and the coin return lever is pressed") {
                subject.returnCoins ()

                describe ("and the display is examined") {
                  val result = subject.display

                  it ("says INSERT COIN") {
                    assert (result === "INSERT COIN")
                  }
                }

                describe ("and the coin return is examined") {
                  val result = subject.harvestCoinReturn ()

                  it ("shows Nil") {
                    assertBagsEqual (result, Nil)
                  }
                }
              }
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

        describe ("and the display is examined") {
          val result = subject.display

          it ("it still says $0.65") {
            assert (result === "$0.65")
          }
        }

        describe ("and the bin is harvested") {
          val result = subject.harvestBin ()

          it ("there's nothing there") {
            assert (result === Nil)
          }
        }

        describe ("and CHIPS ($0.50) is selected") {
          subject.select (CHIPS)

          describe ("and the bin is harvested") {
            val result = subject.harvestBin ()

            it ("there's a bag of chips") {
              assert (result === List (CHIPS))
            }

            describe ("and the display is examined") {
              val result = subject.display

              it ("says INSERT COIN") {
                assert (result === "INSERT COIN")
              }
            }

            describe ("and the coin return lever is pressed") {
              subject.returnCoins ()

              describe ("and the display is examined") {
                val result = subject.display

                it ("says INSERT COIN") {
                  assert (result === "INSERT COIN")
                }
              }

              describe ("and the coin return is examined") {
                val result = subject.harvestCoinReturn ()

                it ("shows NICKEL, DIME") {
                  assertBagsEqual (result, List ("NICKEL", "DIME"))
                }
              }
            }

            describe ("and $0.50 for CHIPS is inserted") {
              subject.insertCoin ("QUARTER")
              subject.insertCoin ("QUARTER")

              describe ("and CHIPS is selected") {
                subject.select (CHIPS)

                describe ("and the bin is harvested") {
                  val result = subject.harvestBin ()

                  it ("there's nothing there") {
                    assert (result === Nil)
                  }
                }

                describe ("and the display is examined") {
                  val result = subject.display

                  it ("says SOLD OUT") {
                    assert (result === "SOLD OUT")
                  }
                }

                describe ("and the coin return lever is pressed") {
                  subject.returnCoins ()

                  describe ("and the display is examined") {
                    val result = subject.display

                    it ("says INSERT COIN") {
                      assert (result === "INSERT COIN")
                    }
                  }

                  describe ("and the coin return is examined") {
                    val result = subject.harvestCoinReturn ()

                    it ("shows QUARTER, QUARTER") {
                      assertBagsEqual (result, List ("QUARTER", "QUARTER"))
                    }
                  }
                }

                describe ("and after the coin return") {
                  subject.returnCoins()

                  describe ("and the display is examined") {
                    val result = subject.display

                    it ("says INSERT COIN") {
                      assert (result === "INSERT COIN")
                    }
                  }
                }
              }
            }
          }
        }

        describe ("and CANDY is selected") {
          subject.select (CANDY)

          describe ("and the bin is harvested") {
            val result = subject.harvestBin ()

            it ("there's a bar of candy") {
              assert (result === List (CANDY))
            }
          }

          describe ("and the display is examined") {
            val result = subject.display

            it ("says INSERT COIN") {
              assert (result === "INSERT COIN")
            }
          }

          describe ("and the coin return lever is pressed") {
            subject.returnCoins ()

            describe ("and the display is examined") {
              val result = subject.display

              it ("says INSERT COIN") {
                assert (result === "INSERT COIN")
              }
            }

            describe ("and the coin return is examined") {
              val result = subject.harvestCoinReturn ()

              it (s"shows nothing") {
                assertBagsEqual (result, Nil)
              }
            }
          }
        }
      }
    }

    describe ("and three quarters are inserted") {
      subject.insertCoin ("QUARTER")
      subject.insertCoin ("QUARTER")
      subject.insertCoin ("QUARTER")

      describe ("and CANDY is selected") {
        subject.select (CANDY)

        describe ("and the bin is harvested") {
          val result = subject.harvestBin ()

          it ("there's nothing there") {
            assert (result === Nil)
          }
        }

        describe ("and the display is examined") {
          val result = subject.display

          it ("says EXACT CHANGE") {
            assert (result === "EXACT CHANGE")
          }
        }

        describe ("and the coin return lever is pressed") {
          subject.returnCoins ()

          describe ("and the display is examined") {
            val result = subject.display

            it ("says INSERT COIN") {
              assert (result === "INSERT COIN")
            }
          }

          describe ("and the coin return is examined") {
            val result = subject.harvestCoinReturn ()

            it ("shows QUARTER, QUARTER, QUARTER") {
              assertBagsEqual (result, List("QUARTER", "QUARTER", "QUARTER"))
            }
          }
        }
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

          describe ("and the bin is harvested") {
            val result = subject.harvestBin ()

            it ("there's a bar of candy") {
              assert (result === List (CANDY))
            }
          }

          describe ("and the display is examined") {
            val result = subject.display

            it ("says INSERT COIN") {
              assert (result === "INSERT COIN")
            }
          }

          describe ("and the coin return lever is pressed") {
            subject.returnCoins ()

            describe ("and the display is examined") {
              val result = subject.display

              it ("says INSERT COIN") {
                assert (result === "INSERT COIN")
              }
            }

            describe ("and the coin return is examined") {
              val result = subject.harvestCoinReturn ()

              it (s"shows DIME") {
                assertBagsEqual (result, List ("DIME"))
              }
            }
          }
        }
      }
    }
  }
}
