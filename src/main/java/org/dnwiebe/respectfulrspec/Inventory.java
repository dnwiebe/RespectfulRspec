package org.dnwiebe.respectfulrspec;

/**
 * Created by dnwiebe on 4/23/17.
 */
public enum Inventory {
  SODA (100, "a bottle of"),
  CHIPS (50, "a bag of"),
  CANDY (65, "a bar of");

  public final int price;
  public final String container;

  Inventory (int price, String container) {
    this.price = price;
    this.container = container;
  }

  @Override
  public String toString () {
    return container + " " + name ();
  }
}
