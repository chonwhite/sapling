package core

import spinal.core._
import spinal.lib._

trait LogicAnalyzer extends BlackBox {
  val io = new Bundle{
    val clk = in Bool()
    val data = slave Flow Bits()
  }
  noIoPrefix()
}
