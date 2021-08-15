package boards

import core.ControlUnit
import spinal.core._

import scala.language.postfixOps

class Zynq7010Bundle extends Bundle{
  val key: Bits = in Bits(width = 4 bits)
  val led: Bits = out Bits(width = 4 bits)
}

class Zynq7010 extends Component{
  val io = new Zynq7010Bundle()

  val reset: Bool = Bool()
  reset := !io.key(0)

  val cv = new ControlUnit()

  io.led := 0
}

object ControlUnitVerilog {
  def main(args: Array[String]): Unit = {
    SpinalVerilog(new Zynq7010())
  }
}
