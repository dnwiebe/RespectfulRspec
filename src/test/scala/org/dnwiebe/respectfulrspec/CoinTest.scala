package org.dnwiebe.respectfulrspec

import java.util

import org.scalatest.path

import scala.collection.JavaConverters._

/**
  * Created by dnwiebe on 4/23/17.
  */
class CoinTest extends path.FunSpec {

  describe ("Coin values") {
    val result = Coin.values ().map {_.value}

    it ("are as expected") {
      assert (result === Array (25, 10, 5))
    }
  }

  describe ("Coin names") {
    val result = Coin.values ().map {_.name ()}

    it ("are as expected") {
      assert (result === Array ("QUARTER", "DIME", "NICKEL"))
    }
  }
}
