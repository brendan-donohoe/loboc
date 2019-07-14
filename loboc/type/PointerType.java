package type;

/**
 * PointerType.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Class representing a pointer type.
 */
public class PointerType extends Type
{
  /**
   * Type of the enclosing type.
   */
  private Type type;
  
  public PointerType(Type type)
  {
    this.type = type;
  }
  
  public Type getType()
  {
    return type;
  }
  
  public String toString()
  {
    return type.toString() + "[]";
  }
  
  public boolean equals(Type other)
  {
    if (other.isPointer())
    {
      return type.equals(((PointerType) other).type);
    }
    
    return false;
  }
  
  public boolean isPointer()
  {
    return true;
  }
  
  public String getShorthand()
  {
    return type.getShorthand() + "[]";
  }
  
  public int computeSize()
  {
    return general.Consts.POINTER_SIZE;
  }
  
  public int computeAlignment()
  {
    return general.Consts.POINTER_ALIGN;
  }
}