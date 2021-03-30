package sim

import core.OpCodes.ALUOpCodes

object SimToolkit {

  val shiftOps = Array(
    ALUOpCodes.SLL,
    ALUOpCodes.SRL,
    ALUOpCodes.SRA
  )

  val ImmOps = Array(
    ALUOpCodes.ADD,
    ALUOpCodes.SLL,
    ALUOpCodes.SLT,
    ALUOpCodes.SLTU,
    ALUOpCodes.XOR,
    ALUOpCodes.SRL,
    ALUOpCodes.SRA,
    ALUOpCodes.OR,
    ALUOpCodes.AND
  )

  val ROps = ImmOps :+ ALUOpCodes.SUB

  def bits32ToInt(value: Long): Int = {
    val binString = value.toBinaryString
    if (binString.length < 32 || binString.charAt(0) == '0') {
      return Integer.parseInt(binString, 2)
    }
    val invertedInt = binString.map(c => if(c == '0') '1' else '0')
    var decimalValue = Integer.parseInt(invertedInt, 2)
    decimalValue = (decimalValue + 1) * -1
    decimalValue
  }
}
