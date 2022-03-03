package core

import spinal.core._
import spinal.lib.bus.amba4.axi.{Axi4, Axi4Config}
import spinal.lib.master

import scala.language.postfixOps

class AxiTest extends Component{
  var config: Axi4Config = Axi4Config (
    addressWidth = 32,
    dataWidth = 128,
    useQos = false,
    useLock = false,
    useId = false,
    useProt = false,
    useRegion = false,
    useCache = false,
    useStrb = false
  )

  val io = new Bundle {
    val axi_m: Axi4 = master (Axi4(config))
  }

  val data: Bits = Bits(width = 128 bits)
  data := 0

  io.axi_m.r.ready := True

  io.axi_m.aw.payload.addr := 0
//  io.axi_m.aw.payload.prot := 0

  io.axi_m.aw.valid := True
  io.axi_m.aw.burst := 0
  io.axi_m.aw.payload.size := 0
  io.axi_m.aw.payload.len := 0
  io.axi_m.b.ready := True
  io.axi_m.w.last := True
  io.axi_m.w.payload.data := 0
  io.axi_m.w.valid := True

  io.axi_m.ar.payload.addr := 0
  io.axi_m.ar.valid := True
  io.axi_m.ar.burst := 0
  io.axi_m.ar.payload.size := 0
  io.axi_m.ar.payload.len := 0
}

object AxiVerilog {
  def main(args: Array[String]): Unit = {
    SpinalVerilog(new AxiTest)
  }
}
