package org.dnwiebe.respectfulrspec

import org.scalatest.path

/**
  * Created by dnwiebe on 4/23/17.
  */
class InventoryTest extends path.FunSpec {
  describe ("Inventory items") {
    val result = Inventory.values ().map {_.name}

    it ("have the expected names") {
      assert (result === Array ("SODA", "CHIPS", "CANDY"))
    }
  }

  describe ("Inventory items") {
    val result = Inventory.values ().map {_.price}

    it ("have the expected prices") {
      assert (result === Array (100, 50, 65))
    }
  }

  describe ("Inventory items") {
    val result = Inventory.values ().map {_.toString}

    it ("have the expected string representations") {
      assert (result === Array ("a bottle of SODA", "a bag of CHIPS", "a bar of CANDY"))
    }
  }
}
