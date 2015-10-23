// controllers/User.scala
def index = AuthenticatedAction { request =>
  val idToken = request.session.get("idToken").get
  val profile = Cache.getAs[JsValue](idToken + "profile").get
  Ok(views.html.user(profile))
}