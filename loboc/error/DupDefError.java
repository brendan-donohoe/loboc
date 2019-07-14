package error;

import symtable.SymbolData;
import lexing.Token;

/**
 * DupDefError.java
 *
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Utility class representing an error in which an already declared variable
 * has been declared again.  The token representing the most recent declaration
 * of the variable, as well as the SymbolData representing the first
 * declaration of the variable, can be passed in to be printed to the user.
 */
public class DupDefError extends ParseError
{
  /**
   * The data of the duplicate identifier in the symbol table (includes line
   * and byte number of the first declaration, for instance).
   */
  private SymbolData dup;
  
  public DupDefError(Token errToken, SymbolData dup)
  {
    super(errToken);
    this.dup = dup;
  }
  
  public String getErrorMessage()
  {
    String result = "";
    
    result += "\tDuplicate declaration of identifier " + errToken.getData()
    	    + ";\n";
    result += "\tFirst declaration was at " + dup.getLineNum() + ":"
    		+ dup.getByteNum() + ";";
    return result;
  }
}