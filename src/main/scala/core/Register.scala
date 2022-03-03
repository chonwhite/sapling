package core

import spinal.core._
import spinal.lib._

import scala.language.postfixOps

class Register extends Component{
  class RegisterBundle extends Bundle {
    val read1: UInt = in UInt(width = 5 bits)
    val read2: UInt = in UInt(width = 5 bits)
    val read1_data: Bits = out Bits(width = 32 bits)
    val read2_data: Bits = out Bits(width = 32 bits)

    val write: Flow[UInt] = slave Flow UInt(width = 5 bits)
    val write_data: Bits = in Bits(width = 32 bits)
  }
  val io = new RegisterBundle()

  val mem: Mem[Bits] = Mem(Bits(32 bits), wordCount = 32)
//  mem.initialContent = 0
  io.read1_data := mem.readAsync(io.read1)
  io.read2_data := mem.readAsync(io.read2)
  mem.write(io.write.payload, io.write_data, io.write.valid)
}

object RegisterVerilog {
  def main(args: Array[String]): Unit = {
    SpinalVerilog(new Register)
  }
}
