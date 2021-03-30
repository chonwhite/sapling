package core

import spinal.core._

class Led extends Component{

  val io = new Bundle{
    val led = out Bits(width = 4 bits)
  }

  val led = Reg(Bits(width = 4 bits))
  val control = new ControlUnit()
  io.led := led

  when(control.io.bus.address.valid) {
    when(control.io.bus.address.payload === U(10000)){
      led := control.io.bus.data.payload(3 downto 0)
    }
  }

}

object LEDUnitVerilog {
  def main(args: Array[String]) {
    SpinalVerilog(new Led())
  }
}
