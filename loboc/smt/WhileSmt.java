package smt;

import java.util.LinkedList;

import threeaddress.Assign;
import threeaddress.BranchType;
import threeaddress.CondGoto;
import threeaddress.Goto;
import threeaddress.Imm;
import threeaddress.Label;
import threeaddress.Return;
import threeaddress.ThreeAddress;
import threeaddress.Var;
import lexing.Token;
import error.SemanticError;
import expr.Expr;
import expr.Num;
import general.Consts;
import general.GeneralUtils;
import general.LabelFactory;
import general.RefInt;
import general.TempFactory;

/**
 * IfSmt.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Class holding information about a single while statement.
 */
public class WhileSmt extends Smt
{
  /**
   * Expression representing the condition of the while loop.
   */
  private Expr cond;
  
  /**
   * The while loop body.
   */
  private Smt s;
  
  public WhileSmt(Expr cond, Smt s, Token startToken)
  {
    super(startToken);
    this.cond = cond;
    this.s = s;
  }
  
  public String getBOFPIF(int offset)
  {
    String padding = GeneralUtils.getPadding(offset);
    
    String result = padding + "while (" + cond.toString(true) + ")\n";
    
    // We print the statement with extra padding if and only if the contained
    // statement is not a block statement.
    
    result += s.getBOFPIF(offset + (s.isBlock() ? 0 : 1));
    
    return result;
  }
  
  public void labelExprs(LinkedList<SemanticError> errors)
  {
    // Type-label our condition expression and add any errors encountered to
    // our list.
    
    LinkedList<SemanticError> tempErrors = new LinkedList<SemanticError>();
    
    cond.labelType(tempErrors);
    
    for (SemanticError err : tempErrors)
    {
      err.setStartToken(startToken);
      err.setErrExpr(cond);
    }
    
    while (!tempErrors.isEmpty())
    {
      errors.addLast(tempErrors.removeFirst());
    }
    
    s.labelExprs(errors);
  }
  
  public Smt fold()
  {
    cond = cond.fold();
    s = s.fold();
    
    if (cond.isConstant())
    {
      // If we've folded the condition expression to a numerical constant,
      // check to see if it's zero (false).  If it's zero, we will never
      // invoke this while loop - return null to signify that this statement
      // can be omitted from our final tree.
      
      Num n = (Num) cond;
      
      int b = n.getBits();
      
      if (b == 0)
      {
        startToken = null;
        cond = null;
        s = null;
        
        return null;
      }
    }
    
    return this;
  }
  
  public void getOffsetsAndLabel(int prevOffset, int prevSize, RefInt curId)
  {
    s.getOffsetsAndLabel(prevOffset, prevSize, curId);
  }
  
  public void setLastSmts()
  {
    // If this was called, this acts as a base case.  We simply set our flag to
    // true.
    
    lastSmt = true;
  }
  
  public void threeAddress(TempFactory tFac, LabelFactory lFac,
    LinkedList<ThreeAddress> addresses)
  {
	// First, we put a label so that we may jump to the top of the loop, prior
    // to performing any computation relevant to checking the condition.

    String loopL = lFac.gen();
    addresses.add(new Label(loopL));
    
    // Next, we check our condition.
    
    Var condTemp = cond.getValue(tFac, lFac, addresses);
    
    // And if the condition is not true, we exit the loop.
    
    String endL = lFac.gen();
    addresses.add(new CondGoto(condTemp, endL, BranchType.EQZ));
    
    // Next, we have the body of our loop, followed by a jump back to the top,
    // and the label denoting the loop exit.
    
    s.threeAddress(tFac, lFac, addresses);
    
    addresses.add(new Goto(loopL));
    addresses.add(new Label(endL));
    
    // If this was the last statement, return with value 0.
    
    if (lastSmt)
    {
      Var v = tFac.gen(Consts.INT_SIZE, Consts.INT_ALIGN);
      addresses.add(new Assign(v, Imm.IMM_ZERO));
      addresses.add(new Return(v));
    }
  }
}