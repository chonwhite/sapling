package mafufactor

import spinal.core._
import spinal.lib._

import scala.language.postfixOps

class AluILABundle extends Bundle {
  val clk = in Bool()
  val op = in UInt(5 bits)
  val s1 = in Bits(32 bits)
  val s2 = in Bits(32 bits)
  val res = in Bits(32 bits)
  val status = new Bundle {
    val negative = in Bool()
    val zero = in Bool()
  }
}

class AluILA extends Component{
  val io = new AluILABundle()
}
