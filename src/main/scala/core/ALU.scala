package core

import core.OpCodes.ALUOpCodes._
import spinal.core._

class ALU extends Component {
  val io = new Bundle {
    val op = in UInt(5 bits)
    val s1 = in Bits(32 bits)
    val s2 = in Bits(32 bits)
    val res = out Bits(32 bits)
  }
  val out_value = UInt(32 bits)

  out_value := 0
  val sop1 = io.s1.asSInt
  val sop2 = io.s2.asSInt
  val op1 = io.s1.asUInt
  val op2 = io.s2.asUInt

  switch(io.op) {
    is(ADD) {
      out_value := op1 + op2
    }
    is(SUB) {
      out_value := op1 - op2
    }
    is(SLL) {
      out_value := op1 |<< op2 //revisit;
    }
    is(SLT) {
      when(sop1 < sop2) {
        out_value := 1
      }otherwise{
        out_value := 0
      }
    }
    is(SLTU) {
      when(op1 < op2) {
        out_value := 1
      }otherwise{
        out_value := 0
      }
    }
    is(XOR) {
      out_value := op1 ^ op2
    }
    is(SRL) {
      out_value := op1 |>> op2 //revisit;
    }
    is(SRA) {
      out_value := op1 >> op2 //revisit;
    }
    is(OR) {
      out_value := op1 | op2
    }
    is(AND) {
      out_value := op1 & op2
    }
  }

  io.res <> out_value.asBits
}

object ALUVerilog {
  def main(args: Array[String]) {
    SpinalVerilog(new ALU)
  }
}