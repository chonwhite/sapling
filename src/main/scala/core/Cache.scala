package core

import spinal.core._
import spinal.lib.{master, slave}

class Cache(width : BitCount, depth : Int) extends Component{
  val io = new Bundle {
    val address = slave Flow(UInt(width = 32 bits))
    val data = master Flow(Bits(width = width))
  }
  var registers = Array.fill(depth){ Reg(Bits(width = width)) init 0 }
  val outData = Bits(width = width)
  val dataValid = Reg(Bool)
  dataValid := io.address.valid

  io.data.valid := dataValid
  io.data.payload := 0

  when(io.address.valid) {
    switch(io.address.payload >> 2) {
      for (index <- 0 until depth) {
        is(index) {
          io.data.payload := registers(index)
        }
      }
    }
  }
}

object CacheVerilog {
  def main(args: Array[String]): Unit = {
    SpinalVerilog(new Cache(32 bits, 32))
  }
}

//val mem = Mem(Bits(32 bits), wordCount = 256)
//val bitCount = log2Up(mem.wordCount)
//
//val cacheStart = Reg(UInt(32 bits)) init 0
//val offsetAddress = (io.address.payload - cacheStart).setName("offset32")(31 downto (32 - bitCount))

