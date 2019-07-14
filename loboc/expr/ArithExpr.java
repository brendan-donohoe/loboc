package expr;

import general.FoldingUtils;
import symtable.SymbolTable;
import threeaddress.BOpType;
import type.InvalidType;
import type.PrimType;
import type.Type;

/**
 * ArithExpr.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Class representing addition, subtraction, multiplication, and division
 * between two expressions.
 */
public class ArithExpr extends BinaryOp
{
  private ArithOp op;
  
  public ArithExpr(SymbolTable tb, Expr e1, Expr e2, ArithOp op)
  {
    super(tb, e1, e2);
    this.op = op;
  }
  
  protected Type computeType()
  {
    Type t1 = e1.getType();
    Type t2 = e2.getType();
    
    if (t1.isSigned() && t2.isSigned())
    {
      return PrimType.SIGNED_TYPE;
    }
    else if (t1.isInt() && t2.isInt())
    {
      return PrimType.UNSIGNED_TYPE;
    }
    else
    {
      return InvalidType.INVALID_TYPE;
    }
  }
  
  public String getOp()
  {
    if (op == ArithOp.ADD)
    {
      return "+";
    }
    else if (op == ArithOp.SUB)
    {
      return "-";
    }
    else if (op == ArithOp.MUL)
    {
      return "*";
    }
    else
    {
      return "/";
    }
  }
  
  public String [] [] getExpectedTypes()
  {
    Type t1 = e1.getType();
    Type t2 = e2.getType();
    
    String [] [] arr = new String [2] [];
    
    if (t1.isInt())
    {
      // The first operand is correctly typed, and the second just needs to be
      // an integer.
      
      String [] arr1 = {};
      String [] arr2 = {"unsigned", "signed"};
      arr[0] = arr1;
      arr[1] = arr2;
    }
    else if (t2.isInt())
    {
      String [] arr1 = {"unsigned", "signed"};
      String [] arr2 = {};
      arr[0] = arr1;
      arr[1] = arr2;
    }
    else
    {
      // Neither operand is validly typed.
      
      String [] arr1 = {"unsigned", "unsigned", "signed", "signed"};
      String [] arr2 = {"unsigned", "signed", "unsigned", "signed"};
      arr[0] = arr1;
      arr[1] = arr2;
    }
    
    return arr;
  }
  
  public Expr fold()
  {
    e1 = e1.fold();
    e2 = e2.fold();
    
    if (e1.isConstant() && e2.isConstant() && type.isPrimitive())
    {
      Num n1 = (Num) e1;
      Num n2 = (Num) e2;
      
      if (n2.getBits() == 0 && op == ArithOp.DIV)
      {
        // If we have a divide by zero error, do not proceed with the folding.
        
        return this;
      }
      
      // Otherwise, carry on as normal.
      
      int bitResult = getFoldResult(n1.getBits(), n2.getBits(), n1.getType(),
        n2.getType());
      
      e1 = null;
      e2 = null;
      
      return new Num(tb, bitResult, type);
    }
    
    return this;
  }
  
  public int getFoldResult(int n1, int n2, Type t1, Type t2)
  {
    if (op == ArithOp.ADD)
    {
      return n1 + n2;
    }
    else if (op == ArithOp.SUB)
    {
      return n1 - n2;
    }
    else if (op == ArithOp.MUL && type.isSigned())
    {
      return n1 * n2;
    }
    else if (op == ArithOp.MUL)
    {
      return FoldingUtils.multU(n1, n2);
    }
    else if (op == ArithOp.DIV && type.isSigned())
    {
      return n1 / n2;
    }
    else
    {
      return FoldingUtils.divU(n1, n2);
    }
  }
  
  public BOpType getOpType()
  {
    switch (op)
    {
      case ADD : return BOpType.ADD;
      case SUB : return BOpType.SUB;
      case MUL : return type.isUnsigned() ? BOpType.MULTU : BOpType.MULT;
      default : return type.isUnsigned() ? BOpType.DIVU : BOpType.DIV;
    }
  }
  
  public static enum ArithOp
  {
    ADD,
    SUB,
    MUL,
    DIV
  };
}