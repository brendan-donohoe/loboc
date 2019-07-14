package threeaddress;

import java.util.LinkedList;

import optimization.DefMap;
import optimization.VarCounter;
import mips.Instruction;
import mips.LabelInst;

/**
 * Label.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 * 
 * Class representing a label, or the destination for a goto instruction.
 */
public class Label extends ThreeAddress
{
  private String label;
  
  public Label(String label)
  {
    this.label = label;
  }
  
  public boolean isLabel()
  {
    return true;
  }
  
  public String toString()
  {
    return idx + ": label " + label + ": DEFS: " + inMask;
  }
  
  public void getInst(LinkedList<Instruction> ins)
  {
    ins.add(new LabelInst(label));
  }
  
  public Var getDef()
  {
    return null;
  }
  
  public void addUseCount(VarCounter ct)
  {
    // No variables to count the uses of!
  }
  
  public void subUseCount(VarCounter ct)
  {
    // Again, no variables to count the uses of!
  }
  
  public boolean isDeadCode(VarCounter ct)
  {
    return false;
  }
  
  public boolean propagate(VarCounter ct, DefMap dm, ThreeAddress [] instArr)
  {
    return false;
  }
  
  public ThreeAddress fold()
  {
    return this;
  }
  
  public ThreeAddress algebraicIdentity(VarCounter ct)
  {
    return this;
  }
}