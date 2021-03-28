package isa

import isa.RV32I._

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.io.Source

class Assembler {

  def getLabel(line: String): String = {
    val labelIndex = line.indexOf(":")
    if (labelIndex > 0) {
      return line.substring(0, labelIndex)
    }
    null
  }

  def assembleFile(filename: String): Array[Instruction] = {
    val instructions = ArrayBuffer[Instruction]()
    val source = Source.fromFile(filename)
    var index = 0
    val codes = ArrayBuffer[String]()

    val indexMap = mutable.HashMap[String, Int]()

    for (line <- source.getLines) {
      val label = getLabel(line)
      if (label != null) {
        indexMap(label) = index
      } else {
        if (line.trim != "") {
          codes += line
          index += 1
        }
      }
    }
    source.close()

    for ((line, index) <- codes.zipWithIndex) {
      println(line)
      val instruction = parseLine(line, index, indexMap)
      instructions += instruction
    }

    println(codes)
    println(indexMap)

    instructions.toArray
  }

  def parseLine(l: String, index: Int, indexMap: mutable.HashMap[String, Int]): Instruction = {
    var line = l
    val commentIndex = line.indexOf("#")
    println(commentIndex)
    if (commentIndex > 0) {
      line = line.substring(0, commentIndex)
    }
    val name = line.split(" ")(0).toLowerCase
    val tokens = line.substring(name.length, line.length).split(",")

    var intArgs = ArrayBuffer[Int]()
    println(s"name: ${name}")
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
      val labelIndex = indexMap(tokens(2).trim)
      intArgs += (labelIndex - index) * 4
      print(intArgs)
      return BInstruction(name, intArgs: _*);
    }
    if (RV32I.isJFormat(name)) {
      intArgs += RV32I.registerNames(tokens(0).trim)
      val labelIndex = indexMap(tokens(1).trim)
      intArgs += (labelIndex - index) * 4
      return JInstruction(name, intArgs: _*)
    }
    null
  }
}

object AssemblerTest {
  def main(args: Array[String]): Unit = {
    val assembler = new Assembler()

    val instructions = assembler.assembleFile("test/led.S")
    for (inst <- instructions) {
      println(inst)
      println(inst.toBinString)
      //      println(inst.toBigInt)
    }
  }
}
