package isa

import scala.io.Source
import isa.RV32I._

import scala.collection.mutable.ArrayBuffer

class Assembler {
  def parseLine(line : String) : Instruction = {
    val name = line.split(" ")(0).toLowerCase
    val tokens = line.substring(name.length, line.length).split(",")

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

  def readFile(filename : String) : Array[Instruction] = {
    val instructions = ArrayBuffer[Instruction]()
    val source = Source.fromFile(filename)
    for (line <- source.getLines) {
//      println(line)
      instructions += parseLine(line)
    }
    source.close()
    instructions.toArray
  }
}

object AssemblerTest {
  def main(args: Array[String]): Unit = {
    val assembler = new Assembler()

    val codes = Array(
      "addi t1, zero, 100",
      "addi t2, zero, 200",
      "add  t3, t1, t2"
    )

    for (code <- codes) {
//      println(assembler.parseLine(code))
    }
    val instructions = assembler.readFile("test/imm.s")
    for (inst <- instructions) {
      println(inst)
      println(inst.toBinString)
      println(inst.toBigInt)
    }
  }
}
