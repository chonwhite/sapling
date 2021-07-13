
module CUILA (
  input   [0:0]  clk                  , //i
  input   [0:0]  decoder_clk          , //i
  input   [0:0]  decoder_inst_valid   , //i
  input   [31:0] decoder_inst_payload , //i
  input   [4:0]  decoder_opcodes      , //i
  input   [2:0]  decoder_format       , //i
  input   [4:0]  decoder_rd           , //i
  input   [4:0]  decoder_rs1          , //i
  input   [4:0]  decoder_rs2          , //i
  input   [31:0] decoder_imm          , //i
  input   [0:0]  alu_clk              , //i
  input   [4:0]  alu_op               , //i
  input   [31:0] alu_s1               , //i
  input   [31:0] alu_s2               , //i
  input   [31:0] alu_res              , //i
  input   [0:0]  alu_status_negative  , //i
  input   [0:0]  alu_status_zero        //i
);

  


endmodule