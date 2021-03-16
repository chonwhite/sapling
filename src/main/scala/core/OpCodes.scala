package core

import scala.collection.immutable.HashMap

object OpCodes {
  object InstructionFormat {
    val RFormat = 0
    val IFormat = 1
    val SFormat = 2
    val BFormat = 3
    val UFormat = 4
    val JFormat = 5
    val ILoadFormat = 6
    val IJumpFormat = 7
  }

  object ALUOpCodes {
    val ADD = 0 // addition : rd = rs1 + rs2;
    val SUB = 8 // subtraction : rd = rs1 - rs2;
    val SLL = 1 // shift left logical : rs1 << rs2 (low five bits)
    val SLT = 2 // set (signed) less than : rs1 < rs2
    val SLTU = 3 // set less than unsigned : rs1 < rs2
    val XOR = 4 // xor : rs1 ^ rs2
    val SRL = 5 // shift right logical : rs1 >>> rs2 (low five bits)
    val SRA = 9 // shift right arithmetic : rs1 >> rs2 (low five bits) signed bits retained
    val OR = 6 // OR : rs1 | rs2
    val AND = 7 // AND : rs1 & rs2

    val codeNameMap = HashMap(
      ADD->"ADD",
      SUB->"SUB",
      SLL->"SLL",
      SLT->"SLT",
      SLTU->"SLTU",
      XOR->"XOR",
      SRL->"SRL",
      SRA->"SRA",
      OR->"OR",
      AND->"AND"
    )

    def getName(code: Int): String ={
      codeNameMap(code)
    }
  }

  object ControlOpCodes {
    val LOAD_BYTE = 0
    val LOAD_HALF = 1
    val LOAD_WORD = 2
    val LUI = 3

  }

  object StoreOpcodes {
    val STORE_BYTE = 0
    val STORE_HALF = 1
    val STORE_WORD = 2
  }

  val AUIPC = 4

  object BranchOpCodes {
    val BEQ = 0 //branch equal
    val BNE = 1 //branch not equal
    val BLT = 4 //branch less than
    val BGE = 5
    val BLTU = 6
    val BGEU = 7
  }

  object PCOpCodes {
    val INCREMENT = 0
    val ADD_OFFSET = 1
    val SET = 2
  }
}




