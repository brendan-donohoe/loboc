package expr;

import general.LabelFactory;
import general.TempFactory;

import java.util.LinkedList;

import symtable.SymbolTable;
import threeaddress.ThreeAddress;
import threeaddress.UOpType;
import threeaddress.Unary;
import threeaddress.Var;
import type.InvalidType;
import type.Type;

/**
 * NegExpr.java
 *
 * @version 1.0
 *
 * @author Brendan Donohoe
 *
 * Class representing a negated expression.
 */

public class NegExpr extends UnaryOp
{
  public NegExpr(SymbolTable tb, Expr e)
  {
    super(tb, e);
  }
  
  protected Type computeType()
  {
    Type t = e.getType();

    if (t.isInt())
    {
      return t;
    }
    
    return InvalidType.INVALID_TYPE;
  }
  
  public String getOp()
  {
    return "-";
  }
  
  public String [] getExpectedTypes()
  {
    // We just needed some type of integer.
    
    String [] arr = {"unsigned", "signed"};
    return arr;
  }
  
  public Expr fold()
  {
    e = e.fold();
    
    if (e.isConstant() && type.isPrimitive())
    {
      Num n = (Num) e;
      
      int b = n.getBits();
      
      // Negation is the same for a signed value as for an unsigned value, so
      // calculating this is equivalent to negating a Java int and taking the
      // most significant 32 bits.
      
      e = null;
      
      return new Num(tb, -b, type);
    }
    
    return this;
  }
  
  public Var getValue(TempFactory tFac, LabelFactory lFac,
    LinkedList<ThreeAddress> addresses)
  {
    Var t = e.getValue(tFac, lFac, addresses);
    
    Var result = tFac.gen(type.getSize(), type.getAlignment());
    
    addresses.add(new Unary(result, t, UOpType.NEG));
    return result;
  }
}