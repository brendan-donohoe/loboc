package smt;

import java.util.LinkedList;

import threeaddress.ThreeAddress;
import lexing.Token;
import error.SemanticError;
import general.LabelFactory;
import general.RefInt;
import general.TempFactory;

/**
 * Smt.java
 * 
 * @author Brendan Donohoe
 *
 * @version 1.0
 * 
 * Class which all statements must inherit from.
 */
public abstract class Smt
{
  protected Token startToken;
  protected boolean lastSmt;
  
  public Smt()
  {
    // Default no-args constructor.  To be used by ErrorSmt only.
  }
  
  public Smt(Token startToken)
  {
    this.startToken = startToken;
  }
  
  public Token getStartToken()
  {
    return startToken;
  }
  
  /**
   * Determine if this statement is a block statement.
   * @return True if this is a block statement - false otherwise.
   */
  public boolean isBlock()
  {
    return false;
  }
  
  /**
   * Determine whether or not this statement represents a parsing error.
   * @return True if this statement represents an error, and false otherwise.
   */
  public boolean isError()
  {
    return false;
  }
  
  /**
   * Generate the BOFPIF form for this statement, including padding.  The
   * number of spaces with which to pad is 2 * offset.
   * @param offset - The amount of padding (sequences of two spaces) to be
   * appended to the beginning of each line of the VSR.
   * @return The indented BOFPIF form for this statement.
   */
  public abstract String getBOFPIF(int offset);
  
  /**
   * Label all expressions appearing in a statement (such as the conditions of
   * an if or while statement, or the expression in an expression statement)
   * with their types.  Also, generate errors for every expression which cannot
   * be typed, and accumulate them inside of a supplied list.
   * @param errors - The accumulated list of errors.
   */
  public abstract void labelExprs(LinkedList<SemanticError> errors);
  
  /**
   * Fold over all expressions appearing in a statement.  The caller must have
   * labeled the expressions previously. 
   * @return The statement replacing this statement after folding - or null if
   * this statement is to be erased from the tree entirely.
   */
  public abstract Smt fold();
  
  /**
   * Compute the offsets of all variables declared within this statement
   * and update the relevant symbol table with this information.  While we're
   * at it, we'll also label each of our variables with a unique integer.
   * @param prevOffset - The offset of the previous variable to be added at
   * this scope.
   * @param prevSize - The size of the previous variable to be added at this
   * scope.
   * @param curId - The next id with which a variable will be named, wrapped
   * in a RefInt object so that changes to curId carry across method calls.
   */
  public abstract void getOffsetsAndLabel(int prevOffset, int prevSize,
    RefInt curId);
  
  /**
   * Determine all possible last statements of the given statement, and mark
   * them (via the "isLastSmt" flag) as they are found.  This will tell us
   * where to insert our return three-address code statements.
   */
  public abstract void setLastSmts();
  
  /**
   * Generate the three address code for this statement.
   * @param tFac - Factory object to generate fresh temporaries as needed.
   * @param lFac - Factory object to generate fresh (you guessed it!) labels,
   * as needed.
   * @param addresses - The accumulated list of three address code
   * instructions.
   */
  public abstract void threeAddress(TempFactory tFac, LabelFactory lFac,
    LinkedList<ThreeAddress> addresses);
}
