# THE SCRAP MACHINE
The scrap machine is a project to create a basic processor from scratch in scrapmechanic. It runs a customs et of 16 bit instructions and ships with a compiler to compile from a made up language called smscript to this assembly instruction set.

## User Guide
### Creating machine code
1. Insert your in and output filepaths into com/idt/ScrapMachineCompilerApplication.java
2. Ensure you have all dependencies
3. Write your smscript file to the input folder
4. Run the main method

### Running machine code on the scrap machine
1. Place the scrap machine in your scrapmechanic world using the lift
2. Save any creation on the lift (the in game content will be overwritten later)
3. Find the blueprint in your appdata folder
4. Replace the blueprint.json file with the one in the output folder of your product. (Additionally the compiler also creates a human readable translation of the assembly and a binary used for the sumulator)
5. Load the creation from 2 again in scrapmechanic. It should now be a ROM module
6. Connect each of the lines to the corresponding port on the chip. (Yellow: Chip to ROM, Blue: ROM to chip)
7. Ensure the chip is reset by pressing the reset button
8. optional: connect to the GPIO
9. press the run button

## SMscript language guide
### General
- Each statement must be on its own line
- All variables are either 2's complement integer or boolean. No type declaration is needed
- Control scructures are closed with the keyword "end"
- Comments are made using // at the start of the line. Comments most take up the entire line

### Variables
Variables are assigned using "var VARIABLE_NAME"
Variables are written using "VARIABLE_NAME = EXPRESSION" where "EXPRESSION" is a standard SMscript exporession

### Expressions
SMscript supports expression using the following operations:
- Addition "+"
- Subtraction "-"
- Multiplication "*"
- Integer Division "/"
- Boolean AND "&&"
- Boolean OR "||"
- Boolean NOT "!"
- Greater Than ">"
- Greater Than or Equal ">="
- Less Than "<"
- Less Than or Equal "<="
- Equal "=="

Normal braces "(" ")" may be used to overwrite default precedence
Immediate values can be used anywhere in expression. Supported are positive numbers and booleans "true" and "false".
Note that it is not possible to express negative immediate values. For example "-1" has to be written as "0-1"

### Control Structures
#### IF
If statements can be written using "if EXPRESSION" where "EXPRESSION" is a standard SMscript expression
If blocks are closed using "end"

#### WHILE LOOP
While loops can be written using "while EXPRESSION" where "EXPRESSION" is a standard SMscript expression
While loops are closed using "end"

#### GPIO
GPIO can be set high, low or read to a variable. 
- "gpioSetHigh PIN_NUMBER" where "PIN_NUMBER" is an integer (NOT VARIABLE OR EXPRESSION)
- "gpioSetLow PIN_NUMBER" where "PIN_NUMBER" is an integer (NOT VARIABLE OR EXPRESSION)
- "gpioRead PIN_NUMBER VARIABLE_NAME" where "PIN_NUMBER" is an integer (NOT VARIABLE OR EXPRESSION) and VARIABLE isthe name of a previously declared variable


## Assembly Guide
### Processor Spec
- 4 16 bit Registers
- 128 byte ram (Expandable up to 2kb)
- 4x16 bit GPIO (Expandable to 16x16 bit)

### Registers:
- r0: 00
- r1: 01
- r2: 10
- r3: 11

### Logic
- AND: 00
- OR: 01
- XOR: 10
- NOT: 11

### Flags
- X: unused
- F: Read Flag
- D: Down Flag
- S: RegisterFlag

- White: Zero flag
- Pink: Greater than flag



```
0 MOV:
00 00 RG -- -- IM -- XX

1 MOV TOP
00 01 RG -- -- IM -- XX

2 ADD:
00 10 RG RG RG XX XX XX

3 SUB:
00 11 RG RG RG XX XX XX



4 CMP (Compare B against A):
01 00 RG RG XX XX XX XX

5 LOAD (XXXX YY):
01 01 RG -A DD R- XX XX

6 STORE (XXXX YY):
01 10 RG -A DD R- XX XX

7 LOGIC:
01 11 RG RG RG OP XX XX



8 TERMINATE:
10 00 XX XX XX XX XX XX

9 BLE (No greater than flag):
10 01 -- -- -P C- -- --

10 GPIOSET (XX YYYY):
10 10 -A DD R- XX XX XX

11 GPIORESET/READ (XX YYYY):
10 11 -A DD R- RG XF XX



12 Shift Up/Down
11 00 RG DX -- -- -- --

13 Empty:
11 01 XX XX XX XX XX XX

14 BEQ (Zero flag):
11 10 -- -- -P C- -- --

15 EMPTY:
11 11 XX XX XX XX XX XX
```



