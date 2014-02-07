package models.DataBase

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current

/**
 * Created with IntelliJ IDEA.
 * User: Nicolas
 * Date: 04/01/14
 * Time: 14:31
 * To change this template use File | Settings | File Templates.
 */
case class Node(id: Long, nodeName: String, networkId: Long)

object Node {

  val node = {
    get[Long]("id") ~
      get[String]("nodeName") ~
      get[Long]("networkId") map {
      case id~nodeName~networkId => Node(id, nodeName, networkId)
    }
  }

  def getNodesFromNetwork(networkId: Long): List[Node] = DB.withConnection { implicit c =>
    SQL("select * from node where (networkId) = ({networkId})").on(
      "networkId" -> networkId
    ).as(node *)
  }

  def all(): List[Node] = DB.withConnection { implicit c =>
    SQL("select * from node").as(node *)
  }

  def getNode(nodeName: String, networkId: Long): Node = {
     DB.withConnection { implicit c =>
       val nodes: List[Node] = SQL("select * from node where networkId = {networkId} and nodeName = ({nodeName})").on(
        "networkId" -> networkId,
        "nodeName" -> nodeName
        ).as(node *)

        if(nodes.size == 1){
          nodes.head
        } else {
          null
        }
    }
  }

  def getNode(nodeId: Long):List[Node] = DB.withConnection { implicit c =>
    SQL("select * from node where (id) = ({nodeId})").on(
      "nodeId" -> nodeId
    ).as(node *)
  }

  def create(nodeName: String, networkId: Long) {
    DB.withConnection { implicit c =>
      SQL("insert into node (nodeName, networkId) values ({nodeName}, {networkId})").on(
        'nodeName -> nodeName,
        'networkId -> networkId
      ).executeUpdate()
    }
  }

  def createWithId(id: Long, nodeName: String, networkId: Long) {
    DB.withConnection { implicit c =>
      SQL("insert into node (id,nodeName, networkId) values ({id},{nodeName}, {networkId})").on(
        'id ->id,
        'nodeName -> nodeName,
        'networkId -> networkId
      ).executeUpdate()
    }
  }

  def delete(id: Long) {
    DB.withConnection { implicit c =>
      SQL("delete from node where id = {id}").on(
        'id -> id
      ).executeUpdate()
    }
  }
}