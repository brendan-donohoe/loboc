package expr;

import java.util.LinkedList;

import error.TernaryTypeError;
import error.SemanticError;
import general.LabelFactory;
import general.TempFactory;
import symtable.SymbolTable;
import threeaddress.Assign;
import threeaddress.BranchType;
import threeaddress.Goto;
import threeaddress.CondGoto;
import threeaddress.Label;
import threeaddress.ThreeAddress;
import threeaddress.Var;
import type.InvalidType;
import type.Type;

/**
 * CondExpr.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Class representing a ternary expression - that is, it holds one condition,
 * one clause which is evaluated when the expression is true, and one clause
 * which is evaluated when the expression is false.
 */
public class CondExpr extends Expr
{
  private Expr cond;
  private Expr truT;
  private Expr flsT;
  
  public CondExpr(SymbolTable tb, Expr cond, Expr truT, Expr flsT)
  {
    super(tb);
    this.cond = cond;
    this.truT = truT;
    this.flsT = flsT;
  }
  
  public String toString(boolean anno)
  {
    String condStr = cond.toString(anno);
    String truTStr = truT.toString(anno);
    String flsTStr = flsT.toString(anno);
    
    String resultStr = "";
    
    if (anno)
    {
      resultStr += " " + type.getShorthand() + ":";
    }
    
    resultStr += "(" + condStr + "?" + truTStr + ":" + flsTStr + ")";
    return resultStr;
  }
  
  protected Type computeType()
  {
    Type t1 = cond.getType();
    Type t2 = truT.getType();
    Type t3 = flsT.getType();
    
    if (t1.isBool() && t2.equals(t3))
    {
      return t2;
    }
    else
    {
      return InvalidType.INVALID_TYPE;
    }
  }
  
  public void labelType(LinkedList<SemanticError> errors)
  {
    // First, compute the types of all three of our children.
    
    cond.labelType(errors);
    truT.labelType(errors);
    flsT.labelType(errors);
    
    // From the types of our children, compute our own type.
    
    type = computeType();
    
    if (errors != null && type.isInvalid() && !cond.getType().isInvalid()
      && !truT.getType().isInvalid() && !flsT.getType().isInvalid())
    {
      errors.add(new TernaryTypeError(cond, truT, flsT));
    }
  }
  
  public Expr fold()
  {
    cond = cond.fold();
    truT = truT.fold();
    flsT = flsT.fold();
    
    // If we have a numerical constant for our condition (even if we don't have
    // any numerical constants in our clauses), we'll fold to the appropriate
    // clause.
    
    if (cond.isConstant() && type.isPrimitive())
    {
      Num nCond = (Num) cond;
      
      int bCond = nCond.getBits();
      
      // Return our result and set any unused references to null.
      
      type = null;
      tb = null;
      cond = null;
      
      if (bCond != 0)
      {
        flsT = null;
        return truT;
      }
      else
      {
        truT = null;
        return flsT;
      }
    }
    
    return this;
  }
  
  public Var getValue(TempFactory tFac, LabelFactory lFac,
    LinkedList<ThreeAddress> addresses)
  {
    // First, we compute the condition's value and put it inside of a temp.
    
    Var condVal = cond.getValue(tFac, lFac, addresses);
    Var condTemp = tFac.gen(cond.getType().getSize(),
      cond.getType().getAlignment());
    addresses.add(new Assign(condTemp, condVal));
    
    // We then generate our labels.
    
    String tL = lFac.gen();
    String endL = lFac.gen();
    
    // Now, we create the jumps and generate the code corresponding to each
    // clause, storing the appropriate postfix instructions in the appropriate
    // list.
    
    addresses.add(new CondGoto(condTemp, tL, BranchType.NEZ));
    
    // False case.
    
    Var fv = flsT.getValue(tFac, lFac, addresses);
    Var result = tFac.gen(fv.getSize(), fv.getAlign());
    addresses.add(new Assign(result, fv)); 
    addresses.add(new Goto(endL));
    
    // True case.
    
    addresses.add(new Label(tL));
    Var tv = truT.getValue(tFac, lFac, addresses);
    addresses.add(new Assign(result, tv));
    addresses.add(new Label(endL));
    
    return result;
  }
}