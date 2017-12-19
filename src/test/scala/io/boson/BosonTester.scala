package io.boson

import java.util.concurrent.CompletableFuture

import bsonLib.{BsonArray, BsonObject}
import io.boson.bson.Boson
import io.boson.bson.bsonImpl.Constants._
import io.boson.bson.bsonValue.BsValue
import io.netty.buffer.{ByteBuf, Unpooled}
import io.netty.util.ByteProcessor

import scala.util.{Failure, Success, Try}

object BosonTester extends App {
  def tester[T](f: T => T): Any = {
    val double: T = 2.5.asInstanceOf[T]
    Try(f(double)) match {
      case Success(v) =>
        println("value selected has same type as provided")
        v
      case Failure(m) =>
        println("value selected DOESNT MATCH with the provided")
        throw new RuntimeException(m)
    }
  }

  val bP: ByteProcessor = (value: Byte) => {
    println("char= " + value.toChar + " int= " + value.toInt + " byte= " + value)
    true
  }


  //val newField: Double = 3.4
  //println(s"value -> ${tester[Double]((_: Double) => newField )}")

  val arr: BsonArray = new BsonArray().add(1.1.toFloat).add(2.2).add("END")
  val validatedByteArray: Array[Byte] = arr.encodeToBarray()
  val obj1: BsonObject = new BsonObject().put("string", "Hi").put("bytearray", "ola".getBytes)
  val validatedByteArray2: Array[Byte] = obj1.encodeToBarray()
  val buffer: ByteBuf = Unpooled.copiedBuffer(validatedByteArray2)
  //buffer.forEachByte(bP)
  println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")
  println("totalSize: "+buffer.readIntLE())
  println("type: " + buffer.readByte())
  println("key: " + buffer.readCharSequence(6,charset))
  //buffer.forEachByte(bP)
  println("value: " + buffer.readCharSequence(3,charset))
  println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")
//  val expression: String = "[0 to end]"
//  val future: CompletableFuture[BsValue] = new CompletableFuture[BsValue]()
//  val boson: Boson = Boson.extractor(expression, (in: BsValue) => future.complete(in))
//  boson.go(validatedByteArray)
//  println("result of extracting \""+ expression+ "\" -> " + future.join())
  println("-------------------------------------------------------------------------------------------------------------")
  val expression2: String = "all"
  val boson2: Boson = Boson.injector(expression2, (_:String) => "Hi!!!")
  val result: CompletableFuture[Array[Byte]] = boson2.go(validatedByteArray2)
  println("result of extracting \""+ expression2+ "\" -> " + result.join())

//  println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")
//  val buf: ByteBuf = Unpooled.copiedBuffer(result.join())
//  println("totalSize: "+buf.readIntLE())
//  println("type: " + buf.readByte())
//  println("key: " + buf.readCharSequence(6,charset))
//  //buf.forEachByte(bP)
//  println("value: " + buf.readDoubleLE())
//  println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")

    val expression: String = "all"
    val future: CompletableFuture[BsValue] = new CompletableFuture[BsValue]()
    val boson: Boson = Boson.extractor(expression, (in: BsValue) => future.complete(in))
    boson.go(result.join())
    println("result of extracting \""+ expression+ "\" -> " + future.join())




}