package core

import spinal.core._
import spinal.lib._

class Bus extends Bundle{
  val address = master Flow UInt(width = 32 bits)
  val data = master Flow(Bits(width = 32 bits))
}
