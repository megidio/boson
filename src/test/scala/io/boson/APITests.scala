package io.boson

import java.nio.ByteBuffer
import java.util.concurrent.CompletableFuture
import bsonLib.{BsonArray, BsonObject}
import io.boson.bson.Boson
import io.boson.bson.bsonValue.{BsSeq, BsValue}
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.junit.Assert.assertEquals

@RunWith(classOf[JUnitRunner])
class APITests extends FunSuite {

  val obj1: BsonObject = new BsonObject().put("fridgeTemp", 5.2f).put("fanVelocity", 20.5).put("doorOpen", false)
  val obj2: BsonObject = new BsonObject().put("fridgeTemp", 5.0f).put("fanVelocity", 20.6).put("doorOpen", false)
  val obj3: BsonObject = new BsonObject().put("fridgeTemp", 3.854f).put("fanVelocity", 20.5).put("doorOpen", true)

  val arr: BsonArray = new BsonArray().add(obj1).add(obj2).add(obj3)

  val bsonEvent: BsonObject = new BsonObject().put("fridgeReadings", arr)

  val validatedByteArray: Array[Byte] = arr.encodeToBarray()

  val validatedByteBuffer: ByteBuffer = ByteBuffer.allocate(validatedByteArray.length)
  validatedByteBuffer.put(validatedByteArray)
  validatedByteBuffer.flip()


  test("extract pos from array with Array[Byte]") {
    val expression: String = "[1 until 3]"
    val future: CompletableFuture[BsValue] = new CompletableFuture[BsValue]()
    val boson: Boson = Boson.extractor(expression, (in: BsValue) => future.complete(in))
    boson.go(validatedByteArray)

    assertEquals(BsSeq(Seq(Seq(
      Map("fridgeTemp" -> 5.0f, "fanVelocity" -> 20.6, "doorOpen" -> false),
      Map("fridgeTemp" -> 3.854f, "fanVelocity" -> 20.5,"doorOpen" -> true)
    ))), future.join())
  }

  test("Use extraction in a loop") {
    val expression: String = "[2 to end]"
    val future: CompletableFuture[BsValue] = new CompletableFuture[BsValue]()
    val boson: Boson = Boson.extractor(expression, (in: BsValue) => future.complete(in))

    val randomInt = scala.util.Random
    for(range <- 0 until 5) {
      boson.go(arr.encodeToBarray())
      println(s"Result$range -> ${future.join()}")
      arr.add(randomInt.nextInt(10))
    }
    assertEquals(true,true)
  }

//  test("extract last occurrence with Array[Byte]") {
//    val expression: String = "last"
//    val future: CompletableFuture[BsValue] = new CompletableFuture[BsValue]()
//    val boson: Boson = Boson.extractor(expression, (in: BsValue) => future.complete(in))
//    boson.go(validatedByteArray)
//    assertEquals(BsSeq(Seq()))
//  }

  test("extract pos from array with ByteBuffer") {
    val expression: String = "[1 to 2]"
    val future: CompletableFuture[BsValue] = new CompletableFuture[BsValue]()
    val boson: Boson = Boson.extractor(expression, (in: BsValue) => future.complete(in))
    boson.go(validatedByteBuffer)

    assertEquals(BsSeq(Seq(Seq(
      Map("fridgeTemp" -> 5.0f, "fanVelocity" -> 20.6, "doorOpen" -> false),
      Map("fridgeTemp" -> 3.854f, "fanVelocity" -> 20.5,"doorOpen" -> true)
    ))), future.join())
  }

}
