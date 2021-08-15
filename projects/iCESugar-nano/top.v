`include "ControlUnit.v"

module iCESugarNano (
  output     [7:0]    io_led,
  input               clk
);
  wire reset;
  assign reset = resetGenerator_reset;

  wire                cv_bus_address_valid;
  wire       [31:0]   cv_bus_address_payload;
  wire                cv_bus_data_valid;
  wire       [31:0]   cv_bus_data_payload;
  reg        [11:0]   resetGenerator_counter;
  reg                 resetGenerator_reset;
  wire       [31:0]   gpioAddress;
  reg        [7:0]    led;

  ControlUnit cv (
    .bus_address_valid      (cv_bus_address_valid          ), //o
    .bus_address_payload    (cv_bus_address_payload[31:0]  ), //o
    .bus_data_valid         (cv_bus_data_valid             ), //o
    .bus_data_payload       (cv_bus_data_payload[31:0]     ), //o
    .reset                  (reset                         ), //i
    .clk                    (clk                           )  //i
  );
  assign gpioAddress = 32'h00000400;
  assign io_led = led;

  initial begin
    resetGenerator_counter <= 12'h0;
    resetGenerator_reset <= 1'b1;
    led <= 8'b11111111;
  end 

  always @ (posedge clk) begin
    if((resetGenerator_counter < 12'h400))begin
      resetGenerator_reset <= 1'b1;
      resetGenerator_counter <= (resetGenerator_counter + 12'h001);
    end else begin
      resetGenerator_reset <= 1'b0;
    end
    if(cv_bus_address_valid)begin
      if((cv_bus_address_payload == gpioAddress))begin
        led <= ~cv_bus_data_payload[7 : 0];
      end
    end
  end


endmodule
