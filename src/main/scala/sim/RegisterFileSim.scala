package sim

import core.RegisterFile
import spinal.core._
import spinal.core.sim._

import scala.collection.mutable._
import scala.util.Random

object RegisterFileSim {

  class MockRegisterFile(dut : RegisterFile) {
    dut.io.read1.valid #= false
    dut.io.read1.payload #= 0
    dut.io.read2.valid #= false
    dut.io.read2.payload #= 0

    var registers = ListBuffer.empty[Long]
    var tempIndex = 0
    var tempData = 0.toLong
    for (_ <- 0 to 32){
      registers += 0
    }

    def read(index : Int): Long = {
      registers(index)
    }

    def write(index : Int, data : Long): Unit = {
      tempIndex = index
      tempData = data

      dut.io.write.valid #= true
      dut.io.write.payload #= index
      dut.io.write_data #= data
    }

    def checkMatch(): Unit = {
      for (index <- 1 to 31){
        println(index)
        print(dut.registers(index).toLong)
        if (registers(index) != dut.registers(index).toLong) {
          println(s"$index : ${registers(index)}   ${dut.registers(index).toLong} ")
        }
//        assert(registers(index) == dut.registers(index).toLong)
      }
    }

    def tick(): Unit = {
      if(tempIndex != 0) {
        registers.update(tempIndex, tempData)
      }
    }
  }

  def sim(): Unit = {
    SimConfig.withWave.compile {
      val dut = new RegisterFile(simSignal = true)
      for (reg <- dut.registers) {
        reg simPublic()
      }
      dut
    }.doSim { dut =>
      dut.clockDomain.forkStimulus(period = 10)
      val regFile = new MockRegisterFile(dut)
      var rd = 3
      var rs1 = 0
      var rs2 = 0
      for (_ <- 0 to 1) {
        val rdData = Random.nextInt(Integer.MAX_VALUE)
        regFile.write(rd, rdData)
        regFile.checkMatch()
        dut.clockDomain.waitSampling()
        regFile.tick()
        regFile.checkMatch()
        assert(regFile.read(rd) == rdData.toLong )
      }
    }
  }

  def main(args: Array[String]): Unit = {
    sim()
  }
}
