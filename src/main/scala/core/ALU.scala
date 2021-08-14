package core

import core.OpCodes.ALUOpCodes._
import spinal.core._

import scala.language.postfixOps

class ALU extends Component {

  class ALUBundle extends Bundle{
    class StatusBundle extends Bundle {
      val negative: Bool = out Bool()
      val zero: Bool = out Bool()
    }
    val op: UInt = in UInt(5 bits)
    val s1: Bits = in Bits(32 bits)
    val s2: Bits = in Bits(32 bits)
    val res: Bits = out Bits(32 bits)
    val status = new StatusBundle()
  }
  val io = new ALUBundle()

  val s1: SInt = io.s1.asSInt
  val s2: SInt = io.s2.asSInt

  val u1: UInt = io.s1.asUInt
  val u2: UInt = io.s2.asUInt

  val signed_value: SInt = SInt(33 bits)
  val unsigned_value: UInt = UInt(33 bits)
  val bits_value: Bits = signed_value.asBits
  val zero: SInt = S(0, 33 bits)
  val sop1: SInt = s1.resize(width = 33 bits)
  val sop2: SInt = s2.resize(width = 33 bits)
  val op1: UInt = u1.resize(width = 33 bits)
  val op2: UInt = u2.resize(width = 33 bits)

  unsigned_value := 0

  switch(io.op) {
    is(ADD) {
      signed_value := sop1 + sop2
    }
    is(SUB) {
      signed_value := sop1 - sop2
    }
    is(SLL) {
      unsigned_value := (op1 |<< op2)
      signed_value := unsigned_value.asSInt
    }
    is(SLT) {
      when(sop1 < sop2) {
        signed_value := 1
      }otherwise{
        signed_value := 0
      }
    }
    is(SLTU) {
      when(op1 < op2) {
        signed_value := 1
      }otherwise{
        signed_value := 0
      }
    }
    is(XOR) {
      signed_value := sop1 ^ sop2
    }
    is(SRL) {
      unsigned_value := (op1 |>> op2)
      signed_value := unsigned_value.asSInt
    }
    is(SRA) {
      signed_value := (sop1 >> op2)
    }
    is(OR) {
      signed_value := (sop1 | sop2)
    }
    is(AND) {
      signed_value := (sop1 & sop2)
    }
    default {
      signed_value := 0
    }
  }

  io.res := bits_value(31 downto 0)
  io.status.negative := signed_value(31)
  io.status.zero := signed_value === zero
}

object ALUVerilog {
  def main(args: Array[String]): Unit = {
    SpinalVerilog(new ALU)
  }
}