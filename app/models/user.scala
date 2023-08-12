package models

import scala.collection.mutable.ListBuffer
import collection.mutable

case class user(username: String, password: String, friends: ListBuffer[String])

object user {

  var users: mutable.Set[user] = mutable.Set(
    user("lalitpo", "9764", ListBuffer()),
    user("lalitpod", "6406", ListBuffer()),
    user("lalitpod09", "6735", ListBuffer()),
    user("paulina", "9764", ListBuffer()),
    user("paulinakkf", "6406", ListBuffer()),
    user("sprintie", "6735", ListBuffer()),
    user("alex", "6735", ListBuffer())
  )

  def findAllUsers: List[user] = users.toList

  def addUser(User: user): Unit = {
    users = users + User
  }

  def deleteUser(User: user): Unit = {
    users = users - User
  }
}
