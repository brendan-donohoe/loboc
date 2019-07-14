package error;

import expr.Expr;

/**
 * AddressableError.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Class representing an error in which the ampersand operator was applied to
 * a non-addressable expression.
 */
public class AddressableError extends SemanticError
{
  private Expr operand;
  
  public AddressableError(Expr operand)
  {
    this.operand = operand;
  }
  
  public String getErrorMessage()
  {
    String result = "\tThe operand " + operand.toString(false)
      + " does not yield an addressable value for this use of the & operator;";
    return result;
  }
}