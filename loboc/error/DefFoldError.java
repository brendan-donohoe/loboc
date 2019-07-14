package error;

import expr.Expr;
import lexing.Token;

/**
 * DefFoldError.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 * 
 * Utility class representing an error in which an expression inside an array
 * declaration was not able to be folded to a constant during the constant
 * folding process.
 */
public class DefFoldError extends ParseError
{
  private Expr errExpr;
  
  public DefFoldError(Token errToken, Expr errExpr)
  {
    super(errToken);
    this.errExpr = errExpr;
  }
  
  public String getErrorMessage()
  {
    String result = "";

    result += "\tUnable to fold expression in array type declaration to "
      + "constant;\n";
    result += "\tBest result: " + errExpr.toString(false) + ";";
    return result;
  }
}