package core

import spinal.core._
import spinal.lib._

import scala.language.postfixOps

class InstructCacheBundle extends Bundle {
  val address = slave Flow UInt(width = 32 bits)
  val data = master Flow Bits(width = 32 bits)
}

trait InstructionCache extends Component {
  val io = new InstructCacheBundle()
  def setConfig(config :CacheConfig): Unit
}

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

class BRamCache extends BlackBox with InstructionCache{
  noIoPrefix()
  addRTLPath("AXI4MemoryBus.v")

  override def setConfig(config: CacheConfig): Unit = {}

  override type RefOwnerType = this.type
}

class MemInstructionCache extends Component with InstructionCache{
  val mem = Mem(Bits(32 bits), wordCount = 128)
  val outData = Bits(width = 32 bits)

  outData := 0
  io.data.valid := io.address.valid
  io.data.payload := outData

  when(io.address.valid) {
    outData := mem.readAsync(io.address.payload(log2Up(mem.wordCount) + 1 downto 2))
  }

  override def setConfig(config: CacheConfig): Unit = {
    mem.initialContent = config.content
  }

  override type RefOwnerType = this.type
}

object CacheVerilog {
  def main(args: Array[String]): Unit = {
    val cacheConfig = CacheConfig(
      width = 32 bits,
      depth = 4,
      rows = 4,
      content = null
    );
//    val dataBus = new NativeBus()
    SpinalConfig(device = Device.XILINX)
          .generateVerilog(new BRamCache())
  }
}

