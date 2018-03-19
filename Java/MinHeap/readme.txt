"ls" into directory p2
Run the file run.sh with any input file. Example:
"./run.sh input.txt" on Linux shell
If this fails, edit the run.sh file and add a space between "javac" and "*.java"

I can't figure out why the compiler asks for additional flags when run.sh hasn't been edited,
but will accept the ./run.sh command if there is an edit to the original code after chmod has updated permissions


Assumptions:
Output file ordering of letters is arbitrary (i.e. line for letter "b" can come before "a")
Huffman code binary is constructed with min-heap heap properties