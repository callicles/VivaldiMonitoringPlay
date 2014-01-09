package controllers.REST

import play.api.mvc._
import play.api.libs.json._
import models._


/**
 * Created with IntelliJ IDEA.
 * User: Nicolas
 * Date: 08/01/14
 * Time: 10:25
 * To change this template use File | Settings | File Templates.
 */
object networkRESTController extends Controller{
  def networks = Action {
    Ok(Json.toJson(Network.all().map{ t =>
      Json.obj("id" ->t.id,"networkName" ->t.networkName)
    }))
  }

  def newNetwork = Action(parse.json) {
    implicit val rdNewNetwork = (__ \ 'networkName).read[String]

    request =>
      request.body.validate[(String)].map{
        case (networkName) => {
          Network.create(networkName)
          val network = Network.getNetwork(networkName)
          if (network != null){
            Ok(Json.obj("id" ->network.id,"networkName" ->network.networkName))
          }else{
            NotFound
          }
        }
      }.recoverTotal{
        e=> BadRequest("Detected error: "+ JsError.toFlatJson(e))
      }
  }

  def deleteNetwork (id: Long) = Action {
    Network.delete(id)
    Ok
  }
}
