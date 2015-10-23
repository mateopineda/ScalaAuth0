package controllers

import play.api.mvc._
import play.api.libs.json.{Writes, Json, JsValue}

import com.github.nscala_time.time._
import com.github.nscala_time.time.Imports._
//import play.api.libs.ws.WS
//import scala.concurrent.Future

import models.Product
import models.User
import models.Movement

import models.User

object Application extends Controller {

  // Datos para HU 4

  var mov1 = new Movement(1,"16/10/2015",500.34,"Retiro")
  var mov2 = new Movement(2,"16/10/2015",1500,"Creación")
  var mov3 = new Movement(3,"16/10/2015",5500,"Cierre")


  var producto1 = new Product("CDT",1,200.25,List(mov1,mov2))
  var producto2 = new Product("Debito",2,2000000,List(mov2))
  var producto3 = new Product("Credito",3,1500000,List(mov3))

  var user1 = new User(1,1017224184,"Alexis Rodriguez",List(producto2,producto1))
  var user2 = new User(3,123456,"Emmanuel Velez",List(producto3,producto1,producto2))
  var user3 = new User(4,456789,"Lixander Cadavid",List(producto2))

  val error : JsValue = Json.parse("""{"error":404, " description": "not found"}""")

  var usuarios = List(user1, user2, user3);

//    //  Datos de prueba para detalle de producto
//  var productName = "credito";
//  var listDetail = "[{\"id\":1,\"name\":\"CDT\",\"productType\":\"Preferencial\",\"createdAt\":" +
//    "\"2015-10-14T21:25:41.456Z\",\"updatedAt\":\"2015-10-14T21:25:41.456Z\",\"CustomerProduct\":" +
//    "{\"balance\":\"100.00\",\"createdAt\":\"2015-10-14T21:25:41.525Z\",\"updatedAt\":\"2015-10-14T21:25:41.525Z\"," +
//    "\"ProductId\":1,\"CustomerId\":1}}]"

  //  Return User's products based on id and types of document HU 4
  def productsUser(typeDocument: Int, idUser: Int) = Action {
    // Query a la base de datos SQL

    //Filtra el Usuario el cual su número de identificación y tipo de identificación concida con las
    // ingresadas en la función
     val pr = usuarios.filter(usuario =>  usuario.documentType == typeDocument && usuario.documentNumber == idUser)

    // Ok(views.html.home("Tipo Documento: "+pr.head.documentType+ "Id: "+pr.head.firstName))
    if(pr.size==0) {

      Ok(error)
    }else {
      Ok(pr.head.toJsonNoMoves())
    }


//    Ok(user1.toJsonNoMoves())//false sin moviemientos

  }


  def detailProductUser(idUser: Int,typeDocument: Int, productId: Int) = Action {
//    // Consumo del API del back-end-legacy
//    // EJ de URL: https://spinnerbank-api-legacy.herokuapp.com/api/v1/customers/1/products
//
    val pr = usuarios.filter(usuario =>  usuario.documentType == typeDocument  && usuario.documentNumber == idUser)
     pr.head.products = pr.head.products.filter( prd => prd.productType==productId)
    if(pr.size==0) {

      Ok(error)
    }else {
      Ok(pr.head.toJson())
    }
  }
}
