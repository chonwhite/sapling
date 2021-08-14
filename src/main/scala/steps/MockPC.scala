package steps

import spinal.core._
import spinal.lib._

import scala.language.postfixOps

class MockPC extends Component{
  val io = new Bundle{
    val address = master Flow UInt(width = 32 bits)
  }

  val clk = ClockDomain.current.readClockWire
  val reset = ClockDomain.current.readResetWire

  val valid = reset
  val address = Reg(UInt(width = 32 bits))
  address.init(0)

  address := address + 4
  io.address.payload <> address
  io.address.valid <> valid
}
