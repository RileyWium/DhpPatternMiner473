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

Notes:
This program falls short of what I wanted to deliver.
- It runs slower than my Apriori program in most cases.
- It doesn't include any means of analysis, 
	besides manually comparing the output times
- It doesn't switch over to apriori when the hash table would be unnecessary
- It doesn't allow for complex changing of the hash function.
The reason for this was a problem with scope, in that I underestimated
making DHP. I was able to make a functional version of DHP rather early but it
was too slow, I tried remaking each component to be faster, rewriting 
the entire program, and even looking at psuedo code online. I have ideas of
how to fix it but don't have the time to complete it now with finals. 
If given just a bit more time I'm sure I could fix it.
I only expect marks for what I delivered, but I do want to give context for 
underdelivering on my pitch.
If you have any questions on my project feel free to contact me at: 
wium@unbc.ca
