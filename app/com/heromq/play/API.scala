package com.heromq.play

import grizzled.slf4j.Logging
import java.nio.charset.Charset
import play.api.libs.iteratee._
import play.api.mvc._
import scala.language.reflectiveCalls

case class Chunk(topic: String, payload: String)

object API extends Controller with Logging {

  private val utf8 = Charset.forName("UTF-8")
  private def str(bytes: Array[Byte]) = new String(bytes, utf8)

  val (enumerator, channel) = Concurrent.broadcast[Chunk]

  def pub(topic: String) = EssentialAction {rh =>
    Iteratee.foreach[Array[Byte]] {bytes => channel.push(Chunk(topic, str(bytes)))} map {_ => Ok}
  }

  def filter(topic: String) = Enumeratee.filter[Chunk] {_.topic == topic}

  def payload = Enumeratee.map[Chunk] {_.payload}

  def sub(topic: String) = Action {req =>
    debug(req.remoteAddress + " - client connected")
    Ok.stream(enumerator &> filter(topic) &> payload &> Concurrent.buffer(50))
  }

}
