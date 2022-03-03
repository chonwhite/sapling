package steps

import spinal.core._
import spinal.lib._

import scala.language.postfixOps

class AddressBundle extends Bundle {
  val address: Flow[UInt] = master Flow UInt(width = 32 bits)
}

class MockPC extends Component{
  val io = new AddressBundle()

  val clk: Bool = ClockDomain.current.readClockWire
  val reset: Bool = ClockDomain.current.readResetWire

  val valid: Bool = reset
  val address: UInt = Reg(UInt(width = 32 bits))
  address.init(0)

  address := address + 4
  io.address.payload <> address
  io.address.valid <> valid
}
