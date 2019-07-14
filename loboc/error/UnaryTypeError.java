package error;

import type.Type;
import expr.Expr;
import general.GeneralUtils;

/**
 * IllegalUnaryTypeError.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Class representing a typing error for a unary operator.
 */
public class UnaryTypeError extends SemanticError
{
  private String uop;
  private Expr operand;
  private String [] expected;
  
  public UnaryTypeError(String uop, Expr operand, String [] expected)
  {
    super();
    this.uop = uop;
    this.operand = operand;
    this.expected = expected;
  }
  
  public String getErrorMessage()
  {
    Type errType = operand.getType();
    
    String operandS = operand.toString(false);
    
    String result = "";
    
    result += "\tFor unary op " + uop + " with operand " + operandS + ";";
    result += "\n\tExpected operand type: ";
    
    result += GeneralUtils.getCommaSeparatedList(expected);
    
    result += ";\n\tActual operand type: " + errType + ";";
    
    return result;
  }
}