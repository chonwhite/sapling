package core

import core.OpCodes.PCOpCodes
import spinal.core._
import spinal.lib.master

class ProgramCounter extends Component {
  val io = new Bundle {
    val op = in UInt(width = 2 bits)
    val imm = in Bits(width = 32 bits)
    val address = master Flow(UInt(width = 32 bits))
  }

  val four = SInt(width = 32 bits)
  four := 4
  val immSigned = SInt(io.imm.getBitsWidth bits)
  immSigned := io.imm.asSInt
  val storedAddress = Reg(SInt(width = 32 bits)) init 0 //TODO
  switch(io.op) {
    is(PCOpCodes.INCREMENT) {
      storedAddress := storedAddress + four
    }
    is(PCOpCodes.ADD_OFFSET) {
      storedAddress := storedAddress + immSigned
    }
    is(PCOpCodes.SET) {
      storedAddress := immSigned
    }
  }
  io.address.payload := storedAddress.asUInt
  io.address.valid := True
}


object ProgramCounterVerilog {
  def main(args: Array[String]) {
    SpinalVerilog(new ProgramCounter)
  }
}

