package expr;

import general.LabelFactory;
import general.TempFactory;

import java.util.LinkedList;

import symtable.SymbolTable;
import threeaddress.Assign;
import threeaddress.ThreeAddress;
import threeaddress.BOpType;
import threeaddress.Var;
import type.Type;

/**
 * ListExpr.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Class representing the comma binary operator.
 */
public class ListExpr extends BinaryOp
{
  public ListExpr(SymbolTable tb, Expr e1, Expr e2)
  {
    super(tb, e1, e2);
  }
  
  protected Type computeType()
  {
    // The result of this expression is simply the result of the second value,
    // so, we can infer that the type of this expression is simply the type of
    // the second value - whatever it may be.

    Type t2 = e2.getType();
    
    return t2;
  }
  
  public String getOp()
  {
    return ",";
  }
  
  public String [] [] getExpectedTypes()
  {
    // This node's type would only be marked as invalid if and only if its
    // second child is invalid.  Thus, by the approach of typeErrors used in
    // binary op, as it will never be the case that the type is valid but the
    // second child has an invalid type, we can never generate an error in this
    // node.  So just return null.
    
    return null;
  }
  
  public int getFoldResult(int n1, int n2, Type t1, Type t2)
  {
    // We simply return the second value in our expression.
    
    return n2;
  }
  
  public Var getValue(TempFactory tFac, LabelFactory lFac,
    LinkedList<ThreeAddress> addresses)
  {
    // We call getValue on the LHS to carry out any side-effects, but we
    // discard its value.
    
    e1.getValue(tFac, lFac, addresses);
    
    // We then return the value of the right operand.
    
    Var t2 = e2.getValue(tFac, lFac, addresses);
    Var tResult = tFac.gen(t2.getSize(), t2.getAlign());
    addresses.add(new Assign(tResult, t2));
    
    return tResult;
  }
  
  public BOpType getOpType()
  {
    return null;
  }
}