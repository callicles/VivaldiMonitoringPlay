package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current

import utils.AnormExtension._
import org.joda.time.DateTime

/**
 * Created with IntelliJ IDEA.
 * User: Nicolas
 * Date: 04/01/14
 * Time: 14:31
 * To change this template use File | Settings | File Templates.
 */
case class InitTime(id: Long, nodeId: Long, initTime: DateTime)

object InitTime {

  val initTimeT = {
    get[Long]("id") ~
      get[Long]("nodeId") ~
      get[DateTime]("initTime") map {
      case id~nodeId~initTime => InitTime(id, nodeId, initTime)
    }
  }

  def getInitTimesFromNode(nodeId: Long): List[InitTime] = DB.withConnection { implicit c =>
    SQL("select * from initTime where nodeId = {nodeId}").on(
      "nodeId" -> nodeId
    ).as(initTimeT *)
  }

  def all(): List[InitTime] = DB.withConnection { implicit c =>
    SQL("select * from initTime").as(initTimeT *)
  }

  def create(nodeId: Long) {
    DB.withConnection { implicit c =>
      SQL("insert into initTime (nodeId) values ({nodeId})").on(
        'nodeId -> nodeId
      ).executeUpdate()
    }
  }

  def delete(id: Long) {
    DB.withConnection { implicit c =>
      SQL("delete from initTime where id = {id}").on(
        'id -> id
      ).executeUpdate()
    }
  }
}

