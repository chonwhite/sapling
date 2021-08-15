#Sapling

Sapling is a growing attempt to build a RISC-V processor from scratch.
The first version milestone is to implement minimum working core runs on FPGA, with no peripherals(on LEDs for demonstration purposes), which means RV32I instructions and no external storage. The core utilizes on-chip memory as the main memory, the program is loaded into the memory during FPGA initialization.

I choose [SpinalHDL](https://github.com/SpinalHDL/SpinalHDL), as my programming framework.
for installation guidelines ,please follow SpinalHDL sbt guidelines: [Spinal sbt](https://github.com/SpinalHDL/SpinalTemplateSbt)


### build for iCESugar-
