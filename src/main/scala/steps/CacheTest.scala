package steps

import core.{BRamCache, CacheConfig}
import mafufactor.ILA
import spinal.core._

class CacheTest extends Component{
  val io: Bundle = new Bundle {}

  noIoPrefix()

  val clk: Bool = ClockDomain.current.readClockWire
  val reset: Bool = ClockDomain.current.readResetWire

  val config: CacheConfig = CacheConfig(width = 32 bits,
    depth = 32, rows = 2, content = null)
  val cache = new BRamCache(config)
  val valid: Bool = reset
  val address: UInt = Reg(UInt(width = 32 bits))
  address.init(0)

  val debugger = new ILA()

  address := address + 4
//  debugger.io.clk <> clk
  cache.io.clk <> clk
  cache.io.address.valid <> valid
  cache.io.address.payload <> address

//  debugger.io.data <> cache.io.data
}

object CacheTestVerilog {
  def main(args: Array[String]) {
    SpinalVerilog(new CacheTest())
  }
}

