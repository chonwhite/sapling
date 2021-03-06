
import org.scalatest.flatspec.AnyFlatSpec
import spinal.core.Component
import spinal.core.sim.SimConfig

trait BaseTest [T <: Component] extends AnyFlatSpec{

  def dut() : T
  def withWave = false

  var config = SimConfig
  if (withWave) {
    config = config.withWave;
  }
  val simulator = config.compile {
    dut()
  }
}
