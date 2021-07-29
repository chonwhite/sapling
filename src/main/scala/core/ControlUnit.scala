package core

import spinal.core._

import scala.language.postfixOps

class ControlUnitBundle extends Bundle {
//  val bus = new Bus()
}

class ControlUnit extends Component {
  noIoPrefix()
  val io = new ControlUnitBundle()

  val PC = new ProgramCounter()
  val instructionFetcher = new InstructionFetcher()
  val registerFile = new RegisterFile()
  val decoder = new Decoder()
  val alu = new ALU()

  val data = new RegData()
  val branch = new Branch()
  val codeGenerator = new MicroCodeGenerator(decoder)

  setIoBusDefaultValue()
  // PC -> instructionFetcher
  instructionFetcher.io.address << PC.io.address
  // instructionFetcher -> decoder
  decoder.io.inst << instructionFetcher.io.instruction
  // decoder -> registerFile
  connectDecoderToRegisterFile()
  // registerFile -> ALU
  configALUInputMux()
  alu.io.op <> decoder.io.opcodes
  alu.io.s1 <> data.aluA
  alu.io.s2 <> data.aluB
  // branch
  // alu -> mem
  // alu -> registerFile
  registerFile.io.write.valid <> codeGenerator.regWriteEnable
  registerFile.io.write.payload <> decoder.io.rd
  registerFile.io.write_data <> data.writeData
  data.writeData <> alu.io.res
  // branch & alu -> pc
  PC.io.op <> data.pcOP
  PC.io.imm <> data.pcIMM

  val debugger = new CUDebugger(this)

  def configALUInputMux(): Unit = {
    when(codeGenerator.aluSrc1 === MicroCodes.ALU_SRC_REG){
      data.aluA := registerFile.io.read1_data
    } otherwise {
      data.aluA := PC.io.pc
    }

    when(codeGenerator.aluSrc2 === MicroCodes.ALU_SRC_REG) {
      data.aluB := registerFile.io.read2_data
    } otherwise {
      data.aluB := decoder.io.imm
    }
  }

  def setIoBusDefaultValue(): Unit = {
//    io.bus.address.valid := False
//    io.bus.address.payload := 0
//    io.bus.data.valid := False
//    io.bus.data.payload := 0
  }

  def connectDecoderToRegisterFile(): Unit = {
    registerFile.io.read1.valid <> data.alwaysValid
    registerFile.io.read1.payload <> decoder.io.rs1
    registerFile.io.read2.valid := data.alwaysValid
    registerFile.io.read2.payload <> decoder.io.rs2
  }

  class RegData extends Area {
    val alwaysValid: Bool = Bool(true)
    val writeData: Bits = Bits(width = 32 bits) // pc, alu_res, mem_read;

    val aluA: Bits = Bits(32 bits)
    val aluB: Bits = Bits(32 bits)

    val pcOP: PC.io.op.type = PC.io.op.clone()
    val pcIMM: PC.io.imm.type = PC.io.imm.clone()
  }

  class Branch extends Area {
    val taken: Bool = Bool()

    when(taken) {
      data.pcOP := OpCodes.PCOpCodes.ADD_OFFSET
      data.pcIMM := decoder.io.imm
    } otherwise {
      data.pcOP := OpCodes.PCOpCodes.INCREMENT
      data.pcIMM := 0
    }

    switch(decoder.io.opcodes) {
      is(OpCodes.BranchOpCodes.BEQ) {
        taken := alu.io.status.zero
      }
      is(OpCodes.BranchOpCodes.BNE) {
        taken := !alu.io.status.zero
      }
      is(OpCodes.BranchOpCodes.BLT) {
        taken := alu.io.status.negative
      }
      is(OpCodes.BranchOpCodes.BGE) {
        taken := !alu.io.status.negative
      }
      is(OpCodes.BranchOpCodes.BLTU) {
        taken := alu.io.status.negative
      }
      is(OpCodes.BranchOpCodes.BGEU) {
        taken := !alu.io.status.negative
      }
      default {
        taken := False
      }
    }
    //TODO jump
  }
}

object ControlUnitVerilog {
  def main(args: Array[String]) {
    SpinalVerilog(new ControlUnit())
  }
}

