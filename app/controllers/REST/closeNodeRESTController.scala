package controllers.REST

import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import models._
import utils.Joda._
import java.math.BigDecimal
import org.joda.time.DateTime


/**
 * Created with IntelliJ IDEA.
 * User: Nicolas
 * Date: 08/01/14
 * Time: 10:30
 * To change this template use File | Settings | File Templates.
 */
object closeNodeRESTController extends Controller{
  def closeNodes = Action {
    Ok(Json.toJson(CloseNode.all().map{ t =>
      Json.obj("id" ->t.id,"localNodeId" ->t.localNodeId, "distantNodeId" ->t.distantNodeId, "logTime" ->t.logTime, "distance" ->t.distance.floatValue())
    }))
  }

  def newCloseNode = Action(parse.json) {

    implicit val rdNewCloseNode = (
      (__ \ 'localNodeId).read[Long] and
        (__ \ 'distantNodeId).read[Long] and
        (__ \ 'distance).read[BigDecimal]
      ).tupled

    request =>
      request.body.validate[(Long, Long, BigDecimal)].map{
        case (localNodeId, distantNodeId, distance) => {
          CloseNode.create(localNodeId, distantNodeId, distance)
          Ok
        }
      }.recoverTotal{
        e=> BadRequest("Detected error: "+ JsError.toFlatJson(e))
      }
  }

  def newCloseNodesFromArray = Action(parse.json) {

    val logTime = DateTime.now()

    request =>
      request.body.validate[(List[SimpleCloseNode])].map{
        nodes =>{
          nodes.foreach(n => CloseNode.create(n.localNodeId, n.distantNodeId, n.distance, logTime))
          Ok
        }
      }.recoverTotal{
        e=> BadRequest("Detected error: "+ JsError.toFlatJson(e))
      }

  }

  def deleteCoordinates (id: Long) = Action {
    CloseNode.delete(id)
    Ok
  }
}
