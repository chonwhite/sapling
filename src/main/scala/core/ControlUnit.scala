package core

import core.OpCodes.InstructionFormat
import spinal.core._
//import core.

import scala.language.postfixOps

class ControlUnitBundle extends Bundle {
  val bus = new Bus()
}

class ControlUnit extends Component {
  noIoPrefix()
  val io = new ControlUnitBundle()

  val PC: ProgramCounter = ComponentFactory.programCounter()
  val instructionFetcher = new InstructionFetcher()
  val registerFile: RegisterFile = ComponentFactory.registerFile()
  val decoder: Decoder = ComponentFactory.decoder()
  val alu: ALU = ComponentFactory.alu()

  val data = new RegData()
  val branch = new Branch()
//  val codeGenerator = new MicroCodeGenerator(decoder)
  val memoryAccess = new MemoryAccess()

  val isBranch : Bool = Bool(true)

  //stage 0 instruction fetch
  //TODO

  //stage 1 instruction decode;
  instructionFetcher.io.address << PC.io.address
  val IFID: Stage1 = core.Stage1(isBranch, PC.io.address, instructionFetcher.io.instruction)

  // instructionFetcher -> decoder
  decoder.io.inst << IFID.instruction

  connectDecoderToRegisterFile()


  val IDEX: Stage2 = core.Stage2(isBranch, IFID.pc)

  IDEX << registerFile
  IDEX >> alu //connect to alu;

  IDEX.genControlSignals(decoder)

  //stage 2 execute
  //TODO branch;

  // EX->MEM
  val EXMEM :Stage3 = Stage3()
  EXMEM << alu

  val MEMWB : Stage4 = Stage4()
  MEMWB.aluResult := EXMEM.aluResult
  // branch
  // alu -> mem
  // alu -> registerFile
  //TODO
  registerFile.io.write.valid := data.alwaysValid
  registerFile.io.write.payload := 0
  registerFile.io.write_data := 0 //TODO
//  data.writeData <> alu.io.res
  // branch & alu -> pc
  PC.io.op <> data.pcOP
  PC.io.imm <> data.pcIMM

//  val debugger = new CUDebugger(this)

  def connectDecoderToRegisterFile(): Unit = {
    registerFile.io.read1.valid <> data.alwaysValid
    registerFile.io.read1.payload <> decoder.io.rs1
    registerFile.io.read2.valid := data.alwaysValid
    registerFile.io.read2.payload <> decoder.io.rs2
  }

  class RegData extends Area {
    val alwaysValid: Bool = Bool(true)
//    val writeData: Bits = Bits(width = 32 bits) // pc, alu_res, mem_read;

//    val aluB: Bits = Bits(32 bits)

    val pcOP: PC.io.op.type = PC.io.op.clone()
    val pcIMM: PC.io.imm.type = PC.io.imm.clone()
  }

  class Branch extends Area {
    val taken: Bool = Bool()
    val isBranchInst: Bool = decoder.io.format === InstructionFormat.BFormat
    val shouldBranch: Bool = isBranchInst && taken

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

