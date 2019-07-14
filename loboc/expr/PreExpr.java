package expr;

import java.util.LinkedList;

import error.AssignableError;
import error.SemanticError;
import general.Consts;
import general.LabelFactory;
import general.TempFactory;
import symtable.SymbolTable;
import threeaddress.Assign;
import threeaddress.Binary;
import threeaddress.Deref;
import threeaddress.DerefAndAssign;
import threeaddress.Imm;
import threeaddress.ThreeAddress;
import threeaddress.BOpType;
import threeaddress.Var;
import type.InvalidType;
import type.PointerType;
import type.Type;

/**
 * PostfixExpr.java
 *
 * @version 1.0
 *
 * @author Brendan Donohoe
 *
 * Class representing an expression with a prefix increment/decrement operator.
 */

public class PreExpr extends UnaryOp
{
  private PreOp op;
  
  public PreExpr(SymbolTable tb, Expr e, PreOp op)
  {
    super(tb, e);
    this.op = op;
  }
  
  public void labelType(LinkedList<SemanticError> errors)
  {
    // First, label our expression with types as normal.
    
    super.labelType(errors);
    
    // Then, verify that this expression is assignable.
    
    if (errors != null && !type.isInvalid() && !e.isAssignable())
    {
      // We are not acting on an assignable expression.  Signal this as an
      // error.
      
      type = InvalidType.INVALID_TYPE;
      errors.add(new AssignableError(e, getOp()));
    }
  }
  
  protected Type computeType()
  {
    Type t = e.getType();

    if (t.isInt() || t.isPointer())
    {
      return t;
    }
    
    return InvalidType.INVALID_TYPE;
  }
  
  public String getOp()
  {
    return op == PreOp.INCR ? "++" : "--";
  }
  
  public String [] getExpectedTypes()
  {
    // We needed an integer or a pointer.
    
    String [] arr = {"unsigned", "signed", "pointer"};
    return arr;
  }
  
  public Expr fold()
  {
    // Simply fold the child.
    
    e = e.fold();
    
    return this;
  }
  
  public Var getValue(TempFactory tFac, LabelFactory lFac,
    LinkedList<ThreeAddress> addresses)
  {
    // We have two possibilities.  We're either dealing with an identifier, in
    // which case it is sufficient to nab its value, and perform the addition
    // on that result.
    
    // But first, let's get the operation and its operand.
    
    BOpType opType;
    
    if (op == PreOp.INCR)
    {
      opType = BOpType.ADD;
    }
    else
    {
      opType = BOpType.SUB;
    }
    
    Imm operand;
    
    if (type.isPointer())
    {
      // If we have a pointer, we add the size of the type contained in the
      // pointer so that we move to the next legal address.
      
      PointerType pType = (PointerType) type;
      
      operand = new Imm(pType.getType().getSize());
    }
    else
    {
      // The operand is just one.
      
      operand = Imm.IMM_ONE;
    }
    
    Var operandTemp = tFac.gen(Consts.INT_SIZE, Consts.INT_ALIGN);
    addresses.add(new Assign(operandTemp, operand));
    
    if (e.isIdentifier())
    {
      // We have an identifier.  Get its value and perform the relevant
      // operation, returning the result.
      
      Var val = e.getValue(tFac, lFac, addresses);
      addresses.add(new Binary(val, val, operandTemp, opType));
      return val;
    }
    else
    {
      // Get the address of whatever we contain, dereference it to get the
      // underlying value, perform the relevant operation on the value, and
      // dereference the original address and assign it our new result.
      
      Var address = e.getAddress(tFac, lFac, addresses);
      Var deref = tFac.gen(e.type.getSize(), e.type.getAlignment());
      addresses.add(new Assign(deref, new Deref(address, deref.getSize())));
      
      Var sum = tFac.gen(e.type.getSize(), e.type.getAlignment());
      addresses.add(new Binary(sum, deref, operandTemp, opType));
      addresses.add(new DerefAndAssign(address, sum, sum.getSize()));
      return sum;
    }
  }
  
  public static enum PreOp
  {
    DECR,
    INCR
  };
}