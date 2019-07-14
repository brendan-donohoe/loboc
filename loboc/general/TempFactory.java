package general;

import threeaddress.TempVar;

/**
 * TempFactory.java
 * 
 * @author Brendan Donohoe
 *
 * Factory class for generating temporary variables and assigning each a
 * particular memory offset on the stack, using a method analogous to that in
 * Spike 5a for generating offsets for user defined variables in the global
 * data segment.
 */
public class TempFactory
{
  private int tempNum;
  
  private int prevSize;
  private int prevOffset;
  
  public TempFactory()
  {
    tempNum = 0;
    prevSize = 0;
    prevOffset = 0;
  }
  
  public int getTempNum()
  {
    return tempNum;
  }
  
  public void setPrevSize(int prevSize)
  {
    this.prevSize = prevSize;
  }
  
  public int getPrevSize()
  {
    return prevSize;
  }
  
  public void setPrevOffset(int prevOffset)
  {
    this.prevOffset = prevOffset;
  }
  
  public int getPrevOffset()
  {
    return prevOffset;
  }
  
  /**
   * Generate a temporary variable with the correct offset, based on the size
   * and alignment passed in.
   * @param size - The size of the variable to be generated, in bytes.
   * @param align - The alignment of the variable to be generated.
   * @return The new temporary variable.
   */
  public TempVar gen(int size, int align)
  {
    TempVar t = new TempVar(tempNum, size, align, generateOffset(size, align));
    
    tempNum++;
    return t;
  }
  
  /**
   * Generate the offset corresponding to a newly created variable of size
   * curSize and alignment curAlign, based on the size and alignment of the
   * previous generated temporary.
   * @param curSize - The size of the new variable.
   * @param curAlign - The alignment of the new variable.
   * @return The offset of the new variable.
   */
  public int generateOffset(int curSize, int curAlign)
  {
    int curOffset = (prevOffset + prevSize + curAlign - 1) / curAlign * curAlign;
    
    prevSize = curSize;
    prevOffset = curOffset;
    
    return curOffset;
  }
}