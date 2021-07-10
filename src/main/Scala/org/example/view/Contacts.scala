package org.example.view

class Contacts(adr: String, name: String, me: Boolean) extends Ordered[Contacts] {
  val address: String = adr
  var Name: String = name
  var itsMe: Boolean = me

  def getName = Name

  //def setName = Name
  def getAdress = address

  override def equals(obj: Any): Boolean = {
    var obj1 = obj.asInstanceOf[Contacts]
    println("{{{")
    println(obj1.toString)
    println(toString)
    println("}}}")
    if (address.equals(obj1.address) && Name.equals(obj1.Name))
      true
    else
      true

  }

  override def <(that: Contacts): Boolean = {
    if (itsMe && !that.itsMe)
      true
    else if (Name < that.Name)
      true
    else
      false
  }

  override def toString: String = {
    "address = " + address + " name = " + Name
  }

  override def compare(that: Contacts): Int = {
    if (itsMe && !that.itsMe) {
      Int.MaxValue
    }else {
      Name.compare(that.Name)
    }

  }}
