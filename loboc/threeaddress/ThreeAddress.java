package threeaddress;

import java.util.BitSet;
import java.util.LinkedList;

import optimization.DefMap;
import optimization.VarCounter;
import mips.Instruction;

/**
 * ThreeAddress.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Class representing a full single three address instruction.
 */
public abstract class ThreeAddress
{
  protected int idx;
  protected BitSet inMask;
  protected BitSet outMask;
  
  public int getIdx()
  {
    return idx;
  }
  
  public void setIdx(int idx)
  {
    this.idx = idx;
  }
  
  public BitSet getInMask()
  {
    return inMask;
  }
  
  public void setInMask(BitSet inMask)
  {
    this.inMask = inMask;
  }
  
  public BitSet getOutMask()
  {
    return outMask;
  }
  
  public void setOutMask(BitSet outMask)
  {
    this.outMask = outMask;
  }
  
  /**
   * Return whether or not this instruction represents a jump.
   * @return True if this is some sort of jump instruction - false otherwise.
   */
  public boolean isJump()
  {
    return false;
  }
  
  /**
   * Return whether or not this instruction represents a label.
   * @return True if this is a label instruction - false otherwise.
   */
  public boolean isLabel()
  {
    return false;
  }
  
  /**
   * Return the RHS of this instruction, if this instruction is a definition
   * and the RHS is either a constant or a regular variable.
   * @return The RHS of this instruction if the RHS is just a simple result, or
   * null, otherwise.
   */
  public Result getPropResult()
  {
    return null;
  }
  
  /**
   * If this instruction represents a definition, update DefMap with the
   * relevant information.
   * @param dm - The DefMap to be updated.
   */
  public void setDefInfo(DefMap dm)
  {
    Var def = getDef();
    
    if (def != null)
    {
      dm.setBit(def, idx);
    }
  }
  
  /**
   * Compute the reaching definition mask of this instruction.
   * @param inMask - The reaching definition mask of the previous instruction.
   * @param dm - The map of variable definition bit vectors.
   */
  public void computeOutMask(BitSet inMask, DefMap dm)
  {
    this.inMask = inMask;
    
    // First, get the mask of the definition variable.  If this is not a
    // definition, exit immediately, setting inMask to outMask.
    
    Var def = getDef();
    
    if (def == null)
    {
      outMask = (BitSet) inMask.clone();
      return;
    }
    
    outMask = dm.getBitSet(def);
    
    // Now, AND this mask with our inMask to get an identical mask with the
    // current reaching definitions of this variable.  Now, "gen" our new
    // definition by setting the bit corresponding to this definition.
    
    outMask.and(inMask);
    outMask.set(idx);
    
    // Now, we XOR this result with the inMask to get an identical mask, with
    // all bits set to 1 in both the first and second masks (i.e., the "killed"
    // definitions) set to 0, and the bits set to 0 in the first but 1 in the
    // second (the "generated" definition) set to 1.  This is our outMask.
    
    outMask.xor(inMask);
  }
  
  /**
   * Attempt to propagate one of the parts of the RHS of this instruction.
   * @param r - The RHS to attempt to propagate.
   * @param dm - The map of definition bit vectors.
   * @param instArr - The array of instructions.
   * @return The new result for the RHS passed in, or null if no such
   * propagation could be performed.
   */
  public Result propagateVar(Result r, DefMap dm, ThreeAddress [] instArr)
  {
    // Firstly, we need our RHS to be a variable.
    
    if (!r.isVar())
    {
      return null;
    }
    
    Var v = (Var) r;
    
    // We'll propagate the result from this variable's previous definition to
    // this one if the result is a constant (constant propagation) or another
    // variable (copy propagation).
    
    // First, get the definition corresponding to this variable, and attempt to
    // grab a result that can be propagated, if we have not computed it
    // previously.
    
    int defIdx = dm.getDef(v, inMask);
    
    if (defIdx == -1)
    {
      return null;
    }
    
    Result retResult = instArr[defIdx].getPropResult();
    
    if (retResult == null)
    {
      return null;
    }
    else if (retResult.isImm())
    {
      return retResult;
    }
    else
    {
      // We wish to perform copy propagation.  We have to be careful here - if
      // we have a series of statements, say, a = b; b = b + 1; c = a;
      // We cannot propagate from the first statement to the last statement,
      // since the variable we wish to propagate, b, is reassigned at 2.  Thus,
      // a and b no longer share the same value by 3.
      
      // To check for this, we do the following.  We first get the retResult
      // variable.
      
      Var rv = retResult.getVar();
      
      // Next, we get the inMasks at the location of the definition, as well as
      // at our current location, as well as the variable mask for rv.
      
      BitSet varMask = dm.getBitSet(rv);
      BitSet defInMask = instArr[defIdx].inMask;
      
      // We then AND them all together.  If there is one bit active by the end
      // of the process (that is, if the same definition of the propagated
      // variable is used at both locations) then we can carry out the
      // propagation.
      
      varMask.and(defInMask);
      varMask.and(inMask);
      
      if (!varMask.isEmpty())
      {
        return retResult;
      }
      else
      {
        // The variable does not use the same definition in both locations.
        
        return null;
      }
    }
  }
  
  /**
   * Get the instruction(s) corresponding to this three address operation and
   * insert them into the list of instructions.
   * @param ins - The list of instructions.
   */
  public abstract void getInst(LinkedList<Instruction> ins);
  
  /**
   * Get the variable on the LHS of this assignment, if this instruction
   * represents some sort of assignment.
   * @return The variable on the LHS of the assignment. 
   */
  public abstract Var getDef();
  
  /**
   * Add the number of times each variable has a use in the code to counter ct.
   * Here, a variable is used whenever it appears on the RHS of a binary or
   * unary operation, it appears, possibly with a dereference or address
   * operator on the RHS of an assignment operation, it appears in the
   * condition of a conditional goto, or it appears in a return.
   * @param ct - The counter tracking the uses of each variable.
   */
  public abstract void addUseCount(VarCounter ct);
  
  /**
   * Subtract the number of times each variable has a use in the code from
   * counter ct.  Here, a variable is used whenever it appears on the RHS of a
   * binary or unary operation, it appears, possibly with a dereference or
   * address operator on the RHS of an assignment operation, it appears in the
   * condition of a conditional goto, or it appears in a return.
   * @param ct - The counter tracking the uses of each variable.
   */
  public abstract void subUseCount(VarCounter ct);
  
  /**
   * Determine whether or not this three address code instruction is "dead".
   * For our purposes, a three address code statement is dead if it sets the
   * value of a variable (via either a normal assignment, a unary operation,
   * or a binary operation) which is never used elsewhere in the code.
   * @param ct - The counter containing the number of times each temporary sees
   * use in the code.
   * @return True if this instruction is dead.  False otherwise.
   */
  public abstract boolean isDeadCode(VarCounter ct);
  
  /**
   * Attempt to perform constant and user variable propagation on the given
   * three address code statement using the mappings available in the table.
   * Update ct as we eliminate uses of temporaries, and pt as we create new
   * mappings.
   * @param ct - The counter containing the number of times each temporary sees
   * use in the code.
   * @param dm - The map of variable definitions.
   * @param instArr - The array of instructions.
   * @return True if we have successfully carried out propagation in some form,
   * and false otherwise.
   */
  public abstract boolean propagate(VarCounter ct, DefMap dm,
    ThreeAddress [] instArr);
  
  /**
   * Attempt to perform constant-folding on the given three address code
   * statement.
   * @return The new three address code statement to be included in our code
   * (or null if the statement should instead be deleted).
   */
  public abstract ThreeAddress fold();
  
  /**
   * Attempt to apply algebraic identities to reduce three address code
   * instructions.
   * @param ct - The counter containing the number of times each temporary sees
   * use in the code.
   * @return The new three address code statement to be included in our code.
   */
  public abstract ThreeAddress algebraicIdentity(VarCounter ct);
}