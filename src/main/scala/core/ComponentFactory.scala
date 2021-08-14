package core

object ComponentFactory {
  def programCounter(): ProgramCounter = {
    new ProgramCounter()
  }

  def registerFile() : RegisterFile = {
    new RegisterFile()
  }

  def decoder() : Decoder = {
    new Decoder()
  }

  def alu() : ALU = {
    new ALU()
  }
}
