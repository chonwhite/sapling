filename = top
pcf_file = io.pcf


build:
	yosys -p "synth_ice40 -json $(filename).json -blif $(filename).blif" $(filename).v
	nextpnr-ice40 --lp1k --package cm36 --json $(filename).json --pcf $(pcf_file) --asc $(filename).asc --freq 24
	icepack $(filename).asc $(filename).bin

prog_flash:
	@if [ -d '$(ICELINK_DIR)' ]; \
        then \
            cp $(filename).bin $(ICELINK_DIR); \
        else \
            echo "iCELink not found, exit"; \
            exit 1; \
    fi


clean:
	rm -rf $(filename).blif $(filename).asc $(filename).bin
