package core

import spinal.core._
import spinal.lib._

import scala.language.postfixOps

case class CacheConfig(
                        width: BitCount,
                        depth: Int,
                        rows: Int,
                        content : Array[BigInt]
                      ) {
  def lineWidth: BitCount = {
    width.value * depth bits
  }
}

case class Tag() extends Bundle {
  val valid = Reg(Bool) init False
  val dirty = Reg(Bool) init False
  val tag = UInt(width = 2 bits)
}

class BRamCache(config: CacheConfig) extends BlackBox {
  val io = new Bundle {
    val clk = in Bool()
    val address = slave Flow UInt(width = 32 bits)
    val data = master Flow Bits(width = 32 bits)
  }
  noIoPrefix()
  addRTLPath("AXI4MemoryBus.v")
}

class InstructionCache(config: CacheConfig) extends Component {
  val io = new Bundle {
    val address = slave Flow UInt(width = 32 bits)
    val data = master Flow Bits(width = config.width)
  }

  val mem = Mem(Bits(32 bits), wordCount = 4096)

  mem.initialContent = config.content
  val outData = Bits(width = 32 bits)

  outData := 0
  io.data.valid := io.address.valid
  io.data.payload := outData

  when(io.address.valid) {
    outData := mem.readAsync(io.address.payload(log2Up(mem.wordCount) + 1 downto 2))
  }
}

object CacheVerilog {
  def main(args: Array[String]): Unit = {
    //    SpinalVerilog(new Cache(32 bits, 32))
    val cacheConfig = CacheConfig(
      width = 32 bits,
      depth = 4,
      rows = 4,
      content = null
    );
//    val dataBus = new NativeBus()
    SpinalConfig(device = Device.XILINX)
          .generateVerilog(new BRamCache(cacheConfig))
  }
}

