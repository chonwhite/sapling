addi s1, zero, 1
slli s1, s1, 2 #iteration count
addi a1, zero, 0 #a1 is used for led status
loop_start:
addi t1, zero, 0 #t1 is i in for loop
loop_condition:
bge t1, s1, loop_end
addi t1, t1, 1 #increment t1 by one
jal zero, loop_condition #jump to loop condition
loop_end:
beq a1, zero, set_to_one #loop ended, check led status
addi a1, zero, 0
jal zero, set_led
set_to_one:
addi a1, zero, 1 #set to one
set_led:
sw a1, 2045(zero)
jal zero, loop_start
