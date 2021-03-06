package controllers.REST

import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import models._
import utils.Joda._
import java.math.BigDecimal
import scala.xml.Utility
import models.DataBase.Node


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
      Json.obj("id" ->t.id,"nodeName" ->Utility.escape(t.nodeName), "networkId" ->t.networkId)
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
          val node = Node.getNode(nodeName,networkId)
          if (node != null){
            Ok(Json.obj("id" ->node.id,"nodeName" ->Utility.escape(node.nodeName), "networkId" ->node.networkId))
          }else{
            NotFound
          }
        }
      }.recoverTotal{
        e=> BadRequest("Detected error: "+ JsError.toFlatJson(e))
      }
  }

  def newNodesWithId = Action(parse.json) {

    implicit val rdNewNode = (
      (__ \ 'id).read[Long] and
      (__ \ 'nodeName).read[String] and
        (__ \ 'networkId).read[Long]
      ).tupled

    request =>
      request.body.validate[(Long,String, Long)].map{
        case (id,nodeName, networkId) => {
          Node.createWithId(id,nodeName,networkId)
          val node = Node.getNode(nodeName,networkId)
          if (node != null){
            Ok(Json.obj("id" ->node.id,"nodeName" ->Utility.escape(node.nodeName), "networkId" ->node.networkId))
          }else{
            NotFound
          }
        }
      }.recoverTotal{
        e=> BadRequest("Detected error: "+ JsError.toFlatJson(e))
      }
  }

  def getNodesFromNetwork(id: Long) = Action {
    Ok(Json.toJson(Node.getNodesFromNetwork(id).map{ t =>
      Json.obj("id" ->t.id,"nodeName" ->Utility.escape(t.nodeName), "networkId" ->t.networkId)
    }))
  }

  def getNode(networkId: Long, nodeName: String) = Action {
    val node = Node.getNode(nodeName,networkId)
    Ok(Json.obj("id" ->node.id,"nodeName" ->Utility.escape(node.nodeName), "networkId" ->node.networkId))
  }

  def deleteNodes (id: Long) = Action {
    Node.delete(id)
    Ok
  }
}
