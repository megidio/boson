package io.joson

import java.util.concurrent.CompletableFuture

import bsonLib.{BsonArray, BsonObject}
import io.boson.bson.Boson
import io.boson.bson.bsonValue.BsValue
import io.boson.json.Joson
import org.junit.Assert.assertEquals
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

/**
  * Created by Ricardo Martins on 22/01/2018.
  */

@RunWith(classOf[JUnitRunner])
class APItests extends FunSuite{


  val hat3: BsonObject = new BsonObject().put("Price", 38).put("Color", "Blue")
  val hat2: BsonObject = new BsonObject().put("Price", 35).put("Color", "White")
  val hat1: BsonObject = new BsonObject().put("Price", 48).put("Color", "Red")
  val hats: BsonArray = new BsonArray().add(hat1).add(hat2).add(hat3)
  val edition3: BsonObject = new BsonObject().put("Title", "C++Machine").put("Price", 38)
  val sEditions3: BsonArray = new BsonArray().add(edition3)
  val title3: BsonObject = new BsonObject().put("Title", "C++").put("Price", 12.6).put("SpecialEditions", sEditions3)
  val edition2: BsonObject = new BsonObject().put("Title", "ScalaMachine").put("Price", 40)
  val sEditions2: BsonArray = new BsonArray().add(edition2)
  val title2: BsonObject = new BsonObject().put("Title", "Scala").put("Price", 21.5).put("SpecialEditions", sEditions2)
  val edition1: BsonObject = new BsonObject().put("Title", "JavaMachine").put("Price", 39)
  val sEditions1: BsonArray = new BsonArray().add(edition1)
  val title1: BsonObject = new BsonObject().put("Title", "Java").put("Price", 15.5).put("SpecialEditions", sEditions1)
  val books: BsonArray = new BsonArray().add(title1).add(title2).add(title3)
  val store: BsonObject = new BsonObject().put("Book", books).put("Hat", hats)
  val bson: BsonObject = new BsonObject().put("Store", store)


  val json = "{\"Store\":{\"Book\":[{\"Title\":\"Java\",\"SpecialEditions\":[{\"Title\":\"JavaMachine\",\"Price\":39}],\"Price\":15.5},{\"Title\":\"Scala\",\"Price\":21.5,\"SpecialEditions\":[{\"Title\":\"ScalaMachine\",\"Price\":40}]},{\"Title\":\"C++\",\"Price\":12.6,\"SpecialEditions\":[{\"Title\":\"C++Machine\",\"Price\":38}]}],\"Hat\":[{\"Price\":48,\"Color\":\"Red\"},{\"Price\":35,\"Color\":\"White\"},{\"Price\":38,\"Color\":\"Blue\"}]}}"

  test("json == bson") {

    /*
    * Injection
    * */
    val expression = "Store"

    println("WORK WITH JOSON\n")
    println("|-------- Perform Injection --------|\n")
    val joson: Joson = Joson.injector(expression, (in: Map[String, Any]) => in.+(("WHAT", 10)))
    val midResult: CompletableFuture[String] = joson.go(json)
    val result: String = midResult.join()
    println("|-------- Perform Extraction --------|\n")
    val future: CompletableFuture[BsValue] = new CompletableFuture[BsValue]()
    val joson1: Joson = Joson.extractor(expression, (in: BsValue) => future.complete(in))
    joson1.go(result)
    val json1: Any = future.join().getValue
    println(json1)


    println("WORK WITH BOSON\n")
    println("|-------- Perform Injection --------|\n")
    val validBsonArray: Array[Byte] = bson.encodeToBarray
    val boson: Boson = Boson.injector(expression,(in: Map[String, Any]) => in.+(("WHAT", 10)))
    val midResult1: CompletableFuture[Array[Byte]] = boson.go(validBsonArray)
    val result1: Array[Byte] = midResult1.join()
    println("|-------- Perform Extraction --------|\n")
    val future1: CompletableFuture[BsValue] = new CompletableFuture[BsValue]()
    val boson1: Boson = Boson.extractor(expression, (in: BsValue) => future1.complete(in))
    boson1.go(result1)
    val bson1: Any = future1.join().getValue
    println(bson1)


    println("|-------- Perform Assertion --------|\n\n")
    assertEquals(json1, bson1)

   // assertEquals(Vector(Map("Book" -> List(Map("Price" -> 15.5, "SpecialEditions" -> List(Map("Price" -> 39, "Title" -> "JavaMachine")), "Title" -> "Java"), Map("Price" -> 21.5, "SpecialEditions" -> List(Map("Price" -> 40, "Title" -> "ScalaMachine")), "Title" -> "Scala"), Map("Price" -> 12.6, "SpecialEditions" -> List(Map("Price" -> 38, "Title" -> "C++Machine")), "Title" -> "C++")), "Hat" -> List(Map("Color" -> "Red", "Price" -> 48), Map("Color" -> "White", "Price" -> 35), Map("Color" -> "Blue", "Price" -> 38)), "WHAT!!!" -> 10)),future.join().getValue  )
  }

}

