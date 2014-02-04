package controllers

import play.api._
import play.api.mvc._
import models.DataBase._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("default"))
  }

  def nodeDetail = Action {

    val networks = Network.all();
    val nodes = Node.getNodesFromNetwork(networks.head.id)
    if (!nodes.isEmpty){
      val initTimes = InitTime.getInitTimesFromNode(nodes.head.id)
      Ok(views.html.nodeDetail(networks,nodes,initTimes))
    }else{
      val initTimes = List[InitTime]()
      Ok(views.html.nodeDetail(networks,nodes,initTimes))
    }
  }

}