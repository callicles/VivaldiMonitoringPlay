package controllers.REST

import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import models._
import utils.Joda._
import java.math.BigDecimal


/**
 * Created with IntelliJ IDEA.
 * User: Nicolas
 * Date: 08/01/14
 * Time: 10:27
 * To change this template use File | Settings | File Templates.
 */

object nodeRESTController extends Controller {
  def nodes = Action {
    Ok(Json.toJson(Node.all().map{ t =>
      Json.obj("id" ->t.id,"nodeName" ->t.nodeName, "networkId" ->t.networkId)
    }))
  }

  def newNodes = Action(parse.json) {

    implicit val rdNewNode = (
      (__ \ 'nodeName).read[String] and
        (__ \ 'networkId).read[Long]
      ).tupled

    request =>
      request.body.validate[(String, Long)].map{
        case (nodeName, networkId) => {
          Node.create(nodeName,networkId)
          Ok("Node created")
        }
      }.recoverTotal{
        e=> BadRequest("Detected error: "+ JsError.toFlatJson(e))
      }
  }

  def getNodesFromNetwork(id: Long) = Action {
    Ok(Json.toJson(Node.getNodesFromNetwork(id).map{ t =>
      Json.obj("id" ->t.id,"nodeName" ->t.nodeName, "networkId" ->t.networkId)
    }))
  }

  def deleteNodes (id: Long) = Action {
    Node.delete(id)
    Ok("Node Deleted")
  }
}
