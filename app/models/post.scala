package models

import akka.http.scaladsl.model.DateTime
import models.user.users

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.DurationInt

case class post(postId: Int,
                username: String,
                date: DateTime,
                title: String,
                textPart: String,
                codePart: String,
                var answers: ListBuffer[String],
                var likes: Int,
                var dislikes: Int,
                tags: ListBuffer[String])

object post {

  val datenow: DateTime = DateTime.now

  var posts: Set[post] = Set(
    post(1, "lalitpo", datenow.-(100229000), "Issue 1", "Issue in Printing 1", "println(\\\"Hello, Scala!\\\")",
      ListBuffer("This is the first answer.", "Here's another answer."), 9, 0, ListBuffer("beautifulsoup", "scala")),
    post(2, "lalitpod", datenow.-(2423422052L), "Issue 2", "Issue in Printing 2", "println(\\\"Hello, Scala!\\\")",
      ListBuffer("This is the second answer.", "Here's another answer."), 6, 0, ListBuffer("programming", "scala")),
    post(3, "lalitpod09", datenow.-(425254292), "Issue 3", "Issue in Printing 3", "println(\\\"Hello, Scala!\\\")",
      ListBuffer("This is the third answer.", "Here's another answer."), 10, 2, ListBuffer("R", "jenkins")),
    post(4, "achintjn", datenow.-(76254233), "Issue 4", "Issue in Printing 4", "println(\\\"Hello, Scala!\\\")",
      ListBuffer("This is the fourth answer.", "Here's another answer."), 5, 0, ListBuffer("selenium", "scala")),
    post(5, "alex", datenow.-(99225338), "Issue 5", "Issue in Printing 5", "println(\\\"Hello, Scala!\\\")",
      ListBuffer("This is the fifth answer.", "Here's another answer."), 4, 1, ListBuffer("python","bootstrap")),
    post(6, "paulina", datenow.-(99225438), "Issue 6", "Issue in Printing 6", "println(\\\"Hello, Scala!\\\")",
      ListBuffer("This is the sixth answer.", "Here's another answer."), 8, 0, ListBuffer("webscraping", "python")))

  def findAllSortedByDate: List[post] = posts.toList.sortBy(_.date)(Ordering[DateTime].reverse)

  def addPost(newPost: post): Unit = {
    posts = posts + newPost
  }
  def deletePost(Post: post): Unit ={
    posts = posts - Post
  }
}