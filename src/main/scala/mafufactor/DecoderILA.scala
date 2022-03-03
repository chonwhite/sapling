package mafufactor

import spinal.core._
import spinal.lib._

import scala.language.postfixOps

class DecoderILABundle extends Bundle {
  val clk: Bool = in Bool()
  val inst: Flow[Bits] = slave Flow Bits(width = 32 bits)
  val opcodes: UInt = in UInt(width = 5 bits)
  val format: UInt = in UInt(width = 3 bits)
  val rd: UInt = in UInt(width = 5 bits)
  val rs1: UInt = in UInt(width = 5 bits)
  val rs2: UInt = in UInt(width = 5 bits)
  val imm: Bits = in Bits(width = 32 bits)
}

class DecoderILA extends BlackBox {
  val io = new DecoderILABundle()
}
