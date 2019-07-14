package type;

import expr.Expr;
import expr.Num;

/**
 * ArrayType.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Class representing an array type.
 */
public class ArrayType extends Type
{
  /**
   * Type of the enclosing type.
   */
  private Type type;
  
  /**
   * The length expression.
   */
  private Expr len;
  
  public ArrayType(Type type, Expr len)
  {
    this.type = type;
    this.len = len;
  }
  
  public Type getType()
  {
    return type;
  }
  
  public String toString()
  {
    return type.toString() + "[" + len.toString(false) + "]";
  }
  
  public boolean equals(Type other)
  {
    if (other.isArray())
    {
      // In order to compare array types, both array sizes must have undergone
      // constant folding prior to comparison.  Thus, both must be numbers
      // which can be easily compared.
      
      ArrayType otherType = (ArrayType) other;
      
      Expr otherLen = otherType.len;
      if (len.isConstant() && otherLen.isConstant())
      {
        return ((Num) len).getBits() == ((Num) otherLen).getBits()
          && (type.equals(otherType.type));
      }
      else
      {
        return false;
      }
    }
    else
    {
      return false;
    }
  }
  
  public boolean isArray()
  {
    return true;
  }
  
  public String getShorthand()
  {
    return type.getShorthand() + "[" + len.toString(false) + "]";
  }
  
  public int getNumElements()
  {
    // It's assumed that we'll perform this step if we have no errors - so
    // everything is properly constant folded already.
    
	if (!type.isArray())
	{
	  return 1;
	}
	else
	{
      return ((Num) len).getBits() * ((ArrayType) type).getNumElements();
    }
  }
  
  public int computeSize()
  {
    if (len.isConstant())
    {
      return ((Num) len).getBits() * type.computeSize();
    }
    return 0;
  }
  
  public int computeAlignment()
  {
    if (len.isConstant())
    {
      return type.computeAlignment();
    }
    return 0;
  }
}