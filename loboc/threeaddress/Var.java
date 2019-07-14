package threeaddress;

import java.util.LinkedList;

import mips.Instruction;
import mips.LoadMem;
import mips.LoadMem.LoadType;
import mips.StoreMem;
import mips.StoreMem.StoreType;

/**
 * Var.java
 *
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Class representing a variable.
 */
public abstract class Var extends Result
{
  /**
   * The id of this variable.
   */
  protected int varNum;
  
  /**
   * The size of the variable, in bytes.
   */
  protected int size;
  
  /**
   * The alignment of the variable, in bytes.
   */
  protected int align;
  
  /**
   * The offset of the variable in memory.
   */
  protected int offset;
  
  public int getVarNum()
  {
    return varNum;
  }
  
  public int getSize()
  {
    return size;
  }
  
  public int getAlign()
  {
    return align;
  }
  
  public boolean isVar()
  {
    return true;
  }
  
  /**
   * Return whether or not this variable is a compiler-generated temporary.
   * @return True if this is a temporary variable, false if it's user-defined.
   */
  public boolean isTemp()
  {
    return false;
  }
  
  /**
   * Generate the instruction to load this variable into the specified
   * register from memory.
   * @param ins - The list of instructions.
   * @param reg - The register into which this variable will be inserted.
   */
  public void getLoadInst(LinkedList<Instruction> ins, int reg)
  {
    LoadType lt;
    
    if (size == 1)
    {
      lt = LoadType.BYTE;
    }
    else
    {
      lt = LoadType.WORD;
    }
    
    ins.add(new LoadMem(offset, lt, reg, isTemp()));
  }
  
  /**
   * Generate the instruction to store this variable into memory from the
   * specified register.
   * @param ins - The list of instructions.
   * @param reg - The register that holds this variable.
   */
  public void getStoreInst(LinkedList<Instruction> ins, int reg)
  {
    StoreType st;
    
    if (size == 1)
    {
      st = StoreType.BYTE;
    }
    else
    {
      st = StoreType.WORD;
    }

    ins.add(new StoreMem(offset, st, reg, isTemp()));
  }
  
  public Var getVar()
  {
    return this;
  }
  
  public boolean equals(Var other)
  {
    return isTemp() == other.isTemp() && varNum == other.getVarNum();
  }
}