package core

import core.OpCodes.InstructionFormat
import spinal.core._

import scala.language.postfixOps

class ControlUnitBundle extends Bundle {
  val bus = new Bus()
}

class ControlUnit extends Component {
  noIoPrefix()
  val io = new ControlUnitBundle()

  val PC = ComponentFactory.programCounter()
  val instructionFetcher = new InstructionFetcher()
  val registerFile = ComponentFactory.registerFile()
  val decoder = ComponentFactory.decoder()
  val alu = ComponentFactory.alu()

  val data = new RegData()
  val branch = new Branch()
  val codeGenerator = new MicroCodeGenerator(decoder)
  val memoryAccess = new MemoryAccess()

  // PC -> instructionFetcher
  instructionFetcher.io.address << PC.io.address
  // instructionFetcher -> decoder
  decoder.io.inst << instructionFetcher.io.instruction
  // decoder -> registerFile
  connectDecoderToRegisterFile()
  // registerFile -> ALU
  configALUInputMux()
  alu.io.op <> codeGenerator.aluOpCodes
  alu.io.s1 <> registerFile.io.read1_data
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

//  val debugger = new CUDebugger(this)

  def configALUInputMux(): Unit = {
    when(codeGenerator.aluSrc2 === MicroCodes.ALU_SRC_REG) {
      data.aluB := registerFile.io.read2_data
    } otherwise {
      data.aluB := decoder.io.imm
    }
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

    val aluB: Bits = Bits(32 bits)

    val pcOP: PC.io.op.type = PC.io.op.clone()
    val pcIMM: PC.io.imm.type = PC.io.imm.clone()
  }

  class Branch extends Area {
    val taken: Bool = Bool()
    val isBranchInst: Bool = decoder.io.format === InstructionFormat.BFormat
    val shouldBranch = isBranchInst && taken

    when(shouldBranch) {
      data.pcOP := OpCodes.PCOpCodes.ADD_OFFSET
      data.pcIMM := decoder.io.imm
    } otherwise {
      when(decoder.io.format === InstructionFormat.JFormat) {
        data.pcOP := OpCodes.PCOpCodes.SET
        data.pcIMM := decoder.io.imm
      }otherwise {
        data.pcOP := OpCodes.PCOpCodes.INCREMENT
        data.pcIMM := 4 //TODO
      }
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
        taken := !alu.io.status.negative || alu.io.status.zero
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

  class MemoryAccess extends Area {
    switch(decoder.io.format) {
      is(InstructionFormat.SFormat) {
        io.bus.address.valid := True
        io.bus.address.payload := alu.io.res

        io.bus.data.valid := True
        io.bus.data.payload := registerFile.io.read2_data
      }
      default {
        io.bus.address.valid := False
        io.bus.address.payload := 0

        io.bus.data.valid := False
        io.bus.data.payload := 0
      }
    }
  }
}

object ControlUnitVerilog {
  def main(args: Array[String]): Unit = {
    SpinalVerilog(new ControlUnit())
  }
}

