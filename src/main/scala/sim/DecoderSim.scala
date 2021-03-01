package sim

import core.Decoder
import core.OpCodes.{ALUOpCodes, BranchOpCodes}
import spinal.core.sim._

import scala.collection.mutable.ListBuffer
import scala.io.AnsiColor.{BOLD, GREEN, RESET}
import scala.util.Random

object DecoderSim {
  val ops = ListBuffer(
    ALUOpCodes.ADD,
    ALUOpCodes.SUB,
    ALUOpCodes.SLL,
    ALUOpCodes.SLT,
    ALUOpCodes.SLTU,
    ALUOpCodes.XOR,
    ALUOpCodes.SRL,
    //      ALUOpCodes.SRA,
    ALUOpCodes.OR,
    ALUOpCodes.AND
  )

  def formatBin(str: String, args: Int*): String = {
    var list = ListBuffer[Long]()
    for (arg <- args) {
      list += arg.toBinaryString.toLong
    }
    str.format(list: _*).replaceAll("\\s", "")
  }

  def RFormatSim(op: Int, dut: Decoder): Unit = {
    val format = "%07d %05d %05d %03d %05d %05d 11"
    var function7 = 0
    val rs2 = Random.nextInt(32)
    val rs1 = Random.nextInt(32)
    var function3 = op
    op match {
      case ALUOpCodes.SUB =>
        function7 = 32
        function3 = 0
      case ALUOpCodes.SRA =>
        function7 = 32
        function3 = 5
      case _ =>

    }
    val rd = Random.nextInt(32)
    val opcode = 0x0C

    val s = formatBin(format, function7, rs2, rs1, function3, rd, opcode)
    var instruction = BigInt(s, 2)

    dut.io.inst.payload #= instruction
    sleep(1)
    if (dut.io.opcodes.toInt != op) {
      println(dut.io.opcodes.toInt)
      println(op)
      println("op equal")
    }
    assert(dut.io.opcodes.toInt == op)
  }

  def IFormatSim(op: Int, dut: Decoder): Unit = {
    val format = "%012d %05d %03d %05d %05d 11"
    var imm = Random.nextInt(1 << 11 - 1)
    val rs1 = Random.nextInt(32)
    var function3 = op
    op match {
      case ALUOpCodes.SLL =>
        imm = Random.nextInt(32)
      case ALUOpCodes.SRL =>
        imm = Random.nextInt(32)
      case ALUOpCodes.SRA =>
        imm = Random.nextInt(32)
        imm = 1024 + imm // 31..26=16
        function3 = 5
      case _ =>

    }
    val rd = Random.nextInt(32)
    val opcode = 0x04

    val s = formatBin(format, imm, rs1, function3, rd, opcode)
    var instruction = BigInt(s, 2)

    dut.io.inst.payload #= instruction
    sleep(1)
    if (dut.io.opcodes.toInt != op) {
      println(dut.io.opcodes.toInt)
      println(op)
      println("op equal")
    }
    assert(dut.io.opcodes.toInt == op)
  }

  def BFormatSim(op: Int, dut: Decoder): Unit = {
    val format = "%s %05d %05d %03d %s %05d 11"
    val range = 1 << 11 - 1
    val imm = Random.nextInt(range) / 2 * 2
    val rs1 = Random.nextInt(32)
    val rs2 = Random.nextInt(32)
    val function3 = op
    val opcode = 0x18

    val immString = "%013d".format(imm.toBinaryString.toLong)
    val imm_high = immString(0) + immString.substring(2, 8)
    val imm_low = immString.substring(8, 12) + immString(1)

    val s = format.format(imm_high, rs2.toBinaryString.toLong, rs1.toBinaryString.toLong,
      function3.toBinaryString.toLong, imm_low,
      opcode.toBinaryString.toLong).replaceAll("\\s", "")
    //    println(immString)
    //    println(imm_high)
    //    println(imm_low)
    //    println(s)
    val instruction = BigInt(s, 2)
    dut.io.inst.payload #= instruction
    sleep(1)
    if (dut.io.opcodes.toInt != op) {
      println(dut.io.opcodes.toInt)
      println(op)
      println("op equal")
    }
    assert(dut.io.opcodes.toInt == op)
    println(dut.io.imm.toLong)
    println(imm)
    assert(dut.io.imm.toLong == imm)
  }

  def execSims(op: Int, callback: (Int, Decoder) => Unit): Unit = {
    SimConfig.withWave.doSim(new Decoder) { dut =>
      for (_ <- 0 to 1000) {
        if (op < 0) {
          val random_op = ops(Random.nextInt(ops.length - 1))
          callback(random_op, dut)
        } else {
          callback(op, dut)
        }
      }
    }
    println(s"$GREEN$BOLD Hello 1979! $RESET")
    println(s"OP $op Success")
  }

  def simRInstruction(op: Int) {
    execSims(op, RFormatSim)
  }

  def simIMMInstruction(op: Int) {
    execSims(op, IFormatSim)
  }

  def simBInstruction(op: Int): Unit = {
    execSims(op, BFormatSim)
  }

  def main(args: Array[String]): Unit = {
    //    val rops = ops.clone()
    //    for (op <- rops) {
    //      simRInstruction(op)
    //    }
    //    simRInstruction(-1)
    //    ops.remove(1) //remove SUB;
    //    ops += ALUOpCodes.SRA
    //
    //    for (op <- ops) {
    //      simIMMInstruction(op)
    //    }
    //    simIMMInstruction(-1)

    simBInstruction(BranchOpCodes.BEQ)
    simBInstruction(BranchOpCodes.BGE)

    //jalr;
  }
}
