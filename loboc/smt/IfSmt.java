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
 * Class holding information about a single if statement.
 */
public class IfSmt extends Smt
{
  /**
   * Expression representing the condition for the if statement.
   */
  private Expr cond;
  
  /**
   * Statement executed in the event that the statement is true.
   */
  private Smt truS;
  
  /**
   * Statement executed in the event that the statement is false (if this if
   * statement has an else clause - this will be null otherwise).
   */
  private Smt flsS;
  
  public IfSmt(Expr cond, Smt truS, Token startToken)
  {
    super(startToken);
    this.cond = cond;
    this.truS = truS;
    this.flsS = null;
  }
  
  public IfSmt(Expr cond, Smt truS, Smt flsS, Token startToken)
  {
    super(startToken);
    this.cond = cond;
    this.truS = truS;
    this.flsS = flsS;
  }
  
  public String getBOFPIF(int offset)
  {
    String padding = GeneralUtils.getPadding(offset);
    String result = padding + "if (" + cond.toString(true) + ")\n";
    
    // We print the statement with extra padding if and only if the contained
    // statement is not a block statement.
    
    result += truS.getBOFPIF(offset + (truS.isBlock() ? 0 : 1));
    
    if (flsS != null)
    {
      result += '\n' + padding + "else\n";
      result += flsS.getBOFPIF(offset + (flsS.isBlock() ? 0 : 1));
    }
    
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
    
    truS.labelExprs(errors);
    
    if (flsS != null)
    {
      flsS.labelExprs(errors);
    }
  }
  
  public Smt fold()
  {
    cond = cond.fold();
    truS = truS.fold();
    
    if (flsS != null)
    {
      flsS = flsS.fold();
    }
    
    if (cond.isConstant())
    {
      // If we've folded the condition expression to a numerical constant,
      // check to see if it's zero (false) or nonzero (true) and return the
      // clause of the if statement corresponding to this result.

      Num n = (Num) cond;
      
      int b = n.getBits();
      
      startToken = null;
      cond = null;
      
      if (b != 0)
      {
        flsS = null;
        return truS;
      }
      else
      {
        // Return flsS - even if it's null.  The BlockSmt calling this method
        // will exclude null folded statements.
        
        truS = null;
        return flsS;
      }
    }
    
    return this;
  }
  
  public void getOffsetsAndLabel(int prevOffset, int prevSize, RefInt curId)
  {
    truS.getOffsetsAndLabel(prevOffset, prevSize, curId);
    
    if (flsS != null)
    {
      flsS.getOffsetsAndLabel(prevOffset, prevSize, curId);
    }
  }
  
  public void setLastSmts()
  {
    // We'll call setLastSmt on both of our clauses, with an additional
    // caveat - we'll call setLastSmt on ourselves if we do not have a false
    // clause, which we'll provide special handling when creating our three
    // address code.
    
    truS.setLastSmts();
    
    if (flsS != null)
    {
      flsS.setLastSmts();
    }
    else
    {
      lastSmt = true;
    }
  }
  
  public void threeAddress(TempFactory tFac, LabelFactory lFac,
    LinkedList<ThreeAddress> addresses)
  {
	Var condTemp = cond.getValue(tFac, lFac, addresses);
    
    // Generate our labels.
    
    String fL = lFac.gen();
    
    String endL = "";
    
    if (flsS != null)
    {
      endL = lFac.gen();
    }
    
    // And we create our jumps.  If the conditional is false, we jump to flsL
    // (which, in the case that we do not have an "else", is the end of the
    // statement).
    
    addresses.add(new CondGoto(condTemp, fL, BranchType.EQZ));
    
    // True case.  Just call threeAddress on the child and, if necessary, jump
    // to endL when finished.
    
    truS.threeAddress(tFac, lFac, addresses);
    
    if (flsS != null)
    {
      addresses.add(new Goto(endL));
    }
    
    // False case.
    
    addresses.add(new Label(fL));
    
    if (flsS != null)
    {
      flsS.threeAddress(tFac, lFac, addresses);
      addresses.add(new Label(endL));
    }
    else if (lastSmt)
    {
      // Return from the function with value 0.
      
      Var v = tFac.gen(Consts.INT_SIZE, Consts.INT_ALIGN);
      addresses.add(new Assign(v, Imm.IMM_ZERO));
      addresses.add(new Return(v));
    }
  }
}