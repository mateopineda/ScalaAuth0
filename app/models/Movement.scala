package models

import play.api.libs.json.Json
/**
 * Created by alexis on 16/10/15.
 */
case class Movement(
                id: Int,
                dateMovement: String,
                valueMovement: Double,
                description: String
                ) {

  def toJson() = Json.obj(
    "dateMovement" -> this.dateMovement,
    "value" -> this.valueMovement,
    "description" -> this.description

  )
  

}

