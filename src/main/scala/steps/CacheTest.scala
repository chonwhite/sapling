package steps

import core.{BRamCache, CacheConfig}
import mafufactor.ILA
import spinal.core._

import scala.language.postfixOps

class CacheTest extends Component{
  val io: Bundle = new Bundle {}

  noIoPrefix()

  val clk: Bool = ClockDomain.current.readClockWire
  val reset: Bool = ClockDomain.current.readResetWire

  val config: CacheConfig = CacheConfig(width = 32 bits,
    depth = 32, rows = 2, content = null)
  val cache = new BRamCache()
  cache.setConfig(config)
  val valid: Bool = reset
  val address: UInt = Reg(UInt(width = 32 bits))
  address.init(0)

  val debugger = new ILA()

  address := address + 4
  cache.io.address.valid <> valid
  cache.io.address.payload <> address

}

object CacheTestVerilog {
  def main(args: Array[String]): Unit = {
    SpinalVerilog(new CacheTest())
  }
}

