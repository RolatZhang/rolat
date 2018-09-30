package rolat.service.scala.test

import scala.collection.mutable

object TestOption {
  var precision:Double=_
  def main(args: Array[String]): Unit = {
    val bigDataSkills =
      Map("Java" -> "first",
        "Hadoop" -> "second",
        "Spark" -> "third",
        "storm" -> "forth",
        "hbase" -> "fifth",
        "hive" -> "sixth",
        "photoshop" -> null)
 
    println(bigDataSkills.get("Java") == Some("first"))
    println(bigDataSkills.get("photoshop").get == null)
    println(bigDataSkills.get("Spark").get == "third")
    println(bigDataSkills.get("abc123") == None)


    val cacheResultMap:mutable.Map[String,(String,Int,Long)]=mutable.Map()
    cacheResultMap+=("1"->("1",2,3L))
    println(cacheResultMap)
    val result=cacheResultMap.get("1")
    println(cacheResultMap)
    println(cacheResultMap)


    val abc="123"
    val b="abc"
    val c="123"
    abc match {
      case b =>println(11)
        println(22)
      case c:String =>println(111)
        println(222)
    }

    println(precision==null)

  }
}