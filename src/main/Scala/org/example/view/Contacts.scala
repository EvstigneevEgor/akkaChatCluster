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
    if(that.Name=="\u041e\u0431\u0449\u0438\u0439\u0020\u0447\u0430\u0442")
     if (itsMe && !that.itsMe) {
      true
    }else {
      Name < that.Name
    }
    else
      false
  }}
