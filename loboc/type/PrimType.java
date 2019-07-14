package type;

/**
 * PrimType.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Class representing a simple primitive type - either a boolean, a signed int,
 * or an unsigned int.
 */
public class PrimType extends Type
{
  public static final PrimType BOOL_TYPE = new PrimType(PrimT.BOOLEAN);
  public static final PrimType UNSIGNED_TYPE = new PrimType(PrimT.UNSIGNED);
  public static final PrimType SIGNED_TYPE = new PrimType(PrimT.SIGNED);
  
  private PrimT primT;
  
  public PrimType(PrimT prim)
  {
    this.primT = prim;
  }
  
  public String toString()
  {
    switch (primT)
    {
      case BOOLEAN : return "bool";
      case SIGNED : return "signed";
      case UNSIGNED : return "unsigned";
      default : return null;
    }
  }
  
  public boolean equals(Type other)
  {
    if (!other.isPrimitive())
    {
      return false;
    }
    
    return primT == ((PrimType) other).primT;
  }
  
  public String getShorthand()
  {
    switch (primT)
    {
      case BOOLEAN : return "B";
      case SIGNED : return "S";
      case UNSIGNED : return "U";
      default : return null;
    }
  }
  
  public boolean isPrimitive()
  {
    return true;
  }
  
  public boolean isInt()
  {
    return primT == PrimT.SIGNED || primT == PrimT.UNSIGNED;
  }
  
  public boolean isSigned()
  {
    return primT == PrimT.SIGNED;
  }
  
  public boolean isUnsigned()
  {
    return primT == PrimT.UNSIGNED;
  }
  
  public boolean isBool()
  {
    return primT == PrimT.BOOLEAN;
  }
  
  public int computeSize()
  {
    return primT == PrimT.BOOLEAN ? general.Consts.BOOL_SIZE :
      general.Consts.INT_SIZE;
  }
  
  public int computeAlignment()
  {
    return primT == PrimT.BOOLEAN ? general.Consts.BOOL_ALIGN :
      general.Consts.INT_ALIGN;
  }
  
  public static enum PrimT
  {
    BOOLEAN,
    SIGNED,
    UNSIGNED
  };
}