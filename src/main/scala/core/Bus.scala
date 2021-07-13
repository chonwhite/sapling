package core

import spinal.core._
import spinal.lib._

import scala.language.postfixOps

class Bus extends Bundle{
  val address = master Flow UInt(width = 32 bits)
  val data = master Flow(Bits(width = 32 bits))
}
