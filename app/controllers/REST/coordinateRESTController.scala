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
 * Time: 10:30
 * To change this template use File | Settings | File Templates.
 */
object coordinateRESTController extends Controller{
  def coordinates = Action {
    Ok(Json.toJson(Coordinate.all().map{ t =>
      Json.obj("id" ->t.id,"nodeId" ->t.nodeId, "coordinateTime" ->t.coordinateTime, "x" ->t.x.floatValue(), "y" ->t.y.floatValue())
    }))
  }

  def newCoordinates = Action(parse.json) {

    implicit val rdNewCoordinate = (
      (__ \ 'nodeId).read[Long] and
        (__ \ 'x).read[BigDecimal] and
        (__ \ 'y).read[BigDecimal]
      ).tupled

    request =>
      request.body.validate[(Long, BigDecimal, BigDecimal)].map{
        case (nodeId, x, y) => {
          Coordinate.create(nodeId, x, y)
          Ok
        }
      }.recoverTotal{
        e=> BadRequest("Detected error: "+ JsError.toFlatJson(e))
      }
  }

  def deleteCoordinates (id: Long) = Action {
    Coordinate.delete(id)
    Ok
  }

  /**
   * This method retrieves the last 'numberOfCoordinatePerNode' coordinates for each node of the network.
   * @param networkId reference
   * @param numberOfCoordinatePerNode number of coordinates to retrieve for each node
   * @return It returns it as a JSON object consisting in an array of array. Coordinates are grouped by nodes
   */
  def getLastSavedCoordinatesFromNetwork(networkId: Long, numberOfCoordinatePerNode: Int) = Action {
    val allNetworkCoordinate = Coordinate.getCoordinatesFromNetwork(networkId)
    val groupedByNode = allNetworkCoordinate.groupBy(_.nodeId)
    val sortedMap = groupedByNode.mapValues(_.sortBy(_.coordinateTime).reverse)
    val listToReturn = sortedMap.mapValues(list => list.take(numberOfCoordinatePerNode)).values

    Ok(Json.toJson(listToReturn.map{ l =>
      Json.toJson(l.map{t =>
        Json.obj("id" ->t.id,"nodeId" ->t.nodeId, "coordinateTime" ->t.coordinateTime, "x" ->t.x.floatValue(), "y" ->t.y.floatValue())})
    }))
  }

  /**
   * This method retrieves all the coordinates from a node since the Initialization until the following
   * initialization.
   * @param nodeId node reference
   * @param initTimeId initTime reference
   * @return a list of coordinates in JSON
   */
  def getNodesCoordinatesFromInitTime(nodeId: Long, initTimeId: Long) = Action{
    val initTimes = InitTime.getInitTimesFromNode(nodeId).sortBy(_.initTime).toList
    val initTimeRef = initTimes.find(i => i.id == initTimeId).head

    val coordinates = Coordinate.getCoordinatesFromNode(nodeId).dropWhile((c) => (c.coordinateTime.getMillis <= initTimeRef.initTime.getMillis))
    val inter =  coordinates.sortBy(_.coordinateTime).toList

    if (initTimes.last.id == initTimeId){
      Ok(Json.toJson(inter.map{ t =>
        Json.obj("id" ->t.id,"nodeId" ->t.nodeId, "coordinateTime" ->t.coordinateTime, "x" ->t.x.floatValue(), "y" ->t.y.floatValue())
      }))
    } else {
      val postInit = initTimes.dropWhile(c => (c.initTime.getMillis <= initTimeRef.initTime.getMillis)).head
      val cutCoordinates = inter.takeWhile(c => (c.coordinateTime.getMillis < postInit.initTime.getMillis))

      Ok(Json.toJson(cutCoordinates.map{ t =>
        Json.obj("id" ->t.id,"nodeId" ->t.nodeId, "coordinateTime" ->t.coordinateTime, "x" ->t.x.floatValue(), "y" ->t.y.floatValue())
      }))
    }
  }
}
