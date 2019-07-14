package threeaddress;

import java.util.LinkedList;

import optimization.DefMap;
import optimization.VarCounter;
import mips.Instruction;
import mips.Jump;

/**
 * Goto.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Class representing an unconditional jump to another branch.
 */
public class Goto extends ThreeAddress
{
  private String label;
  
  public Goto(String label)
  {
    this.label = label;
  }
  
  public boolean isJump()
  {
    return true;
  }
  
  public String toString()
  {
    return idx + ": goto " + label + "; DEFS: " + inMask;
  }
  
  public void getInst(LinkedList<Instruction> ins)
  {
    ins.add(new Jump(label));
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