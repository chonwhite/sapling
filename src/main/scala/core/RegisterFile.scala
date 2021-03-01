package core

import spinal.core._
import spinal.lib.slave

class RegisterFile(simSignal : Boolean = false) extends Component {
  val io = new Bundle {
    val read1 = slave Flow(UInt(width = 5 bits))
    val read2 = slave Flow(UInt(width = 5 bits))
    val read1_data = out Bits(width = 32 bits)
    val read2_data = out Bits(width = 32 bits)

    val write = slave Flow(UInt(width = 5 bits))
    val write_data = in Bits(width = 32 bits)
  }
  io.read1_data := 0
  io.read2_data := 0

  def setDefaultValues(): Unit = {
    io.write.valid := False
    io.write.payload := 0
    io.write_data := 0
  }

  var registers = Array.fill(32){ Reg(Bits(width = 32 bits)) init 0 }

  when(io.read1.valid) {
    readReg(io.read1.payload, io.read1_data)
  }

  when(io.read2.valid) {
    readReg(io.read2.payload, io.read1_data)
  }

  when(io.write.valid) {
    switch(io.write.payload) {
      for (index <- 1 to 31) {
        is(index) {
          registers(index) := io.write_data
        }
      }
    }
  }

  def readReg(index : UInt, dest : Bits): Unit = {
    switch(index) {
      for (index <- 1 to 31) {
        is(index) {
          dest := registers(index)
        }
      }
    }
  }
}

object RegisterFileVerilog {
  def main(args: Array[String]) {
    SpinalVerilog(new RegisterFile)
  }
}
