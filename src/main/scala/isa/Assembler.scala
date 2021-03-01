package isa

class Assembler {

  def parseLine(line : String) : Instruction = {
    val tokens = line.split(" ")
    val name = tokens(0).toLowerCase

    if(RV32I.RInstructions.contains(name)) {

    }
    println(name)
    null
  }
}

object AssemblerTest {
  def main(args: Array[String]): Unit = {
    val assmebler = new Assembler()
    assmebler.parseLine("add a1, a2, a3")
  }
}
