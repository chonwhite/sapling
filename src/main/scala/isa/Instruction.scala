package isa

import core.OpCodes.ALUOpCodes

import scala.collection.mutable.ListBuffer

trait Instruction {
  def toBinString: String

  def toBigInt: BigInt = {
    BigInt(toBinString, 2)
  }

  var name = "unknown"

  def formatBin(str: String, args: Int*): String = {
    var list = ListBuffer[Long]()
    for (arg <- args) {
      val str = arg.toBinaryString
      val number = Integer.parseUnsignedInt(str, 2)
      println(number)
      list += number.toBinaryString.toLong
    }
    str.format(list: _*).replaceAll("\\s", "")
  }

  def toBinary(x: Int, len: Int) = {
    val result = new StringBuilder
    for (i <- len - 1 to 0 by -1) {
      val mask = 1 << i
      result.append(if ((x & mask) != 0) 1
      else 0)
    }
    result.toString
  }
}

class RInstruction(rd: Int, rs1: Int, rs2: Int, opcode: Int) extends Instruction {

  override def toBinString: String = {
    val format = "%07d %05d %05d %03d %05d %05d 11"
    var function7 = 0
    var function3 = this.opcode
    this.opcode match {
      case ALUOpCodes.SUB =>
        function7 = 32
        function3 = 0
      case ALUOpCodes.SRA =>
        function7 = 32
        function3 = 5
      case _ =>

    }
    val opcode = 0x0C


    formatBin(format, function7, rs2, rs1, function3, rd, opcode)
  }

  override def toString: String = {
    "%s %d, %d, %d".format(name, rd, rs1, rs2)
  }
}

class IInstruction(rd: Int, rs1: Int, imm: Int, opcode: Int) extends Instruction {
  override def toBinString: String = {
    val format = "%012d %05d %03d %05d %05d 11"
    var function3 = this.opcode
    var imm = this.imm
    this.opcode match {
      case ALUOpCodes.SRA =>
        imm = 1024 + this.imm // 31..26=16
        function3 = 5
      case _ =>
    }
    val opcode = 0x04
    toBinary(imm, 12) + toBinary(rs1, 5) + toBinary(function3, 3) +
      toBinary(rd, 5) + toBinary(opcode, 5) + "11"
  }

  override def toString: String = {
    "%s %d, %d, %d".format(name, rd, rs1, imm)
  }

}
