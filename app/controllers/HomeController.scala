package controllers

import akka.http.scaladsl.model.DateTime

import javax.inject._
import play.api.mvc._
import models.post

import scala.collection.mutable.ListBuffer

@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  def homePage(): Action[AnyContent] = Action { implicit _: Request[AnyContent] =>
    Ok(views.html.HomePage())
  }

  def signIn(username: String, password: String): Action[AnyContent] = Action {
    val users = models.user.users.toList
    val isValidUser = users.exists(user => user.username == username && user.password == password)

    if (isValidUser) {
      Redirect(routes.HomeController.userHome).withSession("username" -> username)
    } else {
      Redirect(routes.HomeController.homePage)
    }
  }

  def signUp(username: String, password: String): Action[AnyContent] = Action {
    val users = models.user.users.toList
    val userExists = users.exists(_.username == username)

    if (!userExists) {
      val newUser = models.user(username, password, ListBuffer())
      models.user.addUser(newUser)
      Redirect(routes.HomeController.userHome).withSession("username" -> username)
    } else {
      Redirect(routes.HomeController.homePage)
    }
  }


  def userHome: Action[AnyContent] = Action { implicit request =>
    val usernameOption = request.session.get("username")
    usernameOption.map { _ =>
      val posts = post.findAllSortedByDate
      Ok(views.html.UserHome(posts))
    } getOrElse(Redirect(routes.HomeController.homePage))
  }

  def addPost(): Action[AnyContent] = Action {
    Ok(views.html.AddPost())
  }
  def addPost(title: String, textPart: String, codePart: String, tags: List[String]): Action[AnyContent] = Action { implicit request =>
    val username = request.session.get("username")
    val tagsBuffer = ListBuffer(tags: _*)

    username.map { username =>

      post.addPost(post(post.findAllSortedByDate.length+1,
                        username,
                        DateTime.now,
                        title,
                        textPart,
                        codePart,
                        ListBuffer(),
                        0, 0,
                        tagsBuffer))

      Redirect(routes.HomeController.userHome)

    }getOrElse(Redirect(routes.HomeController.userHome))
  }


  def addAnswer(textPart: String, codePart: String) : Action[AnyContent] = Action { implicit request =>
    val postID = request.session.get("postID")
    val username = request.session.get("username").toList.head
    postID.map { postID =>
      val p = post.findAllSortedByDate.toList
      Ok(views.html.AddAnswer(textPart, codePart))
    }getOrElse(Redirect(routes.HomeController.userHome))
  }


  def logout(): Action[AnyContent] = Action {
    Redirect(routes.HomeController.homePage).withNewSession
  }


  def postDetails(id: Int): Action[AnyContent] = Action {
    Redirect(routes.HomeController.homePage).withNewSession
  }
}
