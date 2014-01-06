package models

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
case class Network(id: Long, networkName: String)

object Network {

  val network = {
    get[Long]("id") ~
      get[String]("networkName") map {
      case id~networkName => Network(id, networkName)
    }
  }

  def all(): List[Network] = DB.withConnection { implicit c =>
    SQL("select * from network").as(network *)
  }

  def create(networkName: String) {
    DB.withConnection { implicit c =>
      SQL("insert into network (networkName) values ({networkName})").on(
        'networkName -> networkName
      ).executeUpdate()
    }
  }

  def delete(id: Long) {
    DB.withConnection { implicit c =>
      SQL("delete from network where id = {id}").on(
        'id -> id
      ).executeUpdate()
    }
  }
}

