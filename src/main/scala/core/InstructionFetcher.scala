package core

import spinal.core._
import spinal.lib.{master, slave}

class InstructionFetcher(PC : ProgramCounter) extends Component {
  val io = new Bundle {
    val address = slave Flow(UInt(width = 32 bits))
    val instruction = master Flow(Bits(width = 32 bits))
  }

//  io.instruction.valid := True
//  val address_valid = Bool
//  address_valid := True

  val instructionCache = new Cache(32 bits, 32)
  instructionCache.io.address <> io.address
  io.instruction <> instructionCache.io.data

}

object InstructionFetcherVerilog {
  def main(args: Array[String]) {
    SpinalVerilog(new InstructionFetcher(null))
  }
}
