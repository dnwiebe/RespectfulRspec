package org.dnwiebe.respectfulrspec;

/**
 * Created by dnwiebe on 4/23/17.
 */
public enum Coin {
  QUARTER (25),
  DIME (10),
  NICKEL (5);

  public final int value;

  Coin (int value) {
    this.value = value;
  }
}
