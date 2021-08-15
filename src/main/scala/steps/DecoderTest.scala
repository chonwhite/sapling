package steps

import core._
import mafufactor.DecoderILA
import spinal.core._

import scala.language.postfixOps

class DecoderTest extends Component{
  val io: Bundle = new Bundle {}
  noIoPrefix()

  val clk: Bool = ClockDomain.current.readClockWire

  val PC = new MockPC()
  val config: CacheConfig = CacheConfig(width = 32 bits,
    depth = 32, rows = 2, content = null)
  val cache = new BRamCache()
  cache.setConfig(config)
  val decoder = new Decoder()
  decoder.io.inst <> cache.io.data
  val debugger = new DecoderILA()

  cache.io.address <> PC.io.address

  debugger.io.clk <> clk
  debugger.io.inst <> cache.io.data
  debugger.io.opcodes <> decoder.io.opcodes
  debugger.io.format <> decoder.io.format
  debugger.io.rd <> decoder.io.rd
  debugger.io.rs1 <> decoder.io.rs1
  debugger.io.rs2 <> decoder.io.rs2
  debugger.io.imm <> decoder.io.imm
}

object DecoderTestVerilog {
  def main(args: Array[String]): Unit = {
    SpinalVerilog(new DecoderTest())
  }
}
