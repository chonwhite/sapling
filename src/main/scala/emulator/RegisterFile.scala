package emulator

import scala.collection.mutable.ArrayBuffer

class RegisterFile {
  protected var registers: ArrayBuffer[Long] = ArrayBuffer[Long]()
  for (_ <- 0 to 31) {
    registers += 0L
  }

  def setRegister(index: Int, value: Long): Unit = {
    if (index != 0) {
      registers(index) = value
    }
  }

  def getRegister(index: Int): Long = {
    registers(index)
  }
}
