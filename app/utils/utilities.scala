package utils

/**
 * Created with IntelliJ IDEA.
 * User: Nicolas
 * Date: 04/02/14
 * Time: 21:47
 * To change this template use File | Settings | File Templates.
 */
object utilities {

  def transpose[A](xs: List[List[A]]): List[List[A]] = xs.filter(_.nonEmpty) match {
       case Nil    =>  Nil
       case ys: List[List[A]] => ys.map{ _.head }::transpose(ys.map{ _.tail })
  }
}
