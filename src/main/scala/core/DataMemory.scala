package core

import spinal.core._
import spinal.lib.{Flow, master, slave}

import scala.language.postfixOps

class DataMemory extends Component{
  val io = new Bundle {
    val address: Flow[UInt] = slave Flow UInt(width = 32 bits)
    val data: Flow[Bits] = master Flow Bits(width = 32 bits)
  }
}
