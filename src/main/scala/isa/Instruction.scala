package isa

import core.OpCodes.{ALUOpCodes, InstructionFormat}

import scala.collection.mutable.ListBuffer

trait Instruction {

  var name = "unknown"

  def instructionFormat(): Int

  def toBinString: String

  def toBigInt: BigInt = {
    BigInt(toBinString, 2)
  }

  def formatBin(str: String, args: Int*): String = {
    var list = ListBuffer[Long]()
    for (arg <- args) {
      val str = arg.toBinaryString
      val number = Integer.parseUnsignedInt(str, 2)
      list += number.toBinaryString.toLong
    }
    str.format(list: _*).replaceAll("\\s", "")
  }

  def toBinary(x: Int, len: Int) = {
    val result = new StringBuilder
    for (i <- len - 1 to 0 by -1) {
      val mask = 1 << i
      result.append(if ((x & mask) != 0) 1 else 0)
    }
//    print(result.toString())
    result.toString
  }

  def backward(idx: Int, str: String): String = {
    val s = str(str.length - idx - 1)
//    print(s)
    s.toString
  }

  def backward(up: Int, down: Int, str: String): String = {
    str.substring(str.length - up - 1, str.length - down)
  }
}

class RInstruction(val rd: Int, val rs1: Int, val rs2: Int, val opcode: Int) extends Instruction {
  override def instructionFormat(): Int = InstructionFormat.RFormat

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

class IInstruction(val rd: Int, val rs1: Int, val imm: Int, val opcode: Int) extends Instruction {

  override def instructionFormat(): Int = InstructionFormat.IFormat

  override def toBinString: String = {
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

class BInstruction(val rs1: Int, val rs2: Int, val imm: Int, val opcode: Int) extends Instruction {
  override def instructionFormat(): Int = InstructionFormat.BFormat

  override def toBinString: String = {
    val immString = toBinary(imm, 13)

    backward(12, immString) + backward(10, 5, immString) + toBinary(rs2, 5) +
      toBinary(rs1, 5) + toBinary(opcode, 3) + backward(4, 1, immString) +
      backward(11, immString) + toBinary(0x18, 5) + "11"
  }

  override def toString: String = {
    "%s %d, %d, %d".format(name, rs2, rs1, imm)
  }
}

class JInstruction(val rd: Int, val imm : Int) extends Instruction {
  override def instructionFormat() : Int = InstructionFormat.JFormat

  name = "jal"

  override def toBinString : String = {
    val immString = toBinary(imm, len = 21)
    backward(idx = 20, immString) + backward(10, 1, immString) +
      backward(11, immString) + backward(19, 12, immString) +
      toBinary(rd, 5) + toBinary(0x1b, 5) + "11"
  }

  override def toString: String = {
    "jal %d, %d".format(rd, imm)
  }
}

class SInstruction(val rs1: Int, val rs2: Int, val imm: Int, val opcode: Int) extends Instruction {
  override def instructionFormat() : Int = InstructionFormat.SFormat

  override def toBinString : String = {
    val immString = toBinary(imm, len = 12)
    val function3 = opcode
    backward(11, 5, immString) + toBinary(rs2, 5) + toBinary(rs1, 5) +
      toBinary(function3, 3) + backward(4, 0, immString) + toBinary(0x08, 5) + "11"
  }

  override def toString = {
    "%s %d %d(%d)".format(name, rs2, imm, rs1)
  }
}
