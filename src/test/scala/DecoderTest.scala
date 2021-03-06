import isa.Assembler
import isa.RV32I.{IOpToName, ROpToName}
import sim.DecoderSim
import sim.SimToolkit._

import scala.io.AnsiColor.{BOLD, GREEN, RESET}

class DecoderTest extends BaseTest[DecoderSim] {
  behavior of "Decoder"

  it must "decode R instructions" in {
    for (op <- ROps) {
      execSims(op, ROpToName(op), (op, dut) => {
        dut.simRFormat(op)
      })
    }
  }

  it must "decoder Imm instructions" in {
    for (op <- ImmOps) {
      execSims(op, IOpToName(op), (op, dut) => {
        dut.simIFormat(op)
      })
    }
  }

  it must "decode assembly files" in {
    simulator.doSim { _ =>
      val assembler = new Assembler()
      val instructions = assembler.assembleFile("test/imm.s")
      for (instruction <- instructions) {
        simulator.dut.simInstruction(instruction)
      }
    }
  }

  def execSims(op: Int, testName: String, callback: (Int, DecoderSim) => Unit): Unit = {
    println(s"$GREEN$BOLD Begin Sim :${testName.toUpperCase}$RESET")
    simulator.doSim {
      dut =>
        callback(op, dut)
    }
    println(s"$GREEN$BOLD ${testName.toUpperCase} Passed$RESET")
  }
  override def dut(): DecoderSim = new DecoderSim
}
