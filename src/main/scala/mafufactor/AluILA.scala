package mafufactor

import spinal.core._

import scala.language.postfixOps

class AluILABundle extends Bundle {
  val clk: Bool = in Bool()
  val op: UInt = in UInt(5 bits)
  val s1: Bits = in Bits(32 bits)
  val s2: Bits = in Bits(32 bits)
  val res: Bits = in Bits(32 bits)
  val status = new Bundle {
    val negative: Bool = in Bool()
    val zero: Bool = in Bool()
  }
}

class AluILA extends Component{
  val io = new AluILABundle()
}
