package sim

import core.ControlUnit
import isa.Assembler
import spinal.core.sim._

object ControlUnitSim {


  def simFile(path : String) : Unit = {
    SimConfig.withWave.compile {
      val dut = new ControlUnit()
      for (reg <- dut.instructionFetcher.instructionCache.registers) {
        reg.simPublic()
      }
      dut
    }.doSim { dut =>
      dut.clockDomain.forkStimulus(period = 10)
      val assembler = new Assembler()
      val instructions = assembler.readFile(path)

      for (index <- instructions.indices) {
        val inst = instructions(index)
        // println(inst)
        // println(inst.toBigInt.toLong.toHexString)
        dut.instructionFetcher.instructionCache.registers(index) #= inst.toBigInt
      }

      for ( index <- 0 to instructions.length) {
        dut.clockDomain.waitSampling()
      }
    }
  }

  def main(args: Array[String]): Unit = {
    simFile("test/imm.s")
//     SimConfig.withWave.compile {
//       val dut = new ControlUnit()
//       for (reg <- dut.instructionFetcher.instructionCache.registers) {
//         reg.simPublic()
//       }
//       dut
//     }.doSim { dut =>
//       dut.clockDomain.forkStimulus(period = 10)

//       val assembler = new Assembler()
//       val codes = Array(
//         "addi t1, zero, 100",
//         "addi t2, zero, 200",
//         "add  t3, t1, t2"
//       )

//       for (index <- codes.indices) {
//         val inst = assembler.parseLine(codes(index))
//         println(inst)
//         println(inst.toBigInt.toLong.toHexString)
//         dut.instructionFetcher.instructionCache.registers(index) #= inst.toBigInt
//       }

//       for ( index <- 0 to 20) {
// //        dut.io.address.valid #= true
// //        dut.io.address.payload #= index
//         //        dut.mem(0) #= 1.toLong
//         //        sleep(1)
//         dut.clockDomain.waitSampling()
//       }
//     }
  }
}
