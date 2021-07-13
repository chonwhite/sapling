package core

import core.OpCodes.PCOpCodes
import spinal.core.{Bits, _}
import spinal.lib._

import scala.language.postfixOps

class ProgramCounter extends Component {

  class PCBundle() extends Bundle {
    val op: UInt = in UInt(width = 2 bits)
    val imm: Bits = in Bits(width = 32 bits)
    val address: Flow[UInt] = master Flow UInt(width = 32 bits)
    val pc: Bits = out Bits(width = 32 bits)
  }
  val io: PCBundle = new PCBundle()

  val four: SInt = SInt(width = 32 bits)
  four := 4
  val immSigned: SInt = SInt(io.imm.getBitsWidth bits)
  immSigned := io.imm.asSInt
  val storedAddress: SInt = Reg(SInt(width = 32 bits)) init 0 //TODO
  io.pc := storedAddress.resize(32 bits).asBits
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

