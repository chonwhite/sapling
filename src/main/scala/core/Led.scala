package core

import spinal.core._

import scala.collection.mutable.ArrayBuffer
import scala.language.postfixOps

class LedBundle extends Bundle {
  val led: Bits = out Bits(width = 8 bits)
}

class Led extends Component{

  val io: LedBundle = new LedBundle()


  val counter: UInt = Reg(UInt(width = 26 bits))
  val slowClk: Bool = counter(21)
//  val control = new ControlUnit()

  counter := counter + 1

  val mem: Mem[Bits] = Mem(Bits(8 bits), wordCount = 256)
  val codes: ArrayBuffer[BigInt] = ArrayBuffer[BigInt]()
  for (i <- 0 to 10) {
    codes += BigInt(i)
  }
  mem.initialContent = codes.toArray

  val ccd: ClockDomain = ClockDomain(slowClk, ClockDomain.current.reset)

  val coreArea: ClockingArea = new ClockingArea(ccd) {

  val led: UInt = Reg(UInt(width = 8 bits))

    val valid: Bool = True
    val address = U"8'h03"
    led.init(0)
    io.led <> led.asBits

    address <= address + 1

    led := mem.readSync(
      enable  = valid,
      address = address
    ).asUInt
  }


}

object LEDUnitVerilog {
  def main(args: Array[String]): Unit = {
    SpinalVerilog(new Led())
  }
}
