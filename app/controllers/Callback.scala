// controllers/Callback.scala
object Callback extends Controller {

  // callback route
  def callback(codeOpt: Option[String] = None, stateOpt: Option[String] = None) = Action.async {
    (for {
      code <- codeOpt
      state <- stateOpt
    } yield {
      // Get the token
      getToken(code).flatMap { case (idToken, accessToken) =>
       // Get the user
       getUser(accessToken).map { user =>
          // Cache the user and tokens into cache and session respectively
          Cache.set(idToken+ "profile", user)
          Redirect(routes.User.index())
            .withSession(
              "idToken" -> idToken,
              "accessToken" -> accessToken
            )
      }

      }.recover {
        case ex: IllegalStateException => Unauthorized(ex.getMessage)
      }
    }).getOrElse(Future.successful(BadRequest("No parameters supplied")))
  }

  def getToken(code: String): Future[(String, String)] = {
    val tokenResponse = WS.url(String.format("https://%s/oauth/token", "YOUR_NAMESPACE"))(Play.current).
      withHeaders(HeaderNames.ACCEPT -> MimeTypes.JSON).
      post(
        Json.obj(
          "client_id" -> "YOUR_CLIENT_ID",
          "client_secret" -> "YOUR_CLIENT_SECRET",
          "redirect_uri" -> "http://localhost:9000/callback",
          "code" -> code,
          "grant_type"-> "authorization_code"
        )
      )

    tokenResponse.flatMap { response =>
      (for {
        idToken <- (response.json \ "id_token").asOpt[String]
        accessToken <- (response.json \ "access_token").asOpt[String]
      } yield {
        Future.successful((idToken, accessToken))
      }).getOrElse(Future.failed[(String, String)](/new IllegalStateException("Tokens not sent")))
    }

  }

  def getUser(accessToken: String): Future[JsValue] = {
    val config = Auth0Config.get()
    val userResponse = WS.url(String.format("https://%s/userinfo", config.domain))(Play.current)
      .withQueryString("access_token" -> accessToken)
      .get()

    userResponse.flatMap(response => Future.successful(response.json))
  }
}