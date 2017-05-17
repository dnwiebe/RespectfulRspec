package org.dnwiebe.respectfulrspec.utils

import org.scalatest.path

/**
  * Created by dnwiebe on 4/24/17.
  */
object TestUtils extends path.FunSpec {

  def assertBagsEqual[T] (actual: List[T], expected: List[T]): Unit = {
    val discrepancies: List[(T, Int, Int)] = expected.foldLeft (List[(T, Int, Int)] ()) {(soFar, elem) =>
      val actualCount = actual.count {_ == elem}
      val expectedCount = expected.count {_ == elem}
      actualCount match {
        case c if c == expectedCount => soFar
        case _ => (elem, actualCount, expectedCount) :: soFar
      }
    }
    val messages = discrepancies.map {triple =>
      val (value, actualCount, expectedCount) = triple
      s"Value '$value' should have appeared $expectedCount times, but was seen $actualCount times"
    }
    assert (messages === Nil)
  }
}
