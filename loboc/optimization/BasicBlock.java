package optimization;

import java.util.BitSet;
import java.util.LinkedList;
import java.util.ListIterator;

import threeaddress.ThreeAddress;

/**
 * BasicBlock.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Utility class for a basic block structure - defined to be a contiguous
 * sequence of three address codes such that the block can only be entered at
 * the first instruction, the block can only be exited at the last instruction,
 * and we cannot perform a jump in the middle.
 */
public class BasicBlock
{
  private LinkedList<ThreeAddress> blkAdds;
  
  public BasicBlock(LinkedList<ThreeAddress> blkAdds)
  {
    this.blkAdds = blkAdds;
  }
  
  public LinkedList<ThreeAddress> getBlkAdds()
  {
    return blkAdds;
  }
  
  /**
   * Compute the reaching definition bit vector mask for each of our
   * instructions, starting from the bit vector prev.
   * @param prev - Our bit vector of reaching definitions, acts as our starting
   * point (since we perform only local optimization, not global optimization,
   * prev is always a bit vector of 0s).
   * @param dm - Our map of reaching definitions of each variable.
   */
  public void computeDefMask(BitSet prev, DefMap dm)
  {
    BitSet cur = prev;
    
    for (ThreeAddress a : blkAdds)
    {
      a.computeOutMask(cur, dm);
      cur = a.getOutMask();
    }
  }
  
  /**
   * Perform a single round of optimization on this block, counting the total
   * number of optimizations performed along the way.
   * @param ct - A count of the number of times each variable is used in the
   * code.
   * @param dm - The table of reaching definitions for each variable.
   * @param instArr - The array of instructions.
   * @return The number of optimizations performed.
   */
  public int optimize(VarCounter ct, DefMap dm, ThreeAddress [] instArr)
  {
    int numOpts = 0;
    
    ListIterator<ThreeAddress> it = blkAdds.listIterator();
    
    while (it.hasNext())
    {
      ThreeAddress a = it.next();
      
      if (a.isDeadCode(ct))
      {
        // Delete the instruction from our block.
        
        numOpts++;
        a.subUseCount(ct);
        it.remove();
      }
      else if (a.propagate(ct, dm, instArr))
      {
        numOpts++;
      }
      
      // Attempt more radical transformations, which may give us another
      // instruction (or even remove this instruction) entirely.  If they do,
      // we'll perform any necessary updates to both the list and the
      // instruction array.
      
      // Attempt to fold the statement.
      
      ThreeAddress fa = a.fold();
      
      if (a != fa)
      {
        // Folding was successful - we either have a new statement, or we've
        // gotten rid of this statement entirely.
        
        numOpts++;
        
        if (fa != null)
        {
          it.set(fa);
          
          instArr[a.getIdx()] = fa;
          fa.setIdx(a.getIdx());
          fa.setInMask(a.getInMask());
          fa.setOutMask(a.getOutMask());
        }
        else
        {
          instArr[a.getIdx()] = null;
          it.remove();
        }
      }
      else
      {
        // Try to apply an algebraic identity instead.
        
        ThreeAddress alg = a.algebraicIdentity(ct);
          
        if (a != alg)
        {
          numOpts++;
          it.set(alg);
          
          instArr[a.getIdx()] = alg;
          alg.setIdx(a.getIdx());
          alg.setInMask(a.getInMask());
          alg.setOutMask(a.getOutMask());
        }
      }
    }
    
    return numOpts;
  }
  
  public String toString()
  {
    String s = "BASIC BLOCK:\n";
    for (ThreeAddress a : blkAdds)
    {
      s += a + "\n";
    }
    return s;
  }
}