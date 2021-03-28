package core

import core.OpCodes.ALUOpCodes._
import spinal.core._

class ALU extends Component {
  val io = new Bundle {
    val op = in UInt (5 bits)
    val s1 = in Bits (32 bits)
    val s2 = in Bits (32 bits)
    val res = out Bits (32 bits)
    val status = new Bundle {
      val negative = out Bool()
      val zero = out Bool()
    }
  }

  val s1 = io.s1.asSInt
  val s2 = io.s2.asSInt

  val u1 = io.s1.asUInt
  val u2 = io.s2.asUInt

  val signed_value = SInt(33 bits)
  val unsigned_value = UInt(33 bits)
  val bits_value = signed_value.asBits
  val zero = S(0, 33 bits)
  val sop1 = s1.resize(width = 33 bits)
  val sop2 = s2.resize(width = 33 bits)
  val op1 = u1.resize(width = 33 bits)
  val op2 = u2.resize(width = 33 bits)

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
      } otherwise {
        signed_value := 0
      }
    }
    is(SLTU) {
      when(op1 < op2) {
        signed_value := 1
      } otherwise {
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
  def main(args: Array[String]) {
    SpinalVerilog(new ALU)
  }
}