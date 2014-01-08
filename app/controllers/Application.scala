package controllers

import play.api._
import play.api.mvc._
import models._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("default"))
  }

  def nodeDetail = Action {

    val networks = Network.all();
    val nodes = Node.getNodesFromNetwork(networks.head.id)
    val initTimes = InitTime.getInitTimesFromNode(nodes.head.id)

    Ok(views.html.nodeDetail(networks,nodes,initTimes))
  }

}