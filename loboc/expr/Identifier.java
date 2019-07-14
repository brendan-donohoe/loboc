package expr;

import java.util.LinkedList;

import error.SemanticError;
import error.UndeclaredUseError;
import general.Consts;
import general.LabelFactory;
import general.TempFactory;
import symtable.SymbolData;
import symtable.SymbolTable;
import threeaddress.AddressOf;
import threeaddress.Assign;
import threeaddress.ThreeAddress;
import threeaddress.UserVar;
import threeaddress.Var;
import type.InvalidType;
import type.Type;

/**
 * Identifier.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Class representing a single identifier.
 */
public class Identifier extends Expr
{
  private String id;
  
  public Identifier(SymbolTable tb, String id)
  {
    super(tb);
    this.id = id;
  }
  
  public String toString(boolean anno)
  {
    String resultStr = "";
    
    if (anno)
    {
      resultStr += " " + type.getShorthand() + ":";
    }
    resultStr += id;
    
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
  
  public boolean isIdentifier()
  {
    return true;
  }
  
  public Type computeType()
  {
    // Just look up our identifier in our table and get its type.
    
    if (tb != null)
    {
      SymbolData data = tb.deepGet(id);  
      
      if (data.isDeclared())
      {
        return data.getType();
      }
    }
    
    // This identifier wasn't declared, but we're using it anyway!
    
    return InvalidType.INVALID_TYPE;
  }
  
  public void labelType(LinkedList<SemanticError> errors)
  {
    // We'll compute the identifier's type by looking it up in our table.
    
    type = computeType();
    
    // If we're actually using an identifier without having declared it first,
    // however, that's a big fat error.
    
    if (errors != null && type.isInvalid())
    {
      errors.add(new UndeclaredUseError(id));
    }
  }
  
  public Expr fold()
  {
    return this;
  }
  
  public Var getValue(TempFactory tFac, LabelFactory lFac,
    LinkedList<ThreeAddress> addresses)
  {
    // If a is an array, we treat it as a pointer to the memory location of
    // this variable - denoting its start.  So in this case, we do not return
    // just the identifier a, but the memory address of a.

    if (type.isArray())
    {
      return getAddress(tFac, lFac, addresses);
    }
    
    // Otherwise, we just return a UserVar object representing this identifier.
    
    SymbolData data = tb.deepGet(id);
    
    int vId = data.getId();
    int offset = data.getOffset();
    
    Var v = new UserVar(vId, type.getSize(), type.getAlignment(), offset);
    
    return v;
  }
  
  public Var getAddress(TempFactory tFac, LabelFactory lFac,
    LinkedList<ThreeAddress> addresses)
  {
    SymbolData data = tb.deepGet(id);
    
    int vId = data.getId();
    int offset = data.getOffset();
    
    Var v = new UserVar(vId, type.getSize(), type.getAlignment(), offset);
    
    Var t = tFac.gen(Consts.POINTER_SIZE, Consts.POINTER_ALIGN);
    
    addresses.add(new Assign(t, new AddressOf(v)));
    
    return t;
  }
}