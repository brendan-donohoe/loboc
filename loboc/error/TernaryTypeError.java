package error;

import type.Type;
import expr.Expr;

/**
 * IllegalTernaryTypeError.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Class representing a typing error for a binary operator.
 */
public class TernaryTypeError extends SemanticError
{
  private Expr operand1;
  private Expr operand2;
  private Expr operand3;
  
  public TernaryTypeError(Expr operand1, Expr operand2, Expr operand3)
  {
    super();
    this.operand1 = operand1;
    this.operand2 = operand2;
    this.operand3 = operand3;
  }
  
  public String getErrorMessage()
  {
    Type errType1 = operand1.getType();
    Type errType2 = operand2.getType();
    Type errType3 = operand3.getType();
    
    String operand1S = operand1.toString(false);
    String operand2S = operand2.toString(false);
    String operand3S = operand3.toString(false);
    
    String result = "";
    
    result += "\tFor ternary operator with condition " + operand1S + " and"
      + " clauses " + operand2S + ", " + operand3S + ";";
    if (!errType1.isBool())
    {
      // The condition is not of boolean type.
      
      result += "\n\tExpected condition type: bool;";
      result += "\n\tActual condition type: " + errType1 + ";";
    }
    else
    {
      // The types of the clauses don't match up.
      
      result += "\n\tExpected type for second clause: " + errType2 + ";";
      result += "\n\tActual type for second clause: " + errType3 + ";";
    }

    return result;
  }
}