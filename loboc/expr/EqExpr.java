package expr;

import symtable.SymbolTable;
import threeaddress.BOpType;
import type.InvalidType;
import type.PrimType;
import type.Type;

/**
 * EqExpr.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Class representing equality comparison of two expressions.
 */
public class EqExpr extends BinaryOp
{
  private EqOp op;
  
  public EqExpr(SymbolTable tb, Expr e1, Expr e2, EqOp op)
  {
    super(tb, e1, e2);
    this.op = op;
  }
  
  protected Type computeType()
  {
    Type t1 = e1.getType();
    Type t2 = e2.getType();
    
    if (t1.equals(t2) ||
      (t1.isPrimitive() && t2.isPrimitive()))
    {
      return PrimType.BOOL_TYPE;
    }
    else
    {
      return InvalidType.INVALID_TYPE;
    }
  }
  
  public String getOp()
  {
    return op == EqOp.EQ ? "==" : "!=";
  }
  
  public String [] [] getExpectedTypes()
  {
    Type t1 = e1.type;
    
    String [] [] arr = new String [2] [];
    
    // Since we have a rule that states that both operands can be of any type,
    // provided they are equal, we will not have a case where both types are
    // invalid.  So, this is particularly easy.
    
    String [] arr1 = {};
    arr[0] = arr1;
    
    if (t1.isPrimitive())
    {
      // Our first type was a numeric, so our second type would have needed to
      // be a numeric as well.
      
      String [] arr2 = {"numeric"};
      arr[1] = arr2;
    }
    else
    {
      // Our second type just needed to match our first type.
      
      String [] arr2 = {t1.toString()};
      arr[1] = arr2;
    }
    
    return arr;
  }
  
  public int getFoldResult(int n1, int n2, Type t1, Type t2)
  {
    if (op == EqOp.EQ)
    {
      return (n1 == n2) ? 1 : 0;
    }
    else
    {
      return (n1 != n2) ? 1 : 0;
    }
  }
  
  public BOpType getOpType()
  {
    return op == EqOp.EQ ? BOpType.EQ : BOpType.NE;
  }
  
  public static enum EqOp
  {
    EQ,
    NOTEQ
  };
}