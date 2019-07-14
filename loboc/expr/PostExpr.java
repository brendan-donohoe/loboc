package expr;

import java.util.LinkedList;

import error.AssignableError;
import error.SemanticError;
import general.Consts;
import general.LabelFactory;
import general.TempFactory;
import symtable.SymbolTable;
import threeaddress.Assign;
import threeaddress.BOpType;
import threeaddress.Binary;
import threeaddress.Deref;
import threeaddress.DerefAndAssign;
import threeaddress.Imm;
import threeaddress.ThreeAddress;
import threeaddress.Var;
import type.InvalidType;
import type.PointerType;
import type.Type;

/**
 * PostExpr.java
 *
 * @version 1.0
 *
 * @author Brendan Donohoe
 *
 * Class representing an expression with a postfix operator (either ++ or --).
 */

public class PostExpr extends UnaryOp
{
  private PostOp op;
  
  public PostExpr(SymbolTable tb, Expr e, PostOp op)
  {
    super(tb, e);
    this.op = op;
  }
  
  public boolean isPostfix()
  {
    return true;
  }
  
  public String toString(boolean anno)
  {
    String eStr = e.toString(anno);
    
    String resultStr = "";
    
    if (anno)
    {
      resultStr += " " + type.getShorthand() + ":";
    }
    
    resultStr += "(" + eStr + (op == PostOp.INCR ? "++" : "--") + ")";
    return resultStr;
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
    else
    {
      return InvalidType.INVALID_TYPE;
    }
  }
  
  public String getOp()
  {
    if (op == PostOp.INCR)
    {
      return "++";
    }
    else
    {
      return "--";
    }
  }
  public String [] getExpectedTypes()
  {
    // For increment and decrement, we needed an integer or a pointer.
      
    String [] arr = {"unsigned", "signed", "pointer"};
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
    // Firstly, figure out which operation we will be performing and the
    // numeric operand we will use.
    
    BOpType opType;
    
    if (op == PostOp.INCR)
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
    
    // Now, we'll store our operand in a temporary.
    
    Var operandTemp = tFac.gen(Consts.INT_SIZE, Consts.INT_ALIGN);
    addresses.add(new Assign(operandTemp, operand));
    
    // Now, generate a temporary which will hold the value of whatever we
    // applied this operation to.
    
    Var returnTemp = tFac.gen(e.getType().getSize(),
      e.getType().getAlignment());
    
    if (e.isIdentifier())
    {
      // We're working with an identifier.  It suffices to get its value and
      // copy it over to the temp, after which we increment.
      
      Var val = e.getValue(tFac, lFac, addresses);
      addresses.add(new Assign(returnTemp, val));
      addresses.add(new Binary(val, val, operandTemp, opType));
    }
    else
    {
      // We're working with some other addressable value.  Get its address,
      // dereference it to get whatever the current value of the expression is,
      // and copy that over to the temp.
      
      Var address = e.getAddress(tFac, lFac, addresses);
      Var deref = tFac.gen(e.type.getSize(), e.type.getAlignment());
      addresses.add(new Assign(deref, new Deref(address, deref.getSize())));
      addresses.add(new Assign(returnTemp, deref));
      
      // Afterward, add one to the entity at that address.
      
      Var sum = tFac.gen(e.type.getSize(), e.type.getAlignment());
      addresses.add(new Binary(sum, deref, operandTemp, opType));
      addresses.add(new DerefAndAssign(address, sum, sum.getSize()));
    }
    
    // Finally, return the temp.
    
    return returnTemp;
  }
  
  public static enum PostOp
  {
    INCR,
    DECR
  };
}