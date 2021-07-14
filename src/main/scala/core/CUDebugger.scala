package core

import mafufactor.{AluILABundle, DecoderILABundle}
import spinal.core._

import scala.language.postfixOps

class CUILABundle extends Bundle {
  val clk: Bool = in Bool()
  val decoder = new DecoderILABundle()
  val alu = new AluILABundle()
}

class CUILA extends BlackBox {
  val io = new CUILABundle()
  noIoPrefix()
}

class CUDebugger(cu: ControlUnit) extends Area{
  val debugger = new CUILA()
  debugger.io.clk <> ClockDomain.current.readClockWire
  debugger.io.decoder.clk <> ClockDomain.current.readClockWire
  debugger.io.decoder.inst << cu.decoder.io.inst
  debugger.io.decoder.format <> cu.decoder.io.format
  debugger.io.decoder.opcodes <> cu.decoder.io.opcodes
  debugger.io.decoder.rd <> cu.decoder.io.rd
  debugger.io.decoder.rs1 <> cu.decoder.io.rs1
  debugger.io.decoder.rs2 <> cu.decoder.io.rs2
  debugger.io.decoder.imm <> cu.decoder.io.imm

  debugger.io.alu.clk <> ClockDomain.current.readClockWire
  debugger.io.alu.op <> cu.decoder.io.opcodes
  debugger.io.alu.s1 <> cu.data.aluA
  debugger.io.alu.s2 <> cu.data.aluB
  debugger.io.alu.res <> cu.alu.io.res
  debugger.io.alu.status.zero <> cu.alu.io.status.zero
  debugger.io.alu.status.negative <> cu.alu.io.status.negative
}
