package isa

import isa.RV32I._

import scala.collection.mutable.ArrayBuffer
import scala.io.Source

class Assembler {
  def assembleFile(filename: String): Array[Instruction] = {
    val instructions = ArrayBuffer[Instruction]()
    val source = Source.fromFile(filename)
    for (line <- source.getLines) {
      instructions += parseLine(line)
      println(line)
    }
    source.close()
    instructions.toArray
  }

  def parseLine(line: String): Instruction = {
    val name = line.split(" ")(0).toLowerCase
    val tokens = line.substring(name.length, line.length).split(",")

    var intArgs = ArrayBuffer[Int]()
    if (RV32I.isRFormat(name)) {
      for (t <- tokens) {
        intArgs += RV32I.registerNames(t.trim)
      }
      return RInstruction(name, intArgs: _*);
    }
    if (RV32I.isIFormat(name)) {
      intArgs += RV32I.registerNames(tokens(0).trim)
      intArgs += RV32I.registerNames(tokens(1).trim)
      intArgs += tokens(2).trim.toInt
      return IInstruction(name, intArgs: _*);
    }
    if (RV32I.isBFormat(name)) {
      intArgs += RV32I.registerNames(tokens(0).trim)
      intArgs += RV32I.registerNames(tokens(1).trim)
      intArgs += tokens(2).trim.toInt
      return BInstruction(name, intArgs: _*);
    }
    null
  }
}

object AssemblerTest {
  def main(args: Array[String]): Unit = {
    val assembler = new Assembler()


    val instructions = assembler.assembleFile("test/Branch.S")
    for (inst <- instructions) {
      println(inst)
      println(inst.toBinString)
      println(inst.toBigInt)
    }
  }
}
