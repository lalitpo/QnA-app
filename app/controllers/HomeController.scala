package controllers

import akka.http.scaladsl.model.DateTime

import javax.inject._
import play.api._
import play.api.mvc._
import models.post
import models.post.datenow

import scala.collection.mutable.ListBuffer
import scala.util.control.Breaks.break

@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  def homePage(): Action[AnyContent] = Action { implicit _: Request[AnyContent] =>
    Ok(views.html.homePage())
  }

  def userHome: Action[AnyContent] = Action { implicit request =>
    val usernameOption = request.session.get("username")
    usernameOption.map { _ =>
      val posts = post.findAllSortedByDate
      var posts2 = post.findNotComments
      val users = models.user.findAllUsers
      var returnList : List[String] = List()
      users.foreach{ X =>
        returnList = returnList :+ X.username
      }
      posts.foreach{ X =>
        returnList = returnList :+ X.hashtag
      }
      Ok(views.html.home(posts2, returnList.distinct))
    } getOrElse(Redirect(routes.HomeController.homePage))
  }

  def validateComment(postID: String): Action[AnyContent] = Action { implicit request =>
    Redirect(routes.HomeController.comments).withSession(request.session + ("postID" -> postID))
  }

  def comments: Action[AnyContent] = Action { implicit request =>
    val postID = request.session.get("postID")
    postID.map { postID =>
      val posts = post.findAllSortedByDate
      var posts2 = post.findComments
      var posts3: List[models.post] = List()
      posts.foreach { X =>
        if (X.id.toString == postID) {
          X.comments.foreach { Z =>
            posts2.foreach { Y =>
              if(Y.id == Z) {
                posts3 = posts3 :+ Y
              }
            }
          }
        }
      }
      val users = models.user.findAllUsers
      var returnList : List[String] = List()
      users.foreach{ X =>
        returnList = returnList :+ X.username
      }
      posts.foreach{ X =>
        returnList = returnList :+ X.hashtag
      }
      Ok(views.html.comments(posts3, returnList.distinct))
    } getOrElse(Redirect(routes.HomeController.userHome).withNewSession)
  }

  def validateLike(postID: String): Action[AnyContent] = Action {
    Redirect(routes.HomeController.like).withSession("postID" -> postID)
  }

  def like: Action[AnyContent] = Action { implicit request =>
    val postID = request.session.get("postID")
    postID.map { postID =>
      var posts = post.findNotComments.toList
      var index = -1
      posts.foreach { x =>
        if(x.id.toString == postID){
          x.likes = x.likes + 1
        }
      }
      val users = models.user.findAllUsers
      var returnList : List[String] = List()
      users.foreach{ X =>
        returnList = returnList :+ X.username
      }
      posts.foreach{ X =>
        returnList = returnList :+ X.hashtag
      }
      Ok(views.html.home(posts, returnList))
    } getOrElse(Redirect(routes.HomeController.userHome).withNewSession)
  }
  def addPost(): Action[AnyContent] = Action {
    Ok(views.html.AddPost())
  }
  def createPost(text: String, hashtag: String, img: String): Action[AnyContent] = Action { implicit request =>
    val username = request.session.get("username")
    username.map { username =>
      post.createPost(post(post.findAllSortedByDate.length+1,
        username, DateTime.now, text, ListBuffer(), 0, 0, hashtag, 0, img))
      Redirect(routes.HomeController.userHome)
    }getOrElse(Redirect(routes.HomeController.userHome))
  }

  def removePost(PostID: String): Action[AnyContent] = Action {
    val PostForRemove = post.findAllSortedByDate.find(_.id.toString == PostID).toList.head
    post.removePost(PostForRemove)
    PostForRemove.comments.foreach(x => post.removePost(post.findComments.find(_.id == x).toList.head))
    if (PostForRemove.commentOrNot == 1){
      val posts = post.findNotComments
      posts.foreach { X =>
        var xx = X
        var i = 0
        while (i<xx.comments.length) {
          if (xx.comments(i).toString == PostID){
            post.removePost(X)
            post.createPost(post(X.id,
              X.username, X.date, X.text, X.comments -= xx.comments(i), X.likes, X.share, X.hashtag, X.commentOrNot, X.img))
          }
          i = i + 1
        }
      }
    }
    Redirect(routes.HomeController.userHome)
  }

  def goToSharingPageComment: Action[AnyContent] = Action { implicit request =>
    val postID = request.session.get("postID")
    val username = request.session.get("username").toList.head
    postID.map { postID =>
      val p = post.findNotComments.find(_.id.toString == postID).toList
      Ok(views.html.NewComment(username, postID))
    }getOrElse(Redirect(routes.HomeController.comments))
  }

  def createComment(text: String, hashtag: String, username: String, postID: String): Action[AnyContent] = Action { implicit request =>
    var numberOfComments = post.findAllSortedByDate.length+1
    post.createPost(post(numberOfComments, username, DateTime.now, text, ListBuffer(), 0, 0, hashtag, 1, "NO IMAGE"))
    val posts1 = post.findNotComments
    posts1.foreach { X =>
      if (X.id.toString == postID){
        X.comments = X.comments :+ numberOfComments
      }
    }
    Redirect(routes.HomeController.comments)
  }

  def Share(postID: String): Action[AnyContent] = Action { implicit request =>
    val POST = post.findAllSortedByDate.find(_.id.toString == postID).toList.head
    POST.share = POST.share + 1
    val username = POST.username
    Redirect(routes.HomeController.createPost(s"Shared from @$username | " + POST.text, POST.hashtag, POST.img))
  }

  def signIn(username: String, password:String): Action[AnyContent] = Action {
    val users = models.user.users.toList
    var T: Int = 0
    users.foreach{ X =>
      if (X.username == username) {
        if (X.password == password) {
          T = T + 1
        }
      }
    }
    if (T>0){
      Redirect(routes.HomeController.userHome).withSession("username" -> username)
    }else{
      Redirect(routes.HomeController.homePage)
    }
  }

  def signUp(username: String, password:String): Action[AnyContent] = Action {
    val users = models.user.users.toList
    var T: Int = 0
    users.foreach{ X =>
      if (X.username == username) {
        T = T + 1
      }
    }
    if (T==0){
      models.user.addUser(models.user(username, password, ListBuffer()))
      Redirect(routes.HomeController.userHome).withSession("username" -> username)
    }
    else{
      Redirect(routes.HomeController.homePage)
    }
  }



  def logout(): Action[AnyContent] = Action {
    Redirect(routes.HomeController.homePage).withNewSession
  }

  def Profile(hashtagOrUsername: String): Action[AnyContent] = Action { implicit request =>
    var IsHashtage = 0
    val usernameOption = request.session.get("username")
    var returnList : List[String] = List()
    val posts = post.findAllSortedByDate
    val users = models.user.findAllUsers
    users.foreach{ X =>
      returnList = returnList :+ X.username
    }
    posts.foreach{ X =>
      returnList = returnList :+ X.hashtag
    }
    usernameOption.map { username =>
      if (hashtagOrUsername.charAt(0) == '#'){
        IsHashtage = 1
      }
      if (IsHashtage == 0){
        val posts2 = post.findAllSortedByDate.filterNot(_.username != hashtagOrUsername).toList
        Ok(views.html.tweetsOfAUserOrHashtag(posts2, returnList.distinct))
      }
      else{
        val posts2 = post.findAllSortedByDate.filterNot(_.hashtag != hashtagOrUsername).toList
        Ok(views.html.tweetsOfAUserOrHashtag(posts2, returnList.distinct))
      }
    } getOrElse(Redirect(routes.HomeController.userHome))
  }

  def MyProfile(hashtagOrUsername: String): Action[AnyContent] = Action { implicit request =>
    var IsHashtage = 0
    val usernameOption = request.session.get("username")
    var returnList : List[String] = List()
    val posts = post.findAllSortedByDate
    val users = models.user.findAllUsers
    users.foreach{ X =>
      returnList = returnList :+ X.username
    }
    posts.foreach{ X =>
      returnList = returnList :+ X.hashtag
    }
    usernameOption.map { username =>
      if (hashtagOrUsername.charAt(0) == '#'){
        IsHashtage = 1
      }
      if (IsHashtage == 0){
        val posts2 = post.findAllSortedByDate.filterNot(_.username != hashtagOrUsername).toList
        Ok(views.html.MyProfile(posts2, returnList.distinct, users))
      }
      else{
        val posts2 = post.findAllSortedByDate.filterNot(_.hashtag != hashtagOrUsername).toList
        Ok(views.html.MyProfile(posts2, returnList.distinct, users))
      }
    } getOrElse(Redirect(routes.HomeController.userHome))
  }

  def Follow(username: String, Username: String): Action[AnyContent] = Action { implicit request =>
//    val Username = request.session.get("username").toList(0)
    var users = models.user.findAllUsers
    users.foreach { X =>
      if (Username == X.username){
        var i=0
        while(X.friends.length > i){
          if (X.friends(i) == username){
            break
          }
          i = i + 1
        }
        if(i==X.friends.length){
          models.user.deleteUser(X)
          models.user.addUser(models.user(X.username, X.password, X.friends :+ username))
          Redirect(routes.HomeController.MyProfile(Username))
        }
      }
    }
    Redirect(routes.HomeController.userHome)
  }

  def Explore: Action[AnyContent] = Action { implicit request =>
    val usernameOption = request.session.get("username")
    usernameOption.map { username =>
      val posts = post.findAllSortedByDate
      var posts2: List[post] = List()
      val users = models.user.findAllUsers
      val USER = users.find(_.username == username).toList.head
      posts.foreach { X =>
        var i = 0
        while (i < USER.friends.length){
          posts.find(_.username == USER.friends(i)).toList.foreach{ x =>
            posts2 = posts2 :+ x
          }
          i = i + 1
        }
      }
      var returnList : List[String] = List()
      users.foreach{ X =>
        returnList = returnList :+ X.username
      }
      posts.foreach{ X =>
        returnList = returnList :+ X.hashtag
      }
      Ok(views.html.explore(posts2.distinct, returnList.distinct))
    } getOrElse(Redirect(routes.HomeController.homePage))

  }
}
