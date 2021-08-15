addi s1, zero, 1
addi s2, zero, 1
addi s3, zero, 255
slli s1, s1, 23 #iteration count
slli s2, s2, 10 #gpio address
addi a1, zero, 0 #a1 is used for led status
loop_start:
addi t1, zero, 0 #t1 is i in for loop
loop_condition:
bge t1, s1, loop_end
addi t1, t1, 1 #increment t1 by one
jal zero, loop_condition #jump to loop condition
loop_end:
beq a1, s3, set_to_zero #loop ended, check led status
addi a1, a1, 1 #add 1
jal zero, set_led
set_to_zero:
addi a1, zero, 0
set_led:
sw a1, 0(s2)
jal zero, loop_start
