package core

import spinal.core._
import spinal.lib._

import scala.language.postfixOps

class Bus extends Bundle{
  val address: Flow[Bits] = master Flow Bits(width = 32 bits)
  val data: Flow[Bits] = master Flow(Bits(width = 32 bits))
}
