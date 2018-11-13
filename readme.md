# Curta 
[Curta](https://en.wikipedia.org/wiki/Curta) is a simple parser for a calculator. The calculator grammar is defined by the Synopsys
requirements in the assignment. Example grammars are:
```
    add(1, 2)	                                    →   3
    add(1, mult(2, 3))	                            →   7
    mult(add(2, 2), div(9, 3))	                    →   12
    let(a, 5, add(a, a))                            →   10
    let(a,5,let(b,mult(a,10),add(b,a)))	            →   55
    let(a,let(b,10,add(b,b)),let(b, 20,add(a,b)))   →   40
```

The grammar this project supports is documented in the published [JavaDocs](https://josh-h.github.io/curta/javadoc/index.html).

## Building
The solution uses Java 10 features but was tested against Java 11. Building is simply:

    ./gradlew jar

# Notes
1. The grammar specified in the assignment allows for somewhat ambiguous expressions, like:

       let(a,1,a)
   
   The grammar supported by this implementation is slightly stricter and requires a function
   in place of the second _a_. The grammar is formally defined in the
   [im.moonshot.curta.parser](https://josh-h.github.io/curta/javadoc/im/moonshot/curta/parser/package-summary.html) package docs.
    
1. The assignment's fifth example was also missing the final end parenthesis, ie the parenthesis in
   bold at the end was missing:

    let(a,let(b,10,add(b,b)),let(b, 20,add(a,b))**)**

# Usage
You may run using gradle as follows:

    ./gradlew run --args="add(1,2)"
    
Or on unix systems a wrapper has been added for convenience:

    ./curta 'add(1,2)'
    
Running with no arguments prints a brief usage. The flags are largely self explanatory, with the exception for `--graph`.
Specifying `--graph` generates the syntax tree for the expression provided. If Graphviz is on the path and execution
is on Mac OS then the diagram is opened in the platform browser. Otherwise a graphviz binary search tree diagram
is generated using the dot language.

Example execution is:
```
    $ ./curta 'let(a,let(b,10,add(b,b)),let(b, 20,add(a,b)))'
    40
    
    $ ./curta --verbose 'let(a,let(b,10,add(b,b)),let(b, 20,add(a,b)))'
    Verbose logging enabled
    Starting parser
    Finished parsing
    Starting interpretation
    Finished interpretation
    40

    $ ./curta --graph 'let(a,let(b,10,add(b,b)),let(b, 20,add(a,b)))'
    digraph BST {
    	LET0 [ label = "LET" ];
    	var1 [ label = "a" ];
    	LET2 [ label = "LET" ];
    	var3 [ label = "b" ];
    	int4 [ label = "10" ];
    	ADD5 [ label = "ADD" ];
    	var6 [ label = "b" ];
    	var7 [ label = "b" ];
    	LET8 [ label = "LET" ];
    	var9 [ label = "b" ];
    	int10 [ label = "20" ];
    	ADD11 [ label = "ADD" ];
    	var12 [ label = "a" ];
    	var13 [ label = "b" ];
    	ADD5 -> { var6 var7 };
    	LET2 -> { var3 int4 ADD5 };
    	ADD11 -> { var12 var13 };
    	LET8 -> { var9 int10 ADD11 };
    	LET0 -> { var1 LET2 LET8 };
    }
```
on MacOS the following graph is generated and opened:
![](https://josh-h.github.io/curta/example.svg)
