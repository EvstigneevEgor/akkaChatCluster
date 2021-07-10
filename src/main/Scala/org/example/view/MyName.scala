package org.example.view

case class MyName(memberAdres: String, num : String) {}
case class GetName()
case class SetName(name:String)

case class GetAdress()
case class GetAdressName()
case class Rename(oldName:String,newName:String)
