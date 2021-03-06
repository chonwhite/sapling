package core

import spinal.core._
import spinal.lib.{master, slave}

case class CacheConfig(
  width : BitCount,
  depth : Int,
  rows : Int
) {
  def lineWidth : BitCount = {
    width.value * depth bits
  }
}

case class Tag() extends Bundle {
  val valid = Reg(Bool) init False
  val dirty = Reg(Bool) init False
  val tag = UInt(width = 8 bits)
}

class Cache(config :CacheConfig) extends Component{
  val io = new Bundle {
    val address = slave Flow(UInt(width = 32 bits))
    val data = master Flow(Bits(width = config.width))
  }

  var tags = Array.fill(config.rows) {Tag()}

//  when(ClockDomain.current.isResetActive){
    for(tag <- tags) {
      tag.valid := False
      tag.dirty := False
      tag.tag := 0
    }
//  }

  var registers = Array.fill(config.rows){ Reg(Bits(width = config.lineWidth)) init 0 }
  val outData = Bits(width = config.lineWidth)
  val dataValid = (Bool)
  val lowBits = UInt(width = 2 bits)
  val highBits = UInt(width = 8 bits)
  lowBits := io.address.payload(3 downto 2)
  highBits := io.address.payload(11 downto 4)

  dataValid := io.address.valid
  io.data.valid := dataValid
  outData := 0

  when(io.address.valid) {
    switch(highBits) {
      for (index <- 0 until config.rows) {
        is(tags(index).tag) {
          outData := registers(index)
        }
      }
    }
  }

  io.data.payload := lowBits.mux(
    0 -> outData(127 downto 96),
    1 -> outData(95 downto 64),
    2 -> outData(63 downto 32),
    3 -> outData(31 downto 0)
  )
}

object CacheVerilog {
  def main(args: Array[String]): Unit = {
//    SpinalVerilog(new Cache(32 bits, 32))
    val cacheConfig = CacheConfig(
      width = 32 bits,
      depth = 4,
      rows = 32
    );
    SpinalConfig(device = Device.XILINX)
      .addStandardMemBlackboxing(blackboxAll)
      .generateVerilog(new Cache(cacheConfig))
  }
}


//val mem = Mem(Bits(32 bits), wordCount = 256)
//mem.setTechnology(ramBlock)
//io.data.payload := mem.readAsync(255)
//mem.write(22,22)