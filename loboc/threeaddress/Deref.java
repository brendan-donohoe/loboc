package threeaddress;

import java.util.LinkedList;

import mips.Instruction;
import mips.LoadDeref;
import mips.LoadMem.LoadType;

/**
 * Deref.java
 * 
 * @version 1.0
 * 
 * @author bddonohoe
 *
 * Result representing the dereference of a variable (which, clearly, is
 * assumed to hold a legal address beforehand).
 */
public class Deref extends Result
{
  private Var t;
  private int ptrSize;
  
  public Deref(Var t, int ptrSize)
  {
    this.t = t;
    this.ptrSize = ptrSize;
  }
  
  public String toString()
  {
    return t + "[]";
  }
  
  public void getLoadInst(LinkedList<Instruction> ins, int reg)
  {
    // First, we need to get the variable from memory.
    
    t.getLoadInst(ins, reg);
    
    // Next, we simply dereference it to get the value it points to.
    
    LoadType lt = ptrSize == 1 ? LoadType.BYTE : LoadType.WORD;
    
    ins.add(new LoadDeref(reg, reg, lt));
  }
  
  public Var getVar()
  {
    return t;
  }
}