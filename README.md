#Sapling

Sapling is a growing attempt to build a RISC-V processor from scratch.
The first version milestone is to implement minimum working core runs on FPGA, with no peripherals(on LEDs for demonstration purposes), which means RV32I instructions and no external storage. The core utilizes on-chip memory as the main memory, the program is loaded into the memory during FPGA initialization.

I choose [SpinalHDL](https://github.com/SpinalHDL/SpinalHDL), as my programming framework.
for installation guidelines ,please follow SpinalHDL sbt guidelines: [Spinal sbt](https://github.com/SpinalHDL/SpinalTemplateSbt)


### build for iCESugar-

[iCESugar-nano](https://github.com/wuxx/icesugar-nano) is a very tiny open-source FPGA developer board, since it is based on Lattice iCE40 FPGA chip, it can be compiled using open-source toolchain (yosys & nextpnr & icestorm).

change fpga\_tool\_chain\_path to [fpga_toolchain](https://github.com/YosysHQ/fpga-toolchain) directory path in src/main/scala/boards/iCESugarNano.scala and execute the main function,it would compile scala codes to verilog and copy into projects/iCESugar-nano directory and invoke yosys to generate bitstream file;
