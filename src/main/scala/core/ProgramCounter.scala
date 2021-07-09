package core

import core.OpCodes.PCOpCodes
import spinal.core._
import spinal.lib._

class ProgramCounter extends Component {

  class PCBundle() extends Bundle {
    val op: UInt = in UInt(width = 2 bits)
    val imm: Bits = in Bits(width = 32 bits)
    val address: Flow[UInt] = master Flow UInt(width = 32 bits)
    val pc: UInt = out UInt(width = 32 bits)
  }
  val io: PCBundle = new PCBundle()

  val four: SInt = SInt(width = 32 bits)
  four := 4
  val immSigned: SInt = SInt(io.imm.getBitsWidth bits)
  immSigned := io.imm.asSInt
  val storedAddress: SInt = Reg(SInt(width = 32 bits)) init 0 //TODO
  io.pc := storedAddress.asUInt.resize(32 bits)
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

