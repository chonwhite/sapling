package mafufactor

import core.ControlUnit
import spinal.core._

import scala.language.postfixOps

class ResetGenerator extends Area {
  val counter = Reg(UInt(width = 12 bits)) init 0
  val reset = Reg(Bool) init True
  when(counter < 1024) {
    reset := True
    counter := counter + 1;
  } otherwise {
    reset := False
  }
}

class iCESugarNanoBundle extends Bundle{
  val key: Bits = in Bits(width = 4 bits)
  val led: Bits = out Bits(width = 4 bits)
}

class iCESugarNano extends Component{
  val io = new iCESugarNanoBundle()
  val resetGenerator = new ResetGenerator()

  val gpioAddress = U"32'd1024".asBits

  val led = Reg(Bits(width = 4 bits)) init(0)

  val cv = new ControlUnit()
  when(cv.io.bus.address.valid) {
    when(cv.io.bus.address.payload === gpioAddress) {
      led := cv.io.bus.data.payload(3 downto 0).reversed
    }
  }

  io.led := led

}

object iCESugarNanoVerilog {
  def main(args: Array[String]): Unit = {
    SpinalVerilog(new iCESugarNano())
  }
}
