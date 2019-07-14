package type;

public class InvalidType extends Type
{
  public static final InvalidType INVALID_TYPE = new InvalidType();
  
  public String toString()
  {
    return "invalid";
  }
  
  public boolean equals(Type other)
  {
    return other.isInvalid();
  }
  
  public boolean isInvalid()
  {
    return true;
  }
  
  public String getShorthand()
  {
    return "I";
  }
  
  public int computeSize()
  {
    return 0;
  }
  
  public int computeAlignment()
  {
    return 0;
  }
}