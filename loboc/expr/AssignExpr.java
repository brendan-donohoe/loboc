package expr;

import java.util.LinkedList;

import error.AssignableError;
import error.SemanticError;
import general.Consts;
import general.LabelFactory;
import general.TempFactory;
import symtable.SymbolTable;
import threeaddress.Assign;
import threeaddress.DerefAndAssign;
import threeaddress.ThreeAddress;
import threeaddress.BOpType;
import threeaddress.UOpType;
import threeaddress.Unary;
import threeaddress.Var;
import type.InvalidType;
import type.Type;

/**
 * AssignExpr.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Class representing the assignment of one expression to another.
 */
public class AssignExpr extends BinaryOp
{
  public AssignExpr(SymbolTable tb, Expr e1, Expr e2)
  {
    super(tb, e1, e2);
  }
  
  public void labelType(LinkedList<SemanticError> errors)
  {
    // First, we'll call the parent's version of the method to perform the type
    // labeling.
    
    super.labelType(errors);
    
    // Then, if we have a valid typing from that method, we'll conduct one more
    // test, and attempt to verify that the LHS of this expression is
    // assignable.
    
    if (errors != null && !type.isInvalid() && !e1.isAssignable())
    {
      // We have a non-assignable expression on the LHS.  Signal this as an
      // error and label this node to be invalid.
      
      type = InvalidType.INVALID_TYPE;
      errors.add(new AssignableError(e1, getOp()));
    }
  }
  
  protected Type computeType()
  {
    Type t1 = e1.getType();
    Type t2 = e2.getType();
    
    if ((t1.isPointer() && t2.isPointer() && t1.equals(t2))
      || t1.isPrimitive() && t2.isPrimitive())
    {
      return t1;
    }
    else
    {
      return InvalidType.INVALID_TYPE;
    }
  }
  
  public String getOp()
  {
    return "=";
  }
  
  public String [] [] getExpectedTypes()
  {
    Type t1 = e1.getType();
    Type t2 = e2.getType();
    
    String [] [] arr = new String [2] [];
    
    // First, look at our first type - is it valid?  If it is, then our second
    // type is not.
    
    if (t1.isPrimitive() || t1.isPointer())
    {
      String [] arr1 = {};
      arr[0] = arr1;
      
      if (t1.isPrimitive())
      {
        // This would have been valid as long as our second operand had a
        // numeric type.
        
        String [] arr2 = {"numeric"};
        arr[1] = arr2;
      }
      else
      {
        // The second operand would have needed to be a pointer - specifically,
        // a pointer of the same type as the first operand.
        
        String [] arr2 = {t1.toString()};
        arr[1] = arr2;
      }
    }
    else if (t2.isPrimitive() || t2.isPointer())
    {
      // Our second type is legal, whereas our first type is not.
      
      String [] arr2 = {};
      arr[1] = arr2;
      
      if (t2.isPrimitive())
      {
        String [] arr1 = {"numeric"};
        arr[0] = arr1;
      }
      else
      {
        String [] arr1 = {t2.toString()};
        arr[0] = arr1;
      }
    }
    else
    {
      // Neither type is legal.  Not much we can do but print out the full list
      // of possible types for both operands.
      
      String [] arr1 = {"numeric", "pointer"};
      String [] arr2 = {"numeric", "pointer"};
      
      arr[0] = arr1;
      arr[1] = arr2;
    }
    
    return arr;
  }
  
  public Expr fold()
  {
    e1 = e1.fold();
    e2 = e2.fold();
    
    return this;
  }
  
  public int getFoldResult(int n1, int n2, Type t1, Type t2)
  {
    // Method stub since this expression will never be constant folded.
    
    return -1;
  }
  
  public Var getValue(TempFactory tFac, LabelFactory lFac,
    LinkedList<ThreeAddress> addresses)
  {
    Var val;
    
    // First, we'll get the RHS of the assignment and, if it's a non-boolean
    // expression we're making into a boolean, we'll grab only its least
    // significant byte using the unary TOBY (to byte) operator, before getting
    // the value of the RHS.
    
    if (!e2.getType().isBool() && e1.type.isBool())
    {
      Var operand = e2.getValue(tFac, lFac, addresses);
      val = tFac.gen(Consts.BOOL_SIZE, Consts.BOOL_ALIGN);
      addresses.add(new Unary(val, operand, UOpType.TOBY));
    }
    else
    {
      val = e2.getValue(tFac, lFac, addresses);
    }
    
    // There are two possibilities - we have an identifier on the LHS, in which
    // case we can do a direct assignment, or we have a non-identifier, in
    // which case we'll need to perform our assignment using pointer
    // operations.
    
    if (e1.isIdentifier())
    {
      Var v = e1.getValue(tFac, lFac, addresses);
      addresses.add(new Assign(v, val));
    }
    else
    {
      // First, we'll get the address of the LHS.
    
      Var ptr = e1.getAddress(tFac, lFac, addresses);
      
      // Now, dereference our address and assign val to that location in memory.
      
      addresses.add(new DerefAndAssign(ptr, val, val.getSize()));
    }
    
    // Lastly, return val.
    
    return val;
  }
  
  public BOpType getOpType()
  {
    return null;
  }
}