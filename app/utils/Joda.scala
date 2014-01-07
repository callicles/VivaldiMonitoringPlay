package utils

import org.joda.time.DateTime

/**
 * Created with IntelliJ IDEA.
 * User: Nicolas
 * Date: 06/01/14
 * Time: 16:53
 * To change this template use File | Settings | File Templates.
 */
object Joda {
  implicit def dateTimeOrdering: Ordering[DateTime] = Ordering.fromLessThan(_ isBefore _)
}

