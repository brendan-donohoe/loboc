package smt;

import java.util.LinkedList;

import threeaddress.ThreeAddress;
import error.ParseError;
import error.SemanticError;
import expr.ErrorExpr;
import general.LabelFactory;
import general.RefInt;
import general.TempFactory;

/**
 * ErrorSmt.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Smt returned from SmtParser in the event of a parsing error.  Includes
 * information about the particular error encountered, which the caller can
 * then extract and handle in a way they see fit.
 */
public class ErrorSmt extends Smt
{
  /**
   * The particular error encountered during the parsing process.
   */
  private ParseError err;
  
  public ErrorSmt(ParseError err)
  {
    this.err = err;
  }
  
  public ErrorSmt(ErrorExpr errExpr)
  {
    this.err = errExpr.getErr();
  }
  
  public ParseError getErr()
  {
    return err;
  }
  
  public boolean isError()
  {
    return true;
  }
  
  // Method stubs inherited from the Smt class.
  
  public String getBOFPIF(int offset)
  {
    return null;
  }
  
  public void labelExprs(LinkedList<SemanticError> errors)
  {
    
  }
  
  public Smt fold()
  {
    return null;
  }
  
  public void getOffsetsAndLabel(int prevOffset, int prevSize, RefInt curId)
  {
    
  }
  
  public void setLastSmts()
  {
    
  }
  
  public void threeAddress(TempFactory tFac, LabelFactory lFac,
    LinkedList<ThreeAddress> addresses)
  {
    
  }
}