CS 554: COMPILER CONSTRUCTION
AUTHOR: BRENDAN DONOHOE
SEVERAL TESTS PROVIDED BY: JOHN ERICKSEN, JOHN MCIVER
SPIKE 6
SUBMISSION DATE: 11 MAY 2018



RUNNING THE EXTENSION:

The extension may be run by providing the compiler with an optional "-o" flag.
All tests are included in the test directory.

java -jar loboc.jar -o <filename>



DESCRIPTION:

The Spike 6 program uses the functionality from the previous spikes as a
foundation to carry out the final stage of the compiling process - generation of
MIPS assembly from the AST.

As with previous spikes, Spike 6 may be provided a LOBO-C input program via
either standard input or a filename.  Spike 6 will then generate MIPS code that,
when run (via the TLC testing framework), will perform the desired computation
and store the result (where "result" is the final statement value of the
program, as defined in the spike5b specification) into the register $v0 before
exiting.  Spike 6 may also be provided with an optional "-o" to perform
optimization on the intermediate representation before generating the three
address code.



FULL COMPILATION PROCESS:

            LEXING (spike1)
                 |
                 V
       PARSING (spike2 / spike3)
                 |
                 V
  SEMANTIC ANALYSIS (spike3 / spike4)
                 |
                 V
THREE ADDRESS CODE GENERATION (spike5 / spike6)
                 |
                 V
  THREE ADDRESS CODE OPTIMIZATION (spike6)
                 |
                 V
    MACHINE CODE GENERATION (spike5)

Spike 6 first performs lexical analysis, tokenizing the input from the input
source in a manner consistent with the Spike 1 specification.

Spike 6 then uses these tokens to build an AST representation of the
expressions and statements of the program, also constructing a symbol table for
all identifiers used in the program.  Spike 5 will print any errors encountered
during the parsing process once it completes, performing error recovery where
necessary (as defined in the spike3 specification).

Spike 6 then uses the symbol table to perform semantic analysis - labelling
the AST with types and checking expressions for semantic correctness, printing
any typing errors (defined to be those that do not match the propagation rules
in the spike4 specification) encountered to standard error.

If and only if no errors were encountered during the previous compilation
phases, Spike 6 walks the abstract syntax tree to generate three address code.
The three address code representation is of the following form:

V = R; (variable assignment)
V = R + R'; (binary operation)
if R goto L; (jump if true)
if !R goto L; (jump if false)
V[] = R; (dereference and assignment)
goto L; (unconditional jump)
label L: (jump label)
return R; (exit with value)
V = uop R; (unary operation)

V <- (a variable, either compiler-generated or user-defined)
R, R' <- (a "result", either a numeric literal, the unary & addressing
operator followed by a variable, or a variable followed by the unary []
dereferencing operator)
L <- (a string literal representing a label to be jumped to in the code)
bop <- "+" | "-" | "*" | "* (u)" | "/" | "/ (u)" | "<" | "<=" | ">" | ">=" |
  "< (u)" | "<= (u)" | "> (u)" | ">= (u)" | "==" | "!="
uop <- "-" | "(byte)"

In addition, each variable is assigned a size during the generation process
and, for instructions involving dereferences, the size of the dereferenced
value is saved as well, so that during the code generation process, we know
which of "sb" / "sw" and "lb" / "lw" to use for retrieving and storing a
particular variable in memory.  Note the absence of "||" and "&&", the logic
for these is implicit within the three address code using conditional and
unconditional jumps, so as to perform short-circuiting.  Similarly, there are no
prefix / postfix decrement / increment operations - the logic for these is also
implicit within the three address code.  There is also no array indexing
operation - all array logic is converted to equivalent pointer logic during
three address code generation.

Once the three address code is generated, if the optimization flag is provided,
the compiler attempts to optimize the three address code.  See EXTENSION
OVERVIEW for details.

Finally, the machine code is more-or-less a direct translation of the optimized
three address code.



EXTENSION OVERVIEW:

The following local optimizations are implemented and carried out on individual
basic blocks in the program.

Dead Code Elimination (somewhat)

Constant Propagation

Copy Propagation

Constant Folding

Algebraic Identities

Several of these extensions make use of the fact that we differentiate between
the user-defined variables that were contained in the original symbol table, and
the compiler-generated temporaries that were generated during the code
generation process.  In the examples below, compiler-generated variables are
denoted by a "t" followed by a number, whereas user-defined variables are
denoted by a "v" followed by a number.  At least with this implementation,
because pointers will never point to a location on the stack, where the
compiler-generated temporaries are stored, temporaries have the useful property
that they can never be addressed by a pointer.

To track live definitions and variable definitions, several bit vectors (the
BitSet class in Java) are used, one for every instruction and one for every
variable.  From there, attempting to find a live definition of a particular
variable is a matter of ANDing two bit vectors together and scanning for any
activated bits.

Dead Code Elimination:

Any assignments to compiler-generated temporaries (not user-defined variables)
that are never used anywhere else in the program are eliminated.  For instance,
after dead code elimination, this three address code:

t0 = 1;
v0 = 2;
t1 = -v0;
v1 = t1;

Would become:

v0 = 2;
t1 = -v0;
v1 = t1;

Assuming that t0 was never used anywhere else in the program - otherwise this
wouldn't take place.  This is far from perfect - a better approach would be to
record the uses associated with each assignment of a variable, either
user-defined or temporary, and eliminate the assignments that are known not to
be used.  The reason only temporary assignments - rather than user-defined
variable assignments, are eliminated is due to aliasing issues.  If we have:

v0 = 1;
t1 = t0[];
t2 = t1 + 8;

If v0 is never explicitly used elsewhere, we may be tempted to eliminate v0 = 1.
However, if t0 had pointed to v0, then in fact, that assignment was used during
the assignment to t1, and so the first instruction cannot be safely eliminated
without knowledge of what t0 is referencing.

Constant Propagation:

If we have something like the following:

t0 = 1;
t1 = 2;
...
v0 = t0 + t1;

We may be able to replace this with:

t0 = 1;
t1 = 2;
...
v0 = 1 + 2;

This opens up several opportunities for further optimizations to take place,
such as dead code elimination (if t0 and t1 are otherwise unused) and constant
folding, which may then lead to further constant propagation.  This
optimization will only take place if t0 and t1 cannot be reassigned (either
directly or indirectly via a pointer assignment) before the propagation.

Copy Propagation:

If we have something like the following:

t0 = v1;
t1 = t0;

We may be able to replace this with:

t0 = v1;
...
t1 = v1;

Copy propagation in this case will only take place if neither t0 nor v1 can be
reassigned before the propagation takes place.

Constant Folding:

Similar to the constant folding in spike 4.  If we have:

t0 = 2 + 3;

We can replace it with:

t0 = 5;

Algebraic Identities:

This optimization takes advantage of common algebraic identities to replace
statements like the following:

v1 = v0 * 0;

With:

v1 = 0;



SPECIFIC IMPLEMENTATION INFORMATION:

This implementation uses the rightmost-bracket in declaration corresponds to
the leftmost bracket in array expression approach to array indexing.  So the
first assignment of the following code will produce the expected result, while
the second will not.

unsigned [1] [10] a;

a[9][0] = 1;
a[0][9] = 1;

However, actual error handling for out-of-bounds array indices is not supported
in this implementation so, uh, have fun with that.

All compiler-generated temporary variables are stored on the stack rather than
the global data segment.  Unfortunately, because temporaries can extend beyond
the lifetime of a block (depending on how optimization goes), temporaries never
"die" - thus each temporary gets its own location on the stack.  A better
approach would be to conduct liveness analysis to figure out which temporaries
have future use at each instruction of three address code to more advantageously
allocate locations in the stack.

The only acceptable left-values in an assignment expression are identifiers,
array expressions, or pointer dereferences.  As such, statements like the
following are not possible in this implementation, and will generate errors:

(a ? b : c) = 0;
(0, 1, b) = 5;
&(a ? b : c);
&(a, b);

Finally, pointer incrementation and decrementation increment and decrement by
the size of the type of the item pointed to. This causes the is4_pointer3.loboc
tests and the is4_array2x3x4x5.loboc tests in the public repository to fail, due
to the fact that, in those particular cases, incrementing a pointer was always
expected to increment the address by 1, irrespective of the pointer type.



TESTS:

Tests, which include all of those available in the public repository as of 11:00
AM on the submission date, as well as a handful of extra tests for ISEQ 1 - 4
and several tests for ISEQ 5 which are designed to more directly test the above
optimizations, are available in the test directory.  All tests (with the
appropriate modifications to is4_pointer3.loboc and is4_array2x3x4x5.loboc
above) pass with and without optimizations enabled via the -o flag, when tested
using the TLC framework.



EXTRA FEATURES:

The following types of error messages may be printed if the LOBO-C compiler is
given faulty input.

Unexpected Token Error: Thrown if an anomaly is encountered during parsing.  For
instance, encountering the statement:

signed a;

a + + 2;

Would result in something like the following message:

Error at 3:5:
	Expected: Number, Identifier, "(";
	Actual: "+";

Definition Fold Error: Occurs if the inside of an array type declaration cannot
be folded to a constant.  Something like the following:

unsigned [c + (2*5)] arr;

Would yield:

Error at 1:11:
	Unable to fold expression in array type declaration to constant;
	Best result: (c+10);

Duplicate Definition Error: Occurs if there is a duplicate definition of some
variable in the same scope.

unsigned c;
signed a, b, c;

Would print the message:

Error at 2:14:
	Duplicate declaration of identifier c;
	First declaration was at 1:10;

Undeclared Use Error: Occurs if a variable is used without having ever been
declared.

d + 2;

Prints the message:

Error in expression (d+2) in statement at 1:1:
	Cannot determine the type of undeclared variable d;

Assignable/Addressable Error: Occurs if there is a non-assignable object on
the LHS of an assignment or in the operand of the unary &.

&0;

Gives:

Error in expression (&0) in statement at 1:1:
	The operand 0 does not yield an addressable value for this use of the & operator;

Type Error: Occurs if operands of an operator have invalid types (according to the type
propagation rules).  For instance:

signed [] a;
signed b;

a == b;

Would yield:

Error in expression (a==b) in statement at 4:1:
	For binary op == with operands a, b;
	In the right operand - expected types: signed[];
	Actual type: signed;



EXTRA "FEATURES" (BUGS):

The author is not aware of any bugs in the current version of the code.



ACKNOWLEDGEMENTS:

The author would like to acknowledge all those who contributed tests to the
public repository.  Their tests were tremendously useful in testing correctness
of both the original spike5b program and the optimizations for spike6.  The
author would also like to acknowledge John M for being a great study partner in
the weekends before the final exam.
