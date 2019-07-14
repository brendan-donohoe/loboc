package optimization;

import java.util.BitSet;
import java.util.LinkedList;

import threeaddress.ThreeAddress;

/**
 * Optimizer.java
 * 
 * @author bddonohoe
 * 
 * Class which, provided with a list of three address code instructions,
 * can perform optimizations on the list and return an updated one.
 */
public class Optimizer
{
  private LinkedList<BasicBlock> blocks;
  private ThreeAddress [] instArr;
  private VarCounter ct;
  private DefMap dm;
  
  public Optimizer(LinkedList<ThreeAddress> addresses, int tempCount)
  {
    init(addresses, tempCount);
  }
  
  /**
   * Set up the optimizer - separate instructions into basic blocks, get a
   * count of the number of times temporaries are used in the code, and compute
   * the reaching definitions vectors for our instructions as well as the
   * definition vectors for each of our variables.
   * @param addresses - The list of three address code instructions.
   * @param tempCount - The number of temporaries in the code.
   */
  public void init(LinkedList<ThreeAddress> addresses, int tempCount)
  {
    // First, label all of our addresses with nonnegative integers, and store
    // them inside of an array.
    
	instArr = new ThreeAddress [addresses.size()];
    
    int idx = 0;
    
    for (ThreeAddress a : addresses)
    {
      a.setIdx(idx);
      instArr[idx++] = a;
    }
    
    // Next, create a DefMap object with size equal to the number of
    // instructions.
    
    dm = new DefMap(addresses.size());
    
    // Set up the map.
    
    for (ThreeAddress a : addresses)
    {
      a.setDefInfo(dm);
    }
    
    dm.computeMaskU();
    
    // Get the used counts of all variables for this set of instructions.
    
    ct = new VarCounter(tempCount);
    
    for (ThreeAddress a : addresses)
    {
      a.addUseCount(ct);
    }
    
    // Split the code into basic blocks.
    
    blocks = OptimizerUtils.getBlocks(addresses);
    
    // And compute the reachable definitions for each.
    
    for (BasicBlock b : blocks)
    {
      b.computeDefMask(new BitSet(addresses.size()), dm);
    }
  }
  
  /**
   * Perform optimization on the list of instructions, doing so until no more
   * optimizations can be found.
   */
  public void optimize()
  {
    int numOpts;
    
    do
    {
      numOpts = 0;
      
      for (BasicBlock b : blocks)
      {
        numOpts += b.optimize(ct, dm, instArr);
      }
    }
    while (numOpts > 0);
  }
  
  /**
   * Reassemble the list of instructions from the basic blocks and return said
   * list.
   * @return The list of three address code instructions.
   */
  public LinkedList<ThreeAddress> getAddresses()
  {
    LinkedList<ThreeAddress> addresses = new LinkedList<ThreeAddress>();
    
    for (BasicBlock b : blocks)
    {
      addresses.addAll(b.getBlkAdds());
    }
    
    return addresses;
  }
}