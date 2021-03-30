package core

import spinal.core.{Component, SpinalVerilog, when}
import spinal.lib.bus.amba4.axi.{Axi4, Axi4Config}

class DataMemoryControl extends Component{
  val axiConfig = Axi4Config(
    addressWidth = 32,
    dataWidth    = 32,
    idWidth      = 4
  )
  val dataBus = Axi4(axiConfig)
  when(dataBus.aw.valid){
    //...
    dataBus.aw.payload.burst
  }
}

object DataMemoryControlVerilog {
  def main(args: Array[String]) {
    SpinalVerilog(new DataMemoryControl)
  }
}
