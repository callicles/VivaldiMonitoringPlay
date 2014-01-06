package controllers

/**
 * Created with IntelliJ IDEA.
 * User: Nicolas
 * Date: 04/01/14
 * Time: 12:48
 * To change this template use File | Settings | File Templates.
 */


import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import models._
import utils.Joda._

object REST extends Controller {

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
        Ok("Network Created")
      }
    }.recoverTotal{
      e=> BadRequest("Detected error: "+ JsError.toFlatJson(e))
    }
  }

  def deleteNetwork (id: Long) = Action {
    Network.delete(id)
    Ok("Network Deleted")
  }

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
    Node.getNodesFromNetwork(id)
    Ok("Node retrieved")
  }

  def deleteNodes (id: Long) = Action {
    Node.delete(id)
    Ok("Node Deleted")
  }

  def initTimes = Action {
    Ok(Json.toJson(InitTime.all().map{ t =>
      Json.obj("id" ->t.id,"nodeId" ->t.nodeId, "initTime" ->t.initTime)
    }))
  }

  def newInitTimes = Action(parse.json) {

    implicit val rdNewInitTime = (__ \ 'nodeId).read[Long]

    request =>
      request.body.validate[(Long)].map{
        case (nodeId) => {
          InitTime.create(nodeId)
          Ok("InitNode created")
        }
      }.recoverTotal{
        e=> BadRequest("Detected error: "+ JsError.toFlatJson(e))
      }
  }

  def deleteInitTimes (id: Long) = Action {
    InitTime.delete(id)
    Ok("InitTime Deleted")
  }

  def coordinates = Action {
    Ok(Json.toJson(Coordinate.all().map{ t =>
      Json.obj("id" ->t.id,"nodeId" ->t.nodeId, "coordinateTime" ->t.coordinateTime, "x" ->t.x, "y" ->t.y)
    }))
  }

  def newCoordinates = Action(parse.json) {

    implicit val rdNewCoordinate = (
      (__ \ 'nodeId).read[Long] and
      (__ \ 'x).read[Double] and
      (__ \ 'y).read[Double]
      ).tupled

    request =>
      request.body.validate[(Long, Double, Double)].map{
        case (nodeId, x, y) => {
          Coordinate.create(nodeId, x, y)
          Ok("Coordinate created")
        }
      }.recoverTotal{
        e=> BadRequest("Detected error: "+ JsError.toFlatJson(e))
      }
  }

  /**
   * This method retrieves the last 'numberOfCoordinatePerNode' coordinates for each node of the network
   * @param networkId reference
   * @param numberOfCoordinatePerNode number of coordinates to retrieve for each node
   */
  def getLastSavedCoordinatesFromNetwork(networkId: Long, numberOfCoordinatePerNode: Int) = Action {
    val allNetworkCoordinate = Coordinate.getCoordinateFromNetwork(networkId)
    val groupedByNode = allNetworkCoordinate.groupBy(_.nodeId)
    val sortedMap = groupedByNode.mapValues(_.sortBy(_.coordinateTime))
    val listToReturn = sortedMap.mapValues(list => list.take(numberOfCoordinatePerNode)).values.flatten

    Ok(Json.toJson(listToReturn.map{ t =>
      Json.obj("id" ->t.id,"nodeId" ->t.nodeId, "coordinateTime" ->t.coordinateTime, "x" ->t.x, "y" ->t.y)
    }))
  }

  def deleteCoordinates (id: Long) = Action {
    Coordinate.delete(id)
    Ok("Coordinate Deleted")
  }

}
