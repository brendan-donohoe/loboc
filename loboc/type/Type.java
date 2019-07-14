package type;

/**
 * Type.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * General interface shared by all types.
 */
public abstract class Type
{
  /**
   * The cached size of this type, in bytes.
   */
  private int size = -1;
  
  /**
   * The cached alignment of this type, in bytes.
   */
  private int alignment = -1;
  
  public int getSize()
  {
    if (size == -1)
    {
      size = computeSize();
    }
    return size;
  }
  
  public int getAlignment()
  {
    if (alignment == -1)
    {
      alignment = computeAlignment();
    }
    return alignment;
  }
  
  /**
   * Determine whether or not this type is a primitive type.
   * @return True if this type is a primitive type, and false otherwise.
   */
  public boolean isPrimitive()
  {
    return false;
  }
  
  /**
   * Determine whether or not this type is an integer type (either an unsigned
   * int or a signed int).
   * @return True if this type is an integer type, and false otherwise.
   */
  public boolean isInt()
  {
    return false;
  }
  
  /**
   * Determine whether or not this type is a signed integer type.
   * @return True if this type is a signed integer, and false otherwise.
   */
  public boolean isSigned()
  {
    return false;
  }
  
  /**
   * Determine whether or not this type is an unsigned integer type.
   * @return True if this type is a signed integer, and false otherwise.
   */
  public boolean isUnsigned()
  {
    return false;
  }
  
  /**
   * Determine whether or not this type is a boolean type.
   * @return True if this type is a boolean, and false otherwise.
   */
  public boolean isBool()
  {
    return false;
  }
  
  /**
   * Determine whether or not this type is a pointer type.
   * @return True if this type is a pointer, and false otherwise.
   */
  public boolean isPointer()
  {
    return false;
  }
  
  /**
   * Determine whether or not this type is an array type.
   * @return True if this type is an array, and false otherwise.
   */
  public boolean isArray()
  {
    return false;
  }
  
  /**
   * Determine whether or not this type is a representation of an invalid type.
   * @return True if this is not a legal type, and false otherwise.
   */
  public boolean isInvalid()
  {
    return false;
  }
  
  /**
   * Return true if two types are identical and false otherwise.  Note that,
   * for this method to return an accurate result, all size expressions within
   * array types that are being compared must have been folded to numerical
   * constants previously.  Otherwise, equals will return false, even if the
   * size expressions in the array types being compared are identical.
   * @param other The type to be compared.
   * @return True if the types are identical - false otherwise.
   */
  public abstract boolean equals(Type other);
  
  /**
   * Get a shorthand representation of this type, to be attached to the
   * expressions in the Spike 4 output.
   * @return A shortened string representation of this type.
   */
  public abstract String getShorthand();
  
  /**
   * Get the size of this type (or zero if this type is invalid, i.e., couldn't
   * be constant-folded).
   * @return The size of this type, in bytes.
   */
  protected abstract int computeSize();
  
  /**
   * Get the alignment of this type (or zero if this type is invalid).
   * @return The alignment of this type, in bytes.
   */
  protected abstract int computeAlignment();
}