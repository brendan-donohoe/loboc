package expr;

import java.util.LinkedList;

import threeaddress.TempVar;
import threeaddress.ThreeAddress;
import error.ParseError;
import error.SemanticError;
import general.LabelFactory;
import general.TempFactory;

/**
 * ErrorExpr.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Expr returned from ExprParser in the event of a parsing error.  Includes
 * information about the particular error encountered, which the caller can
 * then extract and handle in a way it sees fit.
 */
public class ErrorExpr extends Expr
{
  /**
   * The particular error encountered during the parsing process.
   */
  private ParseError err;
  
  public ErrorExpr(ParseError err)
  {
    this.err = err;
  }
  
  public ParseError getErr()
  {
    return err;
  }
  
  public boolean isError()
  {
    return true;
  }
  
  // Method stubs inherited from the Expr class.
  
  public String toString(boolean anno)
  {
    return null;
  }
  
  public void labelType(LinkedList<SemanticError> errors)
  {
    
  }
  
  public Expr fold()
  {
    return null;
  }
  
  public TempVar getValue(TempFactory tFac, LabelFactory lFac,
    LinkedList<ThreeAddress> addresses)
  {
    return null;
  }
}