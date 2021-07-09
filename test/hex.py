s = open("ss.txt", "r")
lines = s.readlines()
# print(lines)

for line in lines:
	value = int(line,16)
	str_value = '{0:032b}'.format(value)
	print(str_value)