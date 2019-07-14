package expr;

import java.util.LinkedList;

import error.SemanticError;
import general.GeneralUtils;
import general.LabelFactory;
import general.TempFactory;
import symtable.SymbolTable;
import threeaddress.Assign;
import threeaddress.Imm;
import threeaddress.TempVar;
import threeaddress.ThreeAddress;
import threeaddress.Var;
import type.PrimType;
import type.Type;

/**
 * Num.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Class representing a single number.
 */
public class Num extends Expr
{
  private int bits;
  
  public Num(SymbolTable tb, String dataStr)
  {
    super(tb);
    
    // As long as we successfully parsed this number token, we know that it's a
    // legal value.  Parse its string representation as a long and fit its bits
    // into an int.  We'll also record its type here based on its numerical
    // range (less than the max value of a signed int gives it a signed type,
    // otherwise it's in the range 2147483647 to 4294967295, so it's an
    // unsigned type.
    
    long val = Long.parseLong(dataStr);
    
    bits = (int) val;
    
    // We'll check the numerical range our value falls into in order to
    // determine its type, since this is trivial to record here for later,
    // rather than have to drag around a flag.
    
    if (val <= 2147483647L)
    {
      this.type = PrimType.SIGNED_TYPE;
    }
    else
    {
      this.type = PrimType.UNSIGNED_TYPE;
    }
  }
  
  public Num(SymbolTable tb, int bits, Type type)
  {
    super(tb);
    this.bits = bits;
    this.type = type;
  }
  
  public int getBits()
  {
    return bits;
  }
  
  public String toString(boolean anno)
  {
    String resultStr = "";
    
    if (anno)
    {
      resultStr += " " + type.getShorthand() + ":";
    }
    
    // Get the string representation of this number based on its type.
    
    if (type.isUnsigned())
    {
      resultStr += GeneralUtils.unsignedIntToLong(bits);
    }
    else
    {
      resultStr += bits;
    }
    
    return resultStr;
  }
  
  public boolean isPostfix()
  {
    return true;
  }
  
  public boolean isConstant()
  {
    return true;
  }
  
  public void labelType(LinkedList<SemanticError> errors)
  {
    // We do nothing since we already have our type.
  }
  
  public Expr fold()
  {
    return this;
  }
  
  public Var getValue(TempFactory tFac, LabelFactory lFac,
    LinkedList<ThreeAddress> addresses)
  {
    TempVar t = tFac.gen(type.getSize(), type.getAlignment());
    
    if (type.isBool())
    {
      addresses.add(new Assign(t, new Imm(bits)));
    }
    else
    {
      addresses.add(new Assign(t, new Imm(bits)));
    }
    
    return t;
  }
}