package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current

import utils.AnormExtension._
import org.joda.time.DateTime
import java.math.BigDecimal

/**
 * Created with IntelliJ IDEA.
 * User: Nicolas
 * Date: 04/01/14
 * Time: 14:31
 * To change this template use File | Settings | File Templates.
 */
case class CloseNode(id: Long, localNodeId: Long, distantNodeId: Long, logTime: DateTime, distance: BigDecimal)

object CloseNode {

  val closeNode = {
    get[Long]("id") ~
      get[Long]("localNodeId") ~
      get[Long]("distantNodeId") ~
      get[DateTime]("logTime") ~
      get[BigDecimal]("distance") map {
      case id~localNodeId~distantNodeId~logTime~distance => CloseNode(id, localNodeId, distantNodeId, logTime, distance)
    }
  }

  def all(): List[CloseNode] = DB.withConnection { implicit c =>
    SQL("select * from closeNode").as(closeNode *)
  }

  def getCloseNodesFromNode(localNodeId: Long): List[CloseNode] = DB.withConnection{ implicit c =>
    SQL("select * from closeNode where localNodeId = {localNodeId}").on(
      "localNodeId" -> localNodeId
    ).as(closeNode *)
  }

  def create(localNodeId: Long, distantNodeId: Long, distance: BigDecimal) {
    DB.withConnection { implicit c =>
      SQL("insert into closeNode (localNodeId,distantNodeId,distance) values ({localNodeId},{distantNodeId},{distance})").on(
        'localNodeId -> localNodeId,
        'distantNodeId -> distantNodeId,
        'distance -> distance
      ).executeUpdate()
    }
  }

  def delete(id: Long) {
    DB.withConnection { implicit c =>
      SQL("delete from closeNode where id = {id}").on(
        'id -> id
      ).executeUpdate()
    }
  }
}

