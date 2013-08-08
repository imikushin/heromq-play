package com.heromq.play

import play.api.mvc._
import play.api.libs.iteratee._
import java.nio.charset.Charset
import scala.language.reflectiveCalls

case class Chunk(topic: String, chunk: String)

object API extends Controller {

  private val utf8 = Charset.forName("UTF-8")

  val (enumerator, channel) = Concurrent.broadcast[Chunk]

  def pub(topic: String) = EssentialAction {rh =>
    Iteratee.foreach {bytes: Array[Byte] => channel.push(Chunk(topic, new String(bytes, utf8)))} map {_ => Ok}
  }

  def filter(topic: String) = Enumeratee.filter[Chunk] {_.topic == topic}

  def payload = Enumeratee.map[Chunk] {_.chunk}

  def connDeathWatch[T](addr: String): Enumeratee[T, T] =
    Enumeratee.onIterateeDone {() => println(addr + " - SSE disconnected")}

  def sub(topic: String) = Action {req =>
    println(req.remoteAddress + " - SSE connected")
    Ok.stream(enumerator
        &> filter(topic) &> payload
        &> Concurrent.buffer(50)
        &> connDeathWatch(req.remoteAddress)
    ).as("text/plain")
  }

}
