package core

import spinal.core.{Bits, Bool, IntToBuilder, Reg, RegNextWhen, UInt}
import spinal.lib.Flow

import scala.language.postfixOps

case class Control() {
  //payload is reg source selector;
  val regWrite : Flow[UInt] = Reg(Flow(UInt(width = 2 bits)))

  val memWriteEnable : Bool = Reg(Bool())
  val memSourceSelector : UInt = Reg(UInt(width = 4 bits))

  val jum : Bool = Reg(Bool())
  val branch : Bool = Reg(Bool())

  val aluSrc1Selector : Bits = Reg(Bits(width = 1 bits)) init(0)
  val aluSrc2Selector : Bits = Reg(Bits(width = 1 bits)) init(0)

  val aluOpCode : UInt = Reg(UInt(width = 5 bits))

//  val memWriteEnable
}

case class Stage1 (valid: Bool, pc: Flow[UInt], inst: Flow[Bits]){
  val PC: Flow[UInt] = RegNextWhen(pc, valid)
  val instruction: Flow[Bits] = RegNextWhen(inst, valid)
}

case class Stage2 (valid: Bool, op : UInt){
  val opCode : UInt = RegNextWhen(op, valid); //TODO
  val control : Control = Control()

  val reg1 : Bits = Reg(Bits(width = 32 bits)) //TODO
  val reg2 : Bits = Reg(Bits(width = 32 bits))
  val immediateValue : Bits = Reg(Bits(width = 32 bits)) init(0)

  val rd : UInt = Reg(UInt(width = 5 bits))

  def clear(): Unit = {
    opCode := 0
//    control := 0 //TODO
    reg1 := 0
    reg2 := 0
    immediateValue := 0
    rd := 0
  }
}

case class Stage3 (){
  val control : Control = Control()

  val aluResult : Bits = Reg(Bits(width = 32 bits))
  val reg2 : Bits = Reg(Bits(width = 32 bits))
}
