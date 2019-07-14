package defs;

import error.ParseError;
import expr.ErrorExpr;

/**
 * ErrorDef.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Def returned from SmtParser in the event of a parsing error.  Includes
 * information about the particular error encountered, which the caller can
 * then extract and handle in a way they see fit.
 */
public class ErrorDef extends Def
{
  /**
   * The particular error encountered during the parsing process.
   */
  private ParseError err;
  
  public ErrorDef(ParseError err)
  {
    this.err = err;
  }
  
  public ErrorDef(ErrorExpr errExpr)
  {
    this.err = errExpr.getErr();
  }
  
  public ParseError getErr()
  {
    return err;
  }
  
  public boolean isError()
  {
    return true;
  }
}