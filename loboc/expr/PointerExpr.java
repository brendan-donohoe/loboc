package expr;

import java.util.LinkedList;

import general.Consts;
import general.LabelFactory;
import general.TempFactory;
import symtable.SymbolTable;
import threeaddress.Assign;
import threeaddress.Deref;
import threeaddress.ThreeAddress;
import threeaddress.Var;
import type.InvalidType;
import type.PointerType;
import type.Type;

/**
 * PointerExpr.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Class representing a single pointer access.
 */

public class PointerExpr extends UnaryOp
{
  public PointerExpr(SymbolTable tb, Expr e)
  {
    super(tb, e);
  }
  
  public String toString(boolean anno)
  {
    String eStr = e.toString(anno);
    
    String resultStr = "";
    
    if (anno)
    {
      resultStr += " " + type.getShorthand() + ":";
    }
    
    resultStr += "(" + eStr + getOp() + ")";
    return resultStr;
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
    Type t = e.getType();
      
    if (t.isPointer())
    {
      return ((PointerType) t).getType();
    }

    return InvalidType.INVALID_TYPE;
  }
  
  public String getOp()
  {
    return "[]";
  }
  
  public String [] getExpectedTypes()
  {
    String [] arr = {"pointer"};
    return arr;
  }
  
  public Expr fold()
  {
    e = e.fold();
    
    return this;
  }
  
  public Var getValue(TempFactory tFac, LabelFactory lFac,
    LinkedList<ThreeAddress> addresses)
  {
    // The value of this pointer dereference is the dereference of whatever
    // value the parent pointer contains.

    Var ptr = e.getValue(tFac, lFac, addresses);
    Var deref = tFac.gen(type.getSize(), type.getAlignment());
    
    // If we're pointing to a raw array - not an array element, but a raw
    // array, we're pointing to the address of the start location of the
    // array.  Thus, dereferencing would produce the value at the start of the
    // array.  But we need dereferencing to produce the address of the start of
    // the array instead.  So, rather than dereference in the array pointer
    // case, we simply copy the contents of the pointer - that is, the memory
    // address of the start of the array - and return it.  Otherwise, we do
    // perform the dereference and return whatever is at the location we're
    // pointing to.
    
    if (type.isArray())
    {
      deref = tFac.gen(Consts.POINTER_SIZE, Consts.POINTER_ALIGN);
      addresses.add(new Assign(deref, ptr));
    }
    else
    {
      deref = tFac.gen(type.getSize(), type.getAlignment());
      addresses.add(new Assign(deref, new Deref(ptr, deref.getSize())));
    }

    return deref;
  }
  
  public Var getAddress(TempFactory tFac, LabelFactory lFac,
    LinkedList<ThreeAddress> addresses)
  {
    // The address of this pointer dereference is quite simple - it's the value
    // of the parent pointer, seeing as its value is our address!
    
    return e.getValue(tFac, lFac, addresses);
  }
}