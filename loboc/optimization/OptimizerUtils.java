package optimization;

import java.util.LinkedList;

import threeaddress.ThreeAddress;

/**
 * OptimizerUtils.java
 * 
 * @version 1.0
 * 
 * @author bddonohoe
 *
 * Utility methods for the optimization process.
 */
public class OptimizerUtils
{
  /**
   * Partition the list of three address code instructions into basic blocks.
   * @param addresses - The list of three address code instructions to be
   * partitioned.
   * @return The list of basic blocks.
   */
  public static LinkedList<BasicBlock> getBlocks(
    LinkedList<ThreeAddress> addresses)
  {
    LinkedList<BasicBlock> blks = new LinkedList<BasicBlock>();
    LinkedList<ThreeAddress> blkAdds = new LinkedList<ThreeAddress>();
    
    for (ThreeAddress a : addresses)
    {
      if (a.isJump())
      {
        blkAdds.add(a);
        BasicBlock blk = new BasicBlock(blkAdds);
        blks.add(blk);
        blkAdds = new LinkedList<ThreeAddress>();
      }
      else if (a.isLabel())
      {
        if (!blkAdds.isEmpty())
        {
          BasicBlock blk = new BasicBlock(blkAdds);
          blks.add(blk);
          blkAdds = new LinkedList<ThreeAddress>();
        }
        blkAdds.add(a);
      }
      else
      {
        blkAdds.add(a);
      }
    }
    
    if (!blkAdds.isEmpty())
    {
      BasicBlock blk = new BasicBlock(blkAdds);
      blks.add(blk);
    }
    
    return blks;
  }
}