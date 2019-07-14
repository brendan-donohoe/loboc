package error;

import general.GeneralUtils;
import lexing.Token;
import lexing.Token.TType;

/**
 * UnexpectedError.java
 *
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Utility class representing an error in which an unexpected token was parsed.
 * Acceptable alternatives to the faulty token can be passed in to print to the
 * user.
 */
public class UnexpectedError extends ParseError
{
  /**
   * Suggestions of possible legal alternatives to be given in the error
   * message.
   */
  private String [] expected;
  
  public UnexpectedError(Token errToken, String ... expected)
  {
    super(errToken);
    this.expected = expected;
  }
  
  public String getErrorMessage()
  {
    String result = "";
    
    result += "\tExpected: ";
    
    result += GeneralUtils.getCommaSeparatedList(expected);
    
    result += ";\n\tActual: ";
    
    // Based on the actual token we received versus the one we expected, we
    // either print the token if it's a legal term, or we print something else
    // if it's not even a legal token to begin with.
    
    TType tType = errToken.getType();
    
    switch (tType)
    {
      case ILLCHR :
        result += "Illegal character " + errToken.toString();
        break;
      case ALN_LEN_ERR :
        result += "Illegal identifier (identifiers must not exceed 1024 bytes"
                + " in length!)";
        break;
      case NUM_LEN_ERR :
        result += "Illegal numeric literal (numeric literals must not exceed"
                + " the maximum unsigned value 4294967295!)";
        break;
      default :
        result += errToken.toString();
        break;
    }
    
    result += ";";
    
    return result;
  }
}
