package core

import spinal.core._

case class DataBlock(width: BitCount) extends Bundle {
  val valid = Bool
  val payload = UInt(width = width)
  valid := False
}

class ControlUnit extends Component {
  val signedZero = SInt(width = 32 bits)
  signedZero := 0
  val unsignedZero = UInt(width = 32 bits)
  unsignedZero := 0

  val PC = new ProgramCounter()
  val instructionFetcher = new InstructionFetcher()
  val registerFile = new RegisterFile()
  val decoder = new Decoder();
  val alu = new ALU()

  instructionFetcher.io.address << PC.io.address
  decoder.io.inst << instructionFetcher.io.instruction
  registerFile.io.write_data <> alu.io.res

  val PCData = new Area {
    val op = PC.io.op.clone()
    val imm = PC.io.imm.clone()
    op := 0
    imm := 0

    PC.io.op <> op
    PC.io.imm <> imm
  }

  val ALUData = new Area {
    val opcodes = UInt(width = alu.io.op.getWidth bits)
    val A = Bits(width = 32 bits)
    val B = Bits(width = 32 bits)

    val signedRes = alu.io.res.asSInt
    val unsignedRes = alu.io.res.asUInt

    opcodes := OpCodes.ALUOpCodes.ADD // noop
    B := 0

    A <> registerFile.io.read1_data

    alu.io.op <> opcodes
    alu.io.s1 <> A
    alu.io.s2 <> B
  }

  val Branch = new Area {
    val taken = Bool()
    taken := False

    when(taken) {
      PCData.op := OpCodes.PCOpCodes.ADD_OFFSET
      PCData.imm := decoder.io.imm
    } otherwise {
      PCData.op := OpCodes.PCOpCodes.INCREMENT
      PCData.imm := 0
    }
    //TODO jump

  }

  val regFileData = new Area {
    val reg1 = DataBlock(width = 5 bits)
    val reg2 = DataBlock(width = 5 bits)
    val write = DataBlock(width = 5 bits)

    registerFile.io.read1.valid <> reg1.valid
    registerFile.io.read1.payload <> reg1.payload
    registerFile.io.read2.valid <> reg2.valid
    registerFile.io.read2.payload <> reg2.payload
    registerFile.io.write.valid <> write.valid
    registerFile.io.write.payload <> write.payload

    reg1.payload <> decoder.io.rs1
    reg2.payload <> decoder.io.rs2
    write.payload <> decoder.io.rd

    def enableReg(reg: Bool, enable: Boolean) {
      if (enable) {
        reg := True
      } else {
        reg := False
      }
    }

    def enableResisters(enables: Array[Boolean]): Unit = {
      enableReg(reg1.valid, enables(0))
      enableReg(reg2.valid, enables(1))
      enableReg(write.valid, enables(2))
    }
  }

  switch(decoder.io.format) {
    is(OpCodes.InstructionFormat.RFormat) {
      ALUData.opcodes := decoder.io.opcodes
      regFileData.enableResisters(Array[Boolean](true, true, true))
      ALUData.B := registerFile.io.read2_data
    }
    is(OpCodes.InstructionFormat.IFormat) {
      ALUData.opcodes := decoder.io.opcodes
      regFileData.enableResisters(Array[Boolean](true, false, true))
      ALUData.B := decoder.io.imm
    }
    is(OpCodes.InstructionFormat.BFormat) {
      PCData.imm := decoder.io.imm
      ALUData.B := registerFile.io.read2_data
      ALUData.opcodes := OpCodes.ALUOpCodes.SUB
      regFileData.enableResisters(Array[Boolean](true, true, false))
      switch(decoder.io.opcodes) {
        is(OpCodes.BranchOpCodes.BEQ) {
          Branch.taken := ALUData.signedRes === signedZero
        }
        is(OpCodes.BranchOpCodes.BNE) {
          Branch.taken := ALUData.signedRes =/= signedZero
        }
        is(OpCodes.BranchOpCodes.BLT) {
          Branch.taken := ALUData.signedRes < signedZero
        }
        is(OpCodes.BranchOpCodes.BGE) {
          Branch.taken := ALUData.signedRes >= signedZero
        }
        is(OpCodes.BranchOpCodes.BLTU) {
          Branch.taken := ALUData.unsignedRes < unsignedZero
        }
        is(OpCodes.BranchOpCodes.BGEU) {
          Branch.taken := ALUData.unsignedRes >= unsignedZero
        }
      }
      when(Branch.taken) {

      }
    }
    default {

    }
  }
}

object ControlUnitVerilog {
  def main(args: Array[String]) {
    SpinalVerilog(new ControlUnit)
  }
}

