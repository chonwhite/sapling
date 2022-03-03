package steps

import core.{ALU, BRamCache, CacheConfig, Decoder}
import mafufactor.AluILA
import spinal.core._
//import spinal.lib.cpu.riscv.impl.Utils.ALU

import scala.language.postfixOps

class ALUTest extends Component{
  noIoPrefix()
  val io: Bundle = new Bundle {}

  val clk: Bool = ClockDomain.current.readClockWire
  val s1 = B"32'x42"

  val PC = new MockPC()
  val config: CacheConfig = CacheConfig(width = 32 bits,
    depth = 32, rows = 2, content = null)
  val cache = new BRamCache()
  cache.setConfig(config)

  val decoder = new Decoder()
  decoder.io.inst <> cache.io.data

  val debugger = new AluILA()
  val alu = new ALU()

  cache.io.address <> PC.io.address

  alu.io.op <> decoder.io.opcodes
  alu.io.s1 <> s1
  alu.io.s2 <> decoder.io.imm

  debugger.io.clk <> clk
  debugger.io.op <> decoder.io.opcodes
  debugger.io.s1 <> s1
  debugger.io.s2 <> decoder.io.imm
  debugger.io.res <> alu.io.res
  debugger.io.status <> alu.io.status
}

object ALUTestVerilog {
  def main(args: Array[String]): Unit = {
    SpinalVerilog(new ALUTest())
  }
}

