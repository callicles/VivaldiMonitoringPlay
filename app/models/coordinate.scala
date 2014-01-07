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
case class Coordinate(id: Long, nodeId: Long, coordinateTime: DateTime, x: BigDecimal, y: BigDecimal)

object Coordinate {

  val coordinateD = {
    get[Long]("coordinate.id") ~
    get[Long]("coordinate.nodeId") ~
    get[DateTime]("coordinate.coordinateTime") ~
    get[BigDecimal]("coordinate.x") ~
    get[BigDecimal]("coordinate.y") map {
      case id~nodeId~coordinateTime~x~y => Coordinate(id, nodeId, coordinateTime, x, y)
    }
  }

  def getCoordinateFromNetwork(networkId: Long): List[Coordinate] = DB.withConnection { implicit c =>
    SQL(
      """
        Select d.id,d.nodeId,d.coordinateTime,d.x,d.y from coordinate as d
        join node as n on n.id = d.nodeId
        where n.networkId = {networkId}
      """
    ).on(
      "networkId" -> networkId
    ).as(coordinateD *)
  }

  val coordinate = {
    get[Long]("id") ~
      get[Long]("nodeId") ~
      get[DateTime]("coordinateTime") ~
      get[BigDecimal]("x") ~
      get[BigDecimal]("y") map {
      case id~nodeId~coordinateTime~x~y => Coordinate(id, nodeId, coordinateTime, x, y)
    }
  }

  def all(): List[Coordinate] = DB.withConnection { implicit c =>
    SQL("select * from coordinate").as(coordinate *)
  }

  def create(nodeId: Long, x: BigDecimal, y: BigDecimal) {
    DB.withConnection { implicit c =>
      SQL("insert into coordinate (nodeId,x,y) values ({nodeId},{x},{y})").on(
        'nodeId -> nodeId,
        'x -> x,
        'y -> y
      ).executeUpdate()
    }
  }

  def delete(id: Long) {
    DB.withConnection { implicit c =>
      SQL("delete from coordinate where id = {id}").on(
        'id -> id
      ).executeUpdate()
    }
  }
}

