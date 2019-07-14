package error;

import lexing.Token;

/**
 * ParseError.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Utility class to hold information about a particular type of error that has
 * occurred.  Implementing classes can define additional parameters and
 * implement their own error messages to be printed to standard error.
 */
public abstract class ParseError implements Error
{
  protected Token errToken;
  
  public ParseError(Token errToken)
  {
    this.errToken = errToken;
  }
  
  public String toString()
  {
    String result = "";

    result += "Error at " + errToken.getLineNum() + ":"
            + errToken.getByteNum() + ":\n";
    
    result += getErrorMessage();
    
    return result;
  }
  
  /**
   * Return the error message in a format which can be printed to standard
   * error.
   * @return The error message.
   */
  protected abstract String getErrorMessage();
}