package boards

import core.{ControlUnit, GlobalConfig}
import spinal.core._

import sys.process._
import scala.language.postfixOps

class ResetGenerator extends Area {
  val counter = Reg(UInt(width = 12 bits)) init 0
  val reset = Reg(Bool) init True
  when(counter < 1024) {
    reset := True
    counter := counter + 1
  } otherwise {
    reset := False
  }
}

class iCESugarNanoBundle extends Bundle {
  val key: Bits = in Bits (width = 4 bits)
  val led: Bits = out Bits (width = 4 bits)
}

class iCESugarNano extends Component {
  val io = new iCESugarNanoBundle()
  val resetGenerator = new ResetGenerator()

  val gpioAddress = U"32'd1024".asBits

  val led = Reg(Bits(width = 8 bits)) init 0

  val cv = new ControlUnit()
  when(cv.io.bus.address.valid) {
    when(cv.io.bus.address.payload === gpioAddress) {
      led := cv.io.bus.data.payload(7 downto 0)
    }
  }

  io.led := led
}

object iCESugarNanoVerilog {

  def main(args: Array[String]): Unit = {

    GlobalConfig.assemblyFile = "test/led.S"

    val projectPath = "projects/iCESugar-nano"
    SpinalConfig(mode=Verilog, targetDirectory=projectPath).generate(new ControlUnit)
    
    val fpga_tool_chain_path = ""
    var cmd: String = ""
    if (fpga_tool_chain_path != "") {
      cmd = cmd + s"export PATH=$$PATH:$fpga_tool_chain_path" + "\n"
    }
    cmd = cmd + "make"
    import java.io._
    val pw = new PrintWriter(new File(projectPath + "/build.sh" ))
    pw.write(cmd)
    pw.close()

    println("building bitstream ,It is going to take a while......")
    val output = Process("sh build.sh", new File(projectPath)).!!
    println(output)
  }
}
