package isa

import core.OpCodes.ALUOpCodes
// import sim.DecoderSim.formatBin

import scala.collection.mutable.ListBuffer
import scala.util.Random

trait Instruction {
  def toBinString: String
  def toBigInt: BigInt = {
    BigInt(toBinString, 2)
  }
  var name = "unknown"

  def formatBin(str: String, args: Int*): String = {
    var list = ListBuffer[Long]()
    for (arg <- args) {
      list += arg.toBinaryString.toLong
    }
    str.format(list: _*).replaceAll("\\s", "")
  }
}

class RInstruction(rd : Int, rs1 : Int, rs2 : Int, opcode : Int) extends Instruction {

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

  override def toString : String = {
    "%s %d, %d, %d".format(name, rd, rs1, rs2)
  }
}

class IInstruction(rd : Int, rs1 : Int, imm : Int, opcode : Int) extends Instruction {
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
    formatBin(format, imm, rs1, function3, rd, opcode)
  }

  override def toString : String = {
    "%s %d, %d, %d".format(name, rd, rs1, imm)
  }

}
