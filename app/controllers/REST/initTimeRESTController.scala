package controllers.REST

import play.api.mvc._
import play.api.libs.json._
import models._
import utils.Joda._
import java.math.BigDecimal

/**
 * Created with IntelliJ IDEA.
 * User: Nicolas
 * Date: 08/01/14
 * Time: 10:29
 * To change this template use File | Settings | File Templates.
 */
object initTimeRESTController extends Controller{

  def initTimes = Action {
    Ok(Json.toJson(InitTime.all().map{ t =>
      Json.obj("id" ->t.id,"nodeId" ->t.nodeId, "initTime" ->t.initTime)
    }))
  }

  def getInitTimesFromNode(nodeId: Long)= Action {
    Ok(Json.toJson(InitTime.getInitTimesFromNode(nodeId).map{ t =>
      Json.obj("id" ->t.id,"nodeId" ->t.nodeId, "initTime" ->t.initTime)
    }))
  }

  def newInitTimes = Action(parse.json) {

    implicit val rdNewInitTime = (__ \ 'nodeId).read[Long]

    request =>
      request.body.validate[(Long)].map{
        case (nodeId) => {
          InitTime.create(nodeId)
          Ok
        }
      }.recoverTotal{
        e=> BadRequest("Detected error: "+ JsError.toFlatJson(e))
      }
  }

  def deleteInitTimes (id: Long) = Action {
    InitTime.delete(id)
    Ok
  }
}
