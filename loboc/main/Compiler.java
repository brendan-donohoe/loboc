package main;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;

import optimization.Optimizer;
import parsing.SmtParser;
import smt.Smt;
import threeaddress.ThreeAddress;
import error.Error;
import error.ParseError;
import error.SemanticError;
import general.LabelFactory;
import general.RefInt;
import general.TempFactory;
import lexing.CountReader;
import lexing.Tokenizer;
import mips.Instruction;

/**
 * Compiler.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Class which, given the input source and a boolean denoting whether or not
 * the optimization flag was set, performs the task of compiling the code and
 * generating the MIPS assembly code instructions.
 */
public class Compiler
{
  private Reader in;
  private boolean optFlag;
  
  public Compiler(Reader in, boolean optFlag)
  {
    this.in = in;
    this.optFlag = optFlag;
  }
  
  /**
   * Attempt to compile the provided input and generate MIPS assembly
   * instructions, which are accumulated in the provided ins list.  Also take
   * in an errors list to collect any errors encountered during the compilation
   * process.
   */
  public void run(LinkedList<Instruction> ins, LinkedList<Error> errors) throws IOException
  {
    /*
     * SPIKE 1: LEXING.
     */
    
    // First, we set up the tokenizer.
    
    Tokenizer to = new Tokenizer(new CountReader(in));
      
    /*
     * SPIKE 2 AND SPIKE 3: PARSING.
     */
    
    // Then, we parse several statements while adding any parsing errors
    // encountered along the way to a list.
    
    LinkedList<ParseError> pErrors = new LinkedList<ParseError>();
    Smt blk = SmtParser.topBlock(to, pErrors);
    
    while (!pErrors.isEmpty())
    {
      errors.add(pErrors.remove());
    }
    
    /*
     * SPIKE 3 AND SPIKE 4: SEMANTIC ANALYSIS.
     */
    
    // Next, we label our AST with types and look for any typing errors that
    // do not match our list of type rules.
    
    LinkedList<SemanticError> tErrors = new LinkedList<SemanticError>();
    blk.labelExprs(tErrors);
    
    while (!tErrors.isEmpty())
    {
      errors.add(tErrors.remove());
    }
    
    // This is the end of the line if we have any sort of error.  No use
    // generating code from something that could be any manner of broken.
    
    if (!errors.isEmpty())
    {
      return;
    }
    
    // Perform statement folding.

    blk.fold();
    
    // Label the global variable offsets.
    
    blk.getOffsetsAndLabel(0, 0, new RefInt(0));
    
    // Mark possible return statements in the tree.
    
    blk.setLastSmts();
    
    /*
     * SPIKE 5 / 6: THREE ADDRESS CODE GENERATION.
     */
    
    // Begin generation of three address code.
    
    TempFactory tf = new TempFactory();
    LabelFactory lf = new LabelFactory();
    LinkedList<ThreeAddress> addresses = new LinkedList<ThreeAddress>();
    
    blk.threeAddress(tf, lf, addresses);
    
    // If optimization was enabled, perform optimization on the three address
    // code.
    
    if (optFlag)
    {
      /*
       * SPIKE 6: THREE ADDRESS CODE OPTIMIZATION.
       */
      
      Optimizer o = new Optimizer(addresses, tf.getTempNum());
      o.optimize();
      addresses = o.getAddresses();
    }
    
    /*
     * SPIKE 5: CODE GENERATION.
     */
    
    // Finally, get our three address code instructions and add them to our
    // list before returning.
    
    for (ThreeAddress a : addresses)
    {
      a.getInst(ins);
    }
  }
}