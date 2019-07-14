package error;

/**
 * CondTypeError.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Class representing an error in which the condition for an if statement or a
 * while statement is not of boolean type.
 */
public class CondTypeError extends SemanticError
{
  public CondTypeError()
  {
    super();
  }
  
  public String getErrorMessage()
  {
    String result = "";
    result += "\tExpected type for statement condition: bool;\n";
    result += "\tActual type: " + errExpr.getType();
    return result;
  }
}