package models.Client

/**
 * Created with IntelliJ IDEA.
 * User: Nicolas
 * Date: 04/02/14
 * Time: 09:15
 * To change this template use File | Settings | File Templates.
 */

/**
 * This Object represent a bubble in the bubble graph for a given node
 * @param nodeDistantId distant node to consider
 * @param position position in the close Node Table
 * @param cardinal number of occurence of the distant node in th position 'position'
 */
case class CloseNodeBubble(nodeDistantId: Long, position: Int, cardinal: Int)
