package threeaddress;

import java.util.BitSet;
import java.util.LinkedList;

import optimization.DefMap;
import optimization.VarCounter;
import mips.Instruction;
import mips.StoreDeref;
import mips.StoreMem.StoreType;

/**
 * DerefAndAssign.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Class representing the assignment of some RHS to a dereference of an LHS
 * (for instance, when assigning values to arrays or pointers).
 */
public class DerefAndAssign extends ThreeAddress
{
  private Var ptr;
  private Result r;
  private int ptrSize;
  
  public DerefAndAssign(Var ptr, Result r, int ptrSize)
  {
    this.ptr = ptr;
    this.r = r;
    this.ptrSize = ptrSize;
  }
  
  public String toString()
  {
    return idx + ": " + ptr + "[] = " + r + "; DEFS: " + inMask;
  }
  
  public void getInst(LinkedList<Instruction> ins)
  {
    int regMem = 0;
    int regVal = 1;
    
    // Load the pointer and the value from memory...
    
    ptr.getLoadInst(ins, regMem);
    r.getLoadInst(ins, regVal);
    
    // And store it at the location provided in the memory register.
    
    StoreType st = ptrSize == 1 ? StoreType.BYTE : StoreType.WORD;
    
    ins.add(new StoreDeref(regVal, regMem, st));
  }
  
  public Var getDef()
  {
    return null;
  }
  
  public void computeOutMask(BitSet inMask, DefMap dm)
  {
    // Without knowing what we're pointing to, we have no choice but to kill
    // off all of our definitions of user-defined variables.  Note that, since
    // temporaries can never be addressed, we can keep our definitions for our
    // temporaries.
    
    // We get rid of our user-defined definitions by taking a copy of the
    // userMask computed beforehand in our definition map, ANDing it with the
    // inMask to give us all of our definitions of user-defined variables,
    // and then XORing with the inMask once again.
    
    this.inMask = inMask;  
    outMask = dm.getMaskU();
    outMask.and(inMask);
    outMask.xor(inMask);
  }
  
  public void addUseCount(VarCounter ct)
  {
    ct.incr(ptr);
    ct.incr(r.getVar());
  }
  
  public void subUseCount(VarCounter ct)
  {
    ct.decr(ptr);
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