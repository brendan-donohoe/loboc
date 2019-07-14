package error;

import expr.Expr;

/**
 * AssignableError.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Class representing an error in which an expression requiring an assignable
 * operand is given a non-assignable expression.
 */
public class AssignableError extends SemanticError
{
  private Expr operand;
  private String op;
  
  public AssignableError(Expr operand, String op)
  {
    this.operand = operand;
    this.op = op;
  }
  
  public String getErrorMessage()
  {
    if (op.length() == 1) // "=" operator.
    {
      return "\tNon-assignable expression " + operand.toString(false)
        + " appearing in the left operand of \"" + op + "\";";
    }
    else // "++" or "--" operators.
    {
      return "\tNon-assignable expression " + operand.toString(false)
        + " appearing in the operand of \"" + op + "\";";
    }
  }
}