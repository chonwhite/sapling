package isa

import isa.RV32I._

import scala.collection.mutable.ArrayBuffer

class Assembler {

  def parseLine(line : String) : Instruction = {
    val name = line.split(" ")(0).toLowerCase
    val tokens = line.substring(name.length, line.length).split(",")

    println(name)

    var intArgs = ArrayBuffer[Int]()
    if(RV32I.isRFormat(name)) {
      for(t <- tokens) {
        intArgs += RV32I.registerNames(t.trim)
      }
      return RInstruction(name, intArgs:_*);
    }
    if(RV32I.isIFormat(name)) {
      intArgs += RV32I.registerNames(tokens(0).trim)
      intArgs += RV32I.registerNames(tokens(1).trim)
      intArgs += tokens(2).trim.toInt
      return IInstruction(name, intArgs:_*);
    }

    null
  }
}

object AssemblerTest {
  def main(args: Array[String]): Unit = {
    val assmebler = new Assembler()

    println(assmebler.parseLine("addi t1, zero, 100"))
    println(assmebler.parseLine("addi t2, zero, 20"))
    println(assmebler.parseLine("add t3, t1, t2"))
  }
}
