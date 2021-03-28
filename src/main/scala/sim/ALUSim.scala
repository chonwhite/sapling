package sim

import core.ALU
import core.OpCodes.ALUOpCodes
import sim.SimToolkit._
import spinal.core.sim._

import scala.util.Random

class ALUSim extends ALU {
  var A = BigInt(0.toBinaryString, 2)
  var B = BigInt(0.toBinaryString, 2)

  def add(a: Int, b: Int): Int = {
    io.s1 #= a
    io.s2 #= b
    io.res.toInt
  }

  def simOp(op: Int): Unit = {
    val a = Random.nextInt()
    var b = Random.nextInt()
    if (shiftOps.contains(op)) {
      b = Random.nextInt(32)
    }
    A = BigInt(a.toBinaryString, 2)
    B = BigInt(b.toBinaryString, 2)

    io.op #= op
    io.s1 #= A
    io.s2 #= B
    sleep(cycles = 1)
    println("===")
    println(io.res.toBigInt.toInt)
    println(eval(op).toInt)

    assert(io.res.toBigInt.toInt == eval(op).toInt)
    assert(io.status.negative.toBoolean == negative(op))
  }

  def negative(op: Int): Boolean = {
    eval(op).toInt < 0
  }

  def eval(op: Int): Long = {
    var result = 0L
    op match {
      case ALUOpCodes.ADD => result = A.toInt + B.toInt
      case ALUOpCodes.SUB => result = A.toInt - B.toInt
      case ALUOpCodes.SLL => result = A.toLong << B.toInt
      case ALUOpCodes.SLT =>
        if (A.toInt < B.toInt) {
          result = 1
        } else {
          result = 0
        }
      case ALUOpCodes.SLTU =>
        if (A.toLong < B.toLong) {
          result = 1
        } else {
          result = 0
        }
      case ALUOpCodes.XOR => result = A.toInt ^ B.toInt
      case ALUOpCodes.SRL => result = A.toLong >> B.toInt
      case ALUOpCodes.SRA => result = A.toInt >> B.toInt
      case ALUOpCodes.OR => result = A.toLong | B.toLong
      case ALUOpCodes.AND => result = A.toLong & B.toLong

    }
    result
  }
}


