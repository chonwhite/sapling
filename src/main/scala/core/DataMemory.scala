package core

import spinal.core._
import spinal.lib.{master, slave}

class DataMemory extends Component {
  val io = new Bundle {
    val address = slave Flow UInt(width = 32 bits)
    val data = master Flow Bits(width = 32 bits)
  }
}
