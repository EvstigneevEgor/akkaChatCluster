package org.example.view

class Contacts(adr: String, name: String, me: Boolean)/* extends Ordered[Contacts]*/ {
  val address: String = adr
  var Name: String = name
  var itsMe: Boolean = me

  def getName = Name

  //def setName = Name
  def getAdress = address



   def <(that: Contacts): Boolean = {
    if (itsMe && !that.itsMe)
      true
    else if (Name < that.Name)
      true
    else
      false
  }

  override def toString: String = {
    if(this != null)
    "address = " + address + " name = " + Name
    else
      "null"
  }

   def compare(that: Contacts): Boolean = {
    if (itsMe && !that.itsMe) {
      true
    }else {
      Name < that.Name
    }

  }}
