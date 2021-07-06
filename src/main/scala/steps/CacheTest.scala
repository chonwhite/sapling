package steps

import core.{BRamCache, CacheConfig}
import mafufactor.ILA
import spinal.core._

class CacheTest extends Component{
  val io = new Bundle {
  }

  noIoPrefix()

  val clk = ClockDomain.current.readClockWire;
  val reset = ClockDomain.current.readResetWire

  val cache = new BRamCache(config)
  val valid = reset
  val address = Reg(UInt(width = 32 bits))
  address.init(0)

  val debugger = new ILA()
  val config = CacheConfig(width = 32 bits,
    depth = 32, rows = 2, content = null)

  address := address + 4
  debugger.io.clk <> clk
  cache.io.clk <> clk
  cache.io.address.valid <> valid
  cache.io.address.payload <> address

  debugger.io.data <> cache.io.data
}

object CacheTestVerilog {
  def main(args: Array[String]) {
    SpinalVerilog(new CacheTest())
  }
}

