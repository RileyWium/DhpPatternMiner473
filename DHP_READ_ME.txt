# DhpPatternMiner473
A program which finds patterns in large datasets. Allows the user to search for commonly grouped items using the DHP algorithm.

Compilation:
0) open cmd line
1) navigate to folder location
2) run "javac DHPproj.java" to build class file

Execution:
0) open cmd line
1) navigate to folder of class file
2) run "java DHPproj"
	- this will open the gui interface, with 
	- Min Support Threshold must be inclusively between 0 and 100
		can be a decimal.
	- Size of hash table created, can be a positive integer above 0
	- Input file - press import button and select and transactional
		database text file
	- Name the output file, running will automatically place or overwrite
		a text file in the folder you executed the program from with
		the given name.
3) output will be a txt file including the number of frequent patterns,
	the execution time in milliseconds, and the patterns and supports.

