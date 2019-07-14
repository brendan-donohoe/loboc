package expr;

import java.util.LinkedList;

import error.AddressableError;
import error.SemanticError;
import general.LabelFactory;
import general.TempFactory;
import symtable.SymbolTable;
import threeaddress.ThreeAddress;
import threeaddress.Var;
import type.InvalidType;
import type.PointerType;
import type.Type;

/**
 * AddressExpr.java
 *
 * @version 1.0
 *
 * @author Brendan Donohoe
 *
 * Class representing an expression with an address (&) operator.
 */

public class AddressExpr extends UnaryOp
{
  public AddressExpr(SymbolTable tb, Expr e)
  {
    super(tb, e);
  }
  
  public void labelType(LinkedList<SemanticError> errors)
  {
    e.labelType(errors);
    
    type = computeType();
    
    if (errors != null && type.isInvalid() && !e.getType().isInvalid())
    {
      errors.add(new AddressableError(e));
    }
  }
  
  protected Type computeType()
  {
    Type t = e.getType();
    
    // If the type we're acting on is an l-value, then this yields a pointer
    // to that type.
    
    if (e.isAssignable())
    {
      return new PointerType(t);
    }
    
    return InvalidType.INVALID_TYPE;
  }
  
  public String getOp()
  {
    return "&";
  }
  
  public String [] getExpectedTypes()
  {
    // We do not use this function in handling our error.  Simply return null.
    
    return null;
  }
  
  public Expr fold()
  {
    // We cannot constant fold this expression - simply fold the child.
    
    e = e.fold();
    
    return this;
  }
  
  public Var getValue(TempFactory tFac, LabelFactory lFac,
    LinkedList<ThreeAddress> addresses)
  {
    // The value of this expression is the address of our contained expression.

    return e.getAddress(tFac, lFac, addresses);
  }
}