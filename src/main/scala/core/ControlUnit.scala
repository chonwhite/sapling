package core

import spinal.core._

import scala.language.postfixOps

case class DataBlock(width: BitCount) extends Bundle {
  val valid = Bool
  val payload = UInt(width = width)
  valid := False
}

class ControlUnit extends Component {

  val io = new Bundle{
    val bus = new Bus()
  }

  val signedZero = SInt(width = 32 bits)
  signedZero := 0
  val unsignedZero = UInt(width = 32 bits)
  unsignedZero := 0

  val PC = new ProgramCounter()
  val instructionFetcher = new InstructionFetcher()
  val registerFile = new RegisterFile()
  val decoder = new Decoder();
  val alu = new ALU()

  io.bus.address.valid := False
  io.bus.address.payload := 0
  io.bus.data.valid := False
  io.bus.data.payload := 0

  instructionFetcher.io.address << PC.io.address
  decoder.io.inst << instructionFetcher.io.instruction
  registerFile.io.write_data <> alu.io.res

  val PCData = new Area {
    val op = PC.io.op.clone()
    val imm = PC.io.imm.clone()
    val address = PC.io.pc.clone()
    address := PC.io.pc


    op := 0
    imm := 0

    PC.io.op <> op
    PC.io.imm <> imm
  }

  val ALUData = new Area {
    val opcodes = UInt(width = alu.io.op.getWidth bits)
    val A = Bits(width = 32 bits)
    val B = Bits(width = 32 bits)

    val zero = alu.io.status.zero
    val negative = alu.io.status.negative

    opcodes := OpCodes.ALUOpCodes.ADD // noop
    B := 0

    A <> registerFile.io.read1_data
    when(decoder.io.format === OpCodes.InstructionFormat.UFormat) {
      A := PC.io.pc.asBits
    }

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
      regFileData.enableResisters(Array[Boolean](true, true, true))
      ALUData.opcodes := decoder.io.opcodes
      ALUData.B := registerFile.io.read2_data
    }
    is(OpCodes.InstructionFormat.IFormat) {
      regFileData.enableResisters(Array[Boolean](true, false, true))
      ALUData.opcodes := decoder.io.opcodes
      ALUData.B := decoder.io.imm
    }
    is(OpCodes.InstructionFormat.BFormat) {
      regFileData.enableResisters(Array[Boolean](true, true, false))
      PCData.imm := decoder.io.imm
      ALUData.B := registerFile.io.read2_data
      ALUData.opcodes := OpCodes.ALUOpCodes.SUB
      //TODO move to ALU?

      switch(decoder.io.opcodes) {
        is(OpCodes.BranchOpCodes.BEQ) {
          Branch.taken := ALUData.zero
        }
        is(OpCodes.BranchOpCodes.BNE) {
          Branch.taken := !ALUData.zero
        }
        is(OpCodes.BranchOpCodes.BLT) {
          Branch.taken := ALUData.negative
        }
        is(OpCodes.BranchOpCodes.BGE) {
          Branch.taken := !ALUData.negative
        }
        is(OpCodes.BranchOpCodes.BLTU) {
          Branch.taken := ALUData.negative
        }
        is(OpCodes.BranchOpCodes.BGEU) {
          Branch.taken := !ALUData.negative
        }
      }
    }
    is(OpCodes.InstructionFormat.UFormat) {
      regFileData.enableResisters(Array[Boolean](true, false, true))
      // rs1 should be zero
      ALUData.opcodes := OpCodes.ALUOpCodes.ADD
      ALUData.B := decoder.io.imm
      switch(decoder.io.opcodes) {
        is(OpCodes.AUIPC) {
          ALUData.A := PC.io.pc.asBits
        }
      }
    }
    is(OpCodes.InstructionFormat.JFormat) {
      regFileData.enableResisters(Array[Boolean](false, false, true))

      ALUData.opcodes := OpCodes.ALUOpCodes.ADD
      ALUData.A := PCData.address.asBits
      ALUData.B := 4
      PCData.imm := decoder.io.imm
      PCData.op := OpCodes.PCOpCodes.ADD_OFFSET
    }
    is(OpCodes.InstructionFormat.SFormat) {
      regFileData.enableResisters(Array[Boolean](true, true, false))
      io.bus.address.valid := True
      switch(decoder.io.opcodes) {
        is (OpCodes.StoreOpcodes.STORE_WORD) {
          io.bus.address.payload := decoder.io.imm.asUInt
          io.bus.data.payload := registerFile.io.read2_data
        }
      }
    }
    default {

    }
  }

}

object ControlUnitVerilog {
  def main(args: Array[String]) {
    SpinalVerilog(new ControlUnit())
  }
}

