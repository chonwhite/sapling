package isa

import Riscv._
import spinal.core._

import scala.language.postfixOps

class SD extends Component{
  val ss = U"32'h1A"

//  val ssm = ss === ADD

//  val opcode = UInt(2 bits)
  val opcode: UInt = UInt(width = 3 bits)

  when(ss === ADD) {
    opcode := U"01"
  }
  when(ss === SUB) {
    opcode := U"02"
  }
  when(ss === BEQ) {
    opcode := U"03"
  }
//  switch(ss){
//    is(ADD) {
//      opcode := U"01"
//    }
//    is(SUB) {
//      opcode := U"01"
//    }
//    is(BEQ(true)) {
//      opcode := U"03"
//    }
//  }
//  val opcode = ss.mux(
//    ADD -> U"01",
//    SUB -> U"01",
//    BEQ(true) -> U"01",
//    default -> U"00"
//  )

}

object SDVerilog {
  def main(args: Array[String]): Unit = {
    SpinalVerilog(new SD)
  }
}
