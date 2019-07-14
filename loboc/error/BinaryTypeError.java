package error;

import type.Type;
import expr.Expr;
import general.GeneralUtils;

/**
 * IllegalBinaryTypeError.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Class representing a typing error for a binary operator.
 */
public class BinaryTypeError extends SemanticError
{
  private String op;
  private Expr operandL;
  private Expr operandR;
  private String [] expectedL;
  private String [] expectedR;
  
  public BinaryTypeError(String op, Expr operandL, Expr operandR,
    String [] expectedL, String [] expectedR)
  {
    super();
    this.op = op;
    this.operandL = operandL;
    this.operandR = operandR;
    this.expectedL = expectedL;
    this.expectedR = expectedR;
  }
  
  public String getErrorMessage()
  {
    Type typeL = operandL.getType();
    Type typeR = operandR.getType();
    
    String operandLS = operandL.toString(false);
    String operandRS = operandR.toString(false);
    
    String result = "\tFor binary op " + op + " with operands " + operandLS
      + ", " + operandRS + ";";
    
    // Firstly, which operand, specifically, did our expression tell us was
    // giving us an error (or was the expression just absolute nonsense)?
    
    if (expectedL.length > 0 && expectedR.length > 0)
    {
      // Absolute nonsense it is.  Just print out all the possible type
      // combinations for this operator - we aren't going to be able to get
      // very specific with this error message.
      
      result += "\n\tExpected types for both operands: ";
      
      for (int i = 0; i < expectedL.length - 1; i++)
      {
        result += "(" + expectedL[i] + ", " + expectedR[i] + ")";
        result += ", ";
      }
      
      result += "(" + expectedL[expectedL.length - 1] + ", "
        + expectedR[expectedL.length - 1] + ")";
      
      result += ";\n\tActual operand types: (" + typeL + ", " + typeR + ");";
    }
    else if (expectedR.length > 0)
    {
      // Just the right operand has a faulty type.
      
      result += "\n\tIn the right operand - expected types: ";
      
      result += GeneralUtils.getCommaSeparatedList(expectedR);
      
      result += ";\n\tActual type: " + typeR + ";";
    }
    else
    {
      // Just the left operand has a faulty type.
      
      result += ";\n\tIn the left operand - expected types: ";
      
      result += GeneralUtils.getCommaSeparatedList(expectedL);
      
      result += ";\n\tActual type: " + typeL + ";";
    }
    
    return result;
  }
}