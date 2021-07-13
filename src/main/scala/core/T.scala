package core

import spinal.core._

class T extends Component{
//  val decoder = new Decoder()
//  val microCodeGenerator = new MicroCodeGenerator()
//
//  decoder.io.inst.valid := True
//  decoder.io.inst.payload := 0
//  microCodeGenerator.io.decoder.asInput() <> decoder.io.asInput()

}

object MicroCodeGeneratorVerilog {
  def main(args: Array[String]) {
    //    val decoder = new Decoder()
    SpinalVerilog(new T()).printPruned()
  }
}
