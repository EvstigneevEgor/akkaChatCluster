package org.example.view

import org.example.CborSerializable

case class Contacts(adr: String, var name: String, me: Boolean) extends CborSerializable {
  private val address: String = adr
  private val itsMe: Boolean = me

  def getName: String = name

  def setName(s: String): Unit = name = s

  def <(that: Contacts): Boolean = {
    if (itsMe && !that.itsMe)
      true
    else if (name < that.name)
      true
    else
      false
  }

  override def toString: String = {
    if (this != null)
      "address = " + address + " name = " + name
    else
      "null"
  }

  def compare(that: Contacts): Boolean = {
    if (that.name == "Общий чат")
      if (itsMe && !that.itsMe) {
        true
      } else {
        name < that.name
      }
    else
      false
  }
}
