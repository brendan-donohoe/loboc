package expr;

import general.FoldingUtils;
import symtable.SymbolTable;
import threeaddress.BOpType;
import type.InvalidType;
import type.PrimType;
import type.Type;

/**
 * RelExpr.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Class representing ordered comparison of two numerical expressions.
 */
public class RelExpr extends BinaryOp
{
  private RelOp op;
  
  public RelExpr(SymbolTable tb, Expr e1, Expr e2, RelOp op)
  {
    super(tb, e1, e2);
    this.op = op;
  }
  
  protected Type computeType()
  {
    Type t1 = e1.getType();
    Type t2 = e2.getType();
    
    if (t1.equals(t2))
    {
      if (t1.isInt() || t1.isPointer())
      {
        return PrimType.BOOL_TYPE;
      }
    }
    
    return InvalidType.INVALID_TYPE;
  }
  
  public String getOp()
  {
    switch (op)
    {
      case LT : return "<";
      case LTE : return "<=";
      case GT : return ">";
      default : return ">=";
    }
  }
  
  public String [] [] getExpectedTypes()
  {
    Type t1 = e1.getType();
    Type t2 = e2.getType();
    
    String [] [] arr = new String [2] [];
    
    if (t1.isInt() || t1.isPointer())
    {
      // Our first operand is validly typed, and the second operand just needs
      // to match the type of the first.
      
      String [] arr1 = {};
      String [] arr2 = {t1.toString()};
      arr[0] = arr1;
      arr[1] = arr2;
    }
    else if (t2.isInt() || t2.isPointer())
    {
      // The reverse of the above.
      
      String [] arr1 = {t2.toString()};
      String [] arr2 = {};
      arr[0] = arr1;
      arr[1] = arr2;
    }
    else
    {
      // Neither operand is correctly typed.
      
      String [] arr1 = {"unsigned", "signed", "pointer"};
      String [] arr2 = {"unsigned", "signed", "pointer"};
      arr[0] = arr1;
      arr[1] = arr2;
    }
    
    return arr;
  }
  
  public int getFoldResult(int n1, int n2, Type t1, Type t2)
  {
    boolean result;
    
    // By our type constraints, both types are either unsigned integers
    // or signed integers.
    
    if (t1.isSigned())
    {
      switch (op)
      {
        case LT : result = n1 < n2; break;
        case LTE : result = n1 <= n2; break;
        case GT : result = n1 > n2; break;
        default : result = n1 >= n2;
      }
    }
    else
    {
      switch (op)
      {
        case LT : result = FoldingUtils.ltU(n1, n2); break;
        case LTE : result = FoldingUtils.lteU(n1, n2); break;
        case GT : result = FoldingUtils.gtU(n1, n2); break;
        default : result = FoldingUtils.gteU(n1, n2);
      }
    }
    
    return result ? 1 : 0;
  }
  
  public BOpType getOpType()
  {
    // First, check to see whether or not the arguments are unsigned - if so,
    // we use an unsigned relational operation.  If the arguments are signed,
    // on the other hand, we use the signed relational operation.

    if (e1.getType().isSigned())
    {
      switch (op)
      {
        case LT : return BOpType.LT;
        case LTE : return BOpType.LE;
        case GT : return BOpType.GT;
        default : return BOpType.GE;
      }
    }
    else
    {
      switch (op)
      {
        case LT : return BOpType.LTU;
        case LTE : return BOpType.LEU;
        case GT : return BOpType.GTU;
        default : return BOpType.GEU;
      }
    }
  }
  
  public static enum RelOp
  {
    LT,
    LTE,
    GT,
    GTE
  };
}