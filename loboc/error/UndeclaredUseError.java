package error;

/**
 * UndeclaredUseError.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Class representing an error in which a variable was used in the program
 * without being declared.
 */
public class UndeclaredUseError extends SemanticError
{
  private String id;
  public UndeclaredUseError(String id)
  {
    super();
    this.id = id;
  }
  
  public String getErrorMessage()
  {
    return "\tCannot determine the type of undeclared variable " + id + ";";
  }
}