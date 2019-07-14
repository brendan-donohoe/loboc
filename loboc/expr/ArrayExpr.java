package expr;

import java.util.LinkedList;

import general.Consts;
import general.LabelFactory;
import general.TempFactory;
import symtable.SymbolTable;
import threeaddress.Assign;
import threeaddress.Binary;
import threeaddress.BOpType;
import threeaddress.Deref;
import threeaddress.Imm;
import threeaddress.TempVar;
import threeaddress.ThreeAddress;
import threeaddress.Var;
import type.ArrayType;
import type.InvalidType;
import type.Type;

/**
 * ArrayExpr.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Class representing a single array expression, which includes both: the name
 * of the array, and the index of access.
 */

public class ArrayExpr extends BinaryOp
{
  public ArrayExpr(SymbolTable tb, Expr e1, Expr e2)
  {
    super(tb, e1, e2);
  }
  
  public String toString(boolean anno)
  {
    String e1Str = e1.toString(anno);
    String e2Str = e2.toString(anno);
    
    String returnStr = "";
    
    if (anno)
    {
      returnStr += " " + type.getShorthand() + ":";
    }
    
    returnStr += "(" + e1Str + "[" + e2Str + "])";
    return returnStr;
  }
  
  public boolean isPostfix()
  {
    return true;
  }
  
  public boolean isAssignable()
  {
    return true;
  }
  
  public Type computeType()
  {
    Type t1 = e1.getType();
    Type t2 = e2.getType();
      
    if (t1.isArray() && t2.isPrimitive())
    {
      return ((ArrayType) t1).getType();
    }

    return InvalidType.INVALID_TYPE;
  }
  
  public String getOp()
  {
    return "array access";
  }
  
  public String [] [] getExpectedTypes()
  {
    String [] [] arr = new String [2] [];  
    
    if (!e1.getType().isArray() && e2.getType().isPrimitive())
    {
      String [] arr1 = {"array"};
      String [] arr2 = {};
      
      arr[0] = arr1;
      arr[1] = arr2;
    }
    else if (e1.getType().isArray() && !e2.getType().isPrimitive())
    {
      String [] arr1 = {};
      String [] arr2 = {"numeric"};;
      
      arr[0] = arr1;
      arr[1] = arr2;
    }
    else
    {
      String [] arr1 = {"array"};
      String [] arr2 = {"numeric"};;

      arr[0] = arr1;
      arr[1] = arr2;
    }
    
    return arr;
  }
  
  public Expr fold()
  {
    // This cannot be folded to a constant, so just fold the children.

    e1 = e1.fold();
    e2 = e2.fold();
    
    return this;
  }
  
  public int getFoldResult(int n1, int n2, Type t1, Type t2)
  {
    // Method stub since fold will never call this function.
    
    return -1;
  }
  
  public Var getValue(TempFactory tFac, LabelFactory lFac,
    LinkedList<ThreeAddress> addresses)
  {
    // We treat arrays (even multidimensional ones) as pointers under the hood,
    // which we only actually dereference and get the value out of once we end
    // up accessing a one-dimensional array.  Otherwise, we simply return the
    // address of our value, or, equivalently, a single-dimensional pointer to
    // that location in memory.
    
    Var address = getAddress(tFac, lFac, addresses);
    
    if (type.isArray())
    {
      return address;
    }
    else
    {
      TempVar val = tFac.gen(type.getSize(), type.getAlignment());
      
      addresses.add(new Assign(val, new Deref(address, val.getSize())));
      return val;
    }
  }
  
  public Var getAddress(TempFactory tFac, LabelFactory lFac,
    LinkedList<ThreeAddress> addresses)
  {
    // As we're treating arrays equivalently to single-dimensional pointers, we
    // take the address of the array, as well as the index, perform the
    // necessary arithmetic to compute the new memory offset from the array's
    // original address, add the two together, and return the result.
    
    Var ptr = e1.getValue(tFac, lFac, addresses);
    Var val = e2.getValue(tFac, lFac, addresses);
    
    Imm size = new Imm(type.getSize());
    Var sizeTemp = tFac.gen(Consts.INT_SIZE, Consts.INT_ALIGN);
    addresses.add(new Assign(sizeTemp, size));
    
    Var offset = tFac.gen(Consts.INT_SIZE, Consts.INT_ALIGN);
    addresses.add(new Binary(offset, val, sizeTemp, BOpType.MULTU));
    
    Var result = tFac.gen(Consts.INT_SIZE, Consts.INT_ALIGN);
    addresses.add(new Binary(result, ptr, offset, BOpType.ADD));
    
    return result;
  }
  
  public BOpType getOpType()
  {
    return null;
  }
}