package core

import core.OpCodes.PCOpCodes
import spinal.core.{Bits, _}
import spinal.lib._

import scala.language.postfixOps

class PCDebugger extends BlackBox {
  val io = new Bundle {
    val op: Bits = in Bits(width = 2 bits)
    val imm: Bits = in Bits(width = 32 bits)
    val pc: Bits = in Bits(width = 32 bits)
  }

}

class ProgramCounter extends Component {

  class PCBundle() extends Bundle {
    val op: Bits = in Bits(width = 2 bits)
    val imm: Bits = in Bits(width = 32 bits)
    val address: Flow[UInt] = master Flow UInt(width = 32 bits)
    val pc: Bits = out Bits(width = 32 bits)
  }
  val io: PCBundle = new PCBundle()

//  val PCDebugger = new PCDebugger()
//  PCDebugger.io.op := io.op.asBits
//  PCDebugger.io.imm := io.imm
//  PCDebugger.io.pc := io.pc;

  val four: SInt = SInt(width = 32 bits)
  four := 4
  val immSigned: SInt = SInt(io.imm.getBitsWidth bits)
  immSigned := io.imm.asSInt
  val storedAddress: SInt = Reg(SInt(width = 32 bits)) init 0 //TODO
  io.pc := storedAddress.asBits
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
  io.address.valid := !ClockDomain.current.readResetWire
}

object ProgramCounterVerilog {
  def main(args: Array[String]): Unit = {
    SpinalVerilog(new ProgramCounter)
  }
}

