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
case class Coordinate(id: Long, nodeId: Long, coordinateTime: DateTime, x: Double, y: Double)

object Coordinate {

  val coordinate = {
    get[Long]("id") ~
    get[Long]("nodeId") ~
    get[DateTime]("coordinateTime") ~
    get[Double]("x") ~
    get[Double]("y") map {
      case id~nodeId~coordinateTime~x~y => Coordinate(id, nodeId, coordinateTime, x, y)
    }
  }

  def getCoordinateFromNetwork(networkId: Long): List[Coordinate] = DB.withConnection { implicit c =>
    SQL("select (coordinate.id,coordinate.nodeId,coordinate.coordinateTime,coordinate.x,coordinate.y) from coordinate" +
      " join node on coordinate.nodeId = node.id" +
      " where (node.networkId) = ({networkId})").as(coordinate *)
  }

  def all(): List[Coordinate] = DB.withConnection { implicit c =>
    SQL("select * from coordinate").as(coordinate *)
  }

  def create(nodeId: Long, x: Double, y: Double) {
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

