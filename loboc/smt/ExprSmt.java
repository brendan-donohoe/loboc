package smt;

import java.util.LinkedList;

import threeaddress.Return;
import threeaddress.ThreeAddress;
import threeaddress.Var;
import lexing.Token;
import error.SemanticError;
import expr.Expr;
import general.GeneralUtils;
import general.LabelFactory;
import general.RefInt;
import general.TempFactory;

/**
 * ExprSmt.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Class holding information about a single expression statement (that is, a
 * statement of the form EXPR ";").  This just holds the expression used in the
 * statement.
 */
public class ExprSmt extends Smt
{
  /**
   * The expression used in the statement.
   */
  private Expr e;
  
  public ExprSmt(Expr e, Token startToken)
  {
    super(startToken);
    this.e = e;
  }
  
  public String getBOFPIF(int offset)
  {
    String padding = GeneralUtils.getPadding(offset);
    
    return padding + this.toString();
  }
  
  public String toString()
  {
    return e.toString(true) + ";";
  }
  
  public void labelExprs(LinkedList<SemanticError> errors)
  {
    // Label the expression inside the statement.  First, make a second list to
    // hold all errors encountered.
    
    LinkedList<SemanticError> tempErrors = new LinkedList<SemanticError>();  
    
    e.labelType(tempErrors);
    
    // Now, for each error encountered, label it with this statement's starting
    // token and the entire expression parsed.
    
    for (SemanticError err : tempErrors)
    {
      err.setStartToken(startToken);
      err.setErrExpr(e);
    }
    
    // Then add each of our errors to our list.
    
    while (!tempErrors.isEmpty())
    {
      errors.addLast(tempErrors.removeFirst());
    }
  }

  public Smt fold()
  {
    e = e.fold();
    
    return this;
  }
  
  public void getOffsetsAndLabel(int prevOffset, int prevSize, RefInt curId)
  {
    // Do nothing - we contain no statements.
  }
  
  public void setLastSmts()
  {
    // Simply set this to true.
    
    lastSmt = true;
  }
  
  public void threeAddress(TempFactory tFac, LabelFactory lFac,
    LinkedList<ThreeAddress> addresses)
  {
    // Just get the value of the statement we contain.
    
    Var v = e.getValue(tFac, lFac, addresses);
    
    if (lastSmt)
    {
      addresses.add(new Return(v));
    }
  }
}