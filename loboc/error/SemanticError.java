package error;

import expr.Expr;
import lexing.Token;

/**
 * SemanticError.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Class representing a general semantic error.  There are different inheriting
 * classes for operators of different parity.
 */
public abstract class SemanticError implements Error
{
  /**
   * The token denoting the start of the statement at which this error
   * occurred.
   */
  protected Token startToken;
  
  /**
   * The entire expression in which this error occurred.
   */
  protected Expr errExpr;
  
  public void setStartToken(Token startToken)
  {
    this.startToken = startToken;
  }
  
  public void setErrExpr(Expr errExpr)
  {
    this.errExpr = errExpr;
  }
  
  public String toString()
  {
    String result = "";
    
    String errExprS = errExpr.toString(false);
    
    result += "Error in expression " + errExprS + " in statement at "
      + startToken.getLineNum() + ":" + startToken.getByteNum() + ":\n";
    result += getErrorMessage();
    
    return result;
  }
  
  public abstract String getErrorMessage();
}