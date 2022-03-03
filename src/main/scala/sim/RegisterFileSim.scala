package sim

import core.RegisterFile
import spinal.core._
import spinal.core.sim._

import scala.collection.mutable._

class RegisterFileSim extends RegisterFile {

  for (reg <- registers) {
    reg simPublic()
  }

  val regFile = new MockRegisterFile(this)

  class MockRegisterFile(dut : RegisterFile) {
    dut.io.read1.valid #= false
    dut.io.read1.payload #= 0
    dut.io.read2.valid #= false
    dut.io.read2.payload #= 0

    var registers: ListBuffer[Long] = ListBuffer.empty[Long]
    var tempIndex = 0
    var tempData: Long = 0.toLong
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
        if (registers(index) != dut.registers(index).toLong) {
          println(s"$index : ${registers(index)}   ${dut.registers(index).toLong} ")
        }
        assert(registers(index) == dut.registers(index).toLong)
      }
    }

    def tick(): Unit = {
      if(tempIndex != 0) {
        registers.update(tempIndex, tempData)
      }
    }
  }
}
