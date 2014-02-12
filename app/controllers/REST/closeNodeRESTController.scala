package controllers.REST

import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import models.DataBase._
import utils.Joda._
import utils.utilities.transpose
import java.math.BigDecimal
import org.joda.time.DateTime
import models.DataBase.{SimpleCloseNode, InitTime, CloseNode}
import models.Client.CloseNodeBubble


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

  /**
   * Generates the data structure for the bubble graph
   * @param nodeId Reference to the considered node
   * @param initTimeId reference to the Init time
   * @return Delivers a List[closeNodeBubble] in Json
   */
  def getBubblesFromInitTime(nodeId: Long, initTimeId: Long) = Action{

    val closeNodes = closeNodeFromInitTime(nodeId, initTimeId)

    // Group nodes by logTime to reconstitute close node arrays
    val closeNodesGroupedSorted = closeNodes.groupBy(_.logTime).mapValues(closeNodeList => closeNodeList.sortBy(_.distance))
    // This is the list reference of all distant nodes
    val distantNodeList = closeNodes.groupBy(_.distantNodeId).keys.toArray
    //  In that Object, the first element of the top array represent an array containing all the nodes in the first position, the second etc ...
    val closeNodesGroupedByPosition = transpose(closeNodesGroupedSorted.toList.map(t => t._2))

    var bubbleList = List[CloseNodeBubble]()

    closeNodesGroupedByPosition.foreach(
      nodesInPosition => distantNodeList.foreach(
        distantNodeId => bubbleList = bubbleList:+CloseNodeBubble(distantNodeId,closeNodesGroupedByPosition.indexOf(nodesInPosition),nodesInPosition.count(_.distantNodeId == distantNodeId))
    ))

    /*
    Ok(Json.toJson(bubbleList.map{ b =>
      Json.obj("nodeDistantId" ->b.nodeDistantId,"position" ->b.position, "cardinal" ->b.cardinal)
    }))
    */

    // Format for D3.js
    val bubbleListMapped = bubbleList.groupBy(closeNode => closeNode.nodeDistantId).toArray

    Ok(Json.toJson(bubbleListMapped.map(
      tuple => Json.obj("nodeDistantId" -> tuple._1,"nodeName" -> models.DataBase.Node.getNode(tuple._1).head.nodeName, "bubbles" -> tuple._2.filterNot(_.cardinal == 0).map(
        closeNode => Array(closeNode.position,closeNode.cardinal)
      )))))
  }

  /**
   * This returns a time and a cumulated distance for the current node.
   * @param nodeId reference to the considered node
   * @param initTimeId init Time reference from which the node is active
   * @return a list of Json objects with a time and the corresponding cumulated distance.
   */
  def getDistanceCumulForCloseNodeArray(nodeId: Long, initTimeId: Long) = Action{
    val closeNodes = closeNodeFromInitTime(nodeId, initTimeId)

    val scatterPointsMap = closeNodes.groupBy(_.logTime).mapValues(closeNodeList => {
      var distance: Float = 0
      closeNodeList.foreach(node => distance=distance + node.distance.floatValue())
      distance
    })

    val scatterPointArray = scatterPointsMap.toArray

    Ok(Json.toJson(scatterPointArray.map { n =>
      Json.obj("time" ->n._1,"distanceCumul" ->n._2)
    }))
  }

  def getLastCloseNodes(localNodeId: Long, initTimeId: Long) = Action {
    val closeNodes = closeNodeFromInitTime(localNodeId,initTimeId)
    val closeNodesGrouped = closeNodes.groupBy(_.logTime).toArray
    val closeNodesGroupedSorted = closeNodesGrouped.sortBy(_._1)
    val selectSort = closeNodesGroupedSorted.head._2.sortBy(_.distance)

    Ok(Json.toJson( selectSort.map { c =>
      Json.obj("id" ->c.id,"nodeName" -> models.DataBase.Node.getNode(c.distantNodeId).head.nodeName,"localNodeId" ->c.localNodeId, "distantNodeId" ->c.distantNodeId, "logTime" ->c.logTime, "distance" ->c.distance.floatValue())
    }))
  }

  /**
   * Retrieves close nodes between the init time specified and the next one.
   * @param nodeId Node considered
   * @param initTimeId InitTime reference
   * @return List[CloseNode]
   */
  def closeNodeFromInitTime(nodeId: Long, initTimeId: Long): List[CloseNode] = {
    // Routine to get elements between initializations
    val initTimes = InitTime.getInitTimesFromNode(nodeId).sortBy(_.initTime).toList
    val initTimeRef = initTimes.find(i => i.id == initTimeId).head

    var closeNodes = CloseNode.getCloseNodesFromNode(nodeId).dropWhile((c) => c.logTime.getMillis <= initTimeRef.initTime.getMillis)

    if (initTimes.last.id != initTimeId){
      val postInit = initTimes.dropWhile(c => c.initTime.getMillis <= initTimeRef.initTime.getMillis).head
      closeNodes = closeNodes.takeWhile(c => c.logTime.getMillis < postInit.initTime.getMillis)
    }
    closeNodes
  }

  def deleteCoordinates (id: Long) = Action {
    CloseNode.delete(id)
    Ok
  }
}
