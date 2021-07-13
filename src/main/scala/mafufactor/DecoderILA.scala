package mafufactor

import spinal.core._
import spinal.lib._

import scala.language.postfixOps

class DecoderILABundle extends Bundle {
  val clk = in Bool()
  val inst = slave Flow Bits(width = 32 bits)
  val opcodes = in UInt(width = 5 bits)
  val format = in UInt(width = 3 bits)
  val rd = in UInt(width = 5 bits)
  val rs1 = in UInt(width = 5 bits)
  val rs2 = in UInt(width = 5 bits)
  val imm = in Bits(width = 32 bits)
}

class DecoderILA extends BlackBox {
  val io = new DecoderILABundle()
}
