package sim

import core.ALU
import core.OpCodes.ALUOpCodes
import spinal.core.sim._

import scala.util.Random

object ALUSim {

  val shift_ops = List(ALUOpCodes.SLL, ALUOpCodes.SRL, ALUOpCodes.SRA)

  def sim_op(op: Int) {
    SimConfig.withWave.doSim(new ALU) { dut =>
      //Fork a process to generate the reset and the clock on the dut
      dut.clockDomain.forkStimulus(period = 10)
      var resultModel = BigInt(0.toBinaryString, 2)
      var lastA = BigInt(0.toBinaryString, 2)
      var lastB = BigInt(0.toBinaryString, 2)
      for (_ <- 0 to 100) {
        //Drive the dut inputs with random values
        var a = BigInt(Random.nextInt().toBinaryString, 2)
        var b = BigInt(Random.nextInt().toBinaryString, 2)
        if (shift_ops.contains(op)) {
          b = BigInt(Random.nextInt(32).toBinaryString, 2)
        }

        dut.io.op #= op
        dut.io.s1 #= a
        dut.io.s2 #= b

        dut.clockDomain.waitSampling()
        assert(dut.io.res.toBigInt.toInt == resultModel.toInt)

        op match {
          case ALUOpCodes.ADD => resultModel = a.toInt + b.toInt
          case ALUOpCodes.SUB => resultModel = a.toInt - b.toInt
          case ALUOpCodes.SLL => resultModel = a.toInt << b.toInt
          case ALUOpCodes.SLT =>
            if (a.toInt < b.toInt) {
              resultModel = 1
            } else {
              resultModel = 0
            }
          case ALUOpCodes.SLTU =>
            if (a.toLong < b.toLong) {
              resultModel = 1
            } else {
              resultModel = 0
            }
          case ALUOpCodes.XOR => resultModel = a.toInt ^ b.toInt
          case ALUOpCodes.SRL => resultModel = a.toLong >> b.toInt
          case ALUOpCodes.SRA => resultModel = a.toInt >> b.toInt
          case ALUOpCodes.OR => resultModel = a.toLong | b.toLong
          case ALUOpCodes.AND => resultModel = a.toLong & b.toLong

        }
        lastA = a
        lastB = b
      }
    }
    println("op %d success!".format(op))
  }

  def main(args: Array[String]) {
    val ops = Array(
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
    for (op <- ops) {
      sim_op(op)
    }
    //    sim_op(ALUOpCodes.ADD)
    //    sim_op(ALUOpCodes.SUB)
    //    sim_op(ALUOpCodes.SLL)
    //    sim_op(ALUOpCodes.SLT)
    //    sim_op(ALUOpCodes.SLTU)
    //    sim_op(ALUOpCodes.XOR)
    //    sim_op(ALUOpCodes.SRL)
    ////    sim_op(ALUOpCodes.SRA)
    //    sim_op(ALUOpCodes.OR)
    //    sim_op(ALUOpCodes.AND)
  }
}
