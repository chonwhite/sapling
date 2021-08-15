package core

import isa.Assembler
import spinal.core.IntToBuilder

import scala.collection.mutable.ArrayBuffer
import scala.language.postfixOps

object GlobalConfig {

  var assemblyFile = "test/led.S"

  def cacheConfig(): CacheConfig = {
    val assembler = new Assembler()
    val instructions = assembler.assembleFile(assemblyFile)
    val codes = ArrayBuffer[BigInt]()
    for (inst <- instructions) {
      codes += inst.toBigInt
    }
    val cacheConfig = CacheConfig(
      width = 32 bits,
      depth = 4,
      rows = 32,
      content = codes.toArray
    )
    cacheConfig
  }
}
