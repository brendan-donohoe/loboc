package threeaddress;

import java.util.LinkedList;

import optimization.DefMap;
import optimization.VarCounter;
import mips.Exit;
import mips.ExitVal;
import mips.Instruction;

/**
 * Return.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Class representing return from the program. 
 */
public class Return extends ThreeAddress
{
  private Result r;
  
  public Return(Result r)
  {
    this.r = r;
  }
  
  public boolean isJump()
  {
    return true;
  }
  
  public String toString()
  {
    return idx + ": return " + r + "; DEFS: " + inMask;
  }
  
  public void getInst(LinkedList<Instruction> ins)
  {
    int reg = 0;
    
    // Load the variable into a register.
    
    r.getLoadInst(ins, reg);
    
    // Set the exit value to the value of the variable.
    
    ins.add(new ExitVal(reg));
    
    // And exit the program.
    
    ins.add(new Exit());
  }
  
  public Var getDef()
  {
    return null;
  }
  
  public void addUseCount(VarCounter ct)
  {
    ct.incr(r.getVar());
  }
  
  public void subUseCount(VarCounter ct)
  {
    ct.decr(r.getVar());
  }
  
  public boolean isDeadCode(VarCounter ct)
  {
    return false;
  }
  
  public boolean propagate(VarCounter ct, DefMap dm, ThreeAddress [] instArr)
  {
    Result p = propagateVar(r, dm, instArr);
    
    if (p == null)
    {
      return false;
    }
    
    ct.decr(r.getVar());
    ct.incr(p.getVar());
    
    r = p;
    
    return true;
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