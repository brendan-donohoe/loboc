package optimization;

import java.util.BitSet;
import java.util.HashMap;

import threeaddress.Var;

/**
 * DefMap.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Basically our second symbol table for the optimization process.  This
 * includes a bit vector for each variable denoting the locations at which each
 * variable is defined, which are stored in separate arrays for user-defined
 * and temporary variables.
 */
public class DefMap
{
  private int setSize;
  private HashMap<Integer, BitSet> defMapU;
  private HashMap<Integer, BitSet> defMapT;
  
  /**
   * Mask of definitions for our user-defined variables only.  Can use to
   * filter either user variable definitions (by ANDing) or temp variable
   * definitions (by XORing).
   */
  private BitSet maskU;
  
  public DefMap(int setSize)
  {
    this.setSize = setSize;
    
    defMapU = new HashMap<Integer, BitSet>();
    defMapT = new HashMap<Integer, BitSet>();
  }
  
  public BitSet getMaskU()
  {
    return (BitSet) maskU.clone();
  }
  
  public BitSet getBitSet(Var v)
  {
    int varNum = v.getVarNum();
    
    if (v.isTemp())
    {
      BitSet bits = defMapT.get(varNum);
      
      if (bits == null)
      {
        bits = new BitSet(setSize);
        defMapT.put(varNum, bits);
      }
      
      return (BitSet) bits.clone();
    }
    else
    {
      BitSet bits = defMapU.get(varNum);
      
      if (bits == null)
      {
        bits = new BitSet(setSize);
        defMapU.put(varNum, bits);
      }
      
      return (BitSet) bits.clone();
    }
  }
  
  /**
   * Set the bit for the relevant vector and relevant variable.
   * @param v - The variable whose bit vector is to have its bit set.
   * @param bit - The position of the bit to be set.
   */
  public void setBit(Var v, int bit)
  {
    int varNum = v.getVarNum();
    BitSet bits;
    
    if (v.isTemp())
    {
      bits = defMapT.get(varNum);
      
      if (bits == null)
      {
        bits = new BitSet(setSize);
        defMapT.put(varNum, bits);
      }
    }
    else
    {
      bits = defMapU.get(varNum);
      
      if (bits == null)
      {
        bits = new BitSet(setSize);
        defMapU.put(varNum, bits);
      }
    }
    
    bits.set(bit);
  }
  
  /**
   * Get the position of the reaching definition of this variable, provided
   * with the instruction's reaching definition mask.
   * @param v - The variable whose definition is to be computed.
   * @param in - The mask of reaching definitions to the RHS of the instruction
   * that called this method.
   * @return The position of the reaching definition.
   */
  public int getDef(Var v, BitSet in)
  {
    BitSet defns;
    
    if (v.isTemp())
    {
      defns = defMapT.get(v.getVarNum());
    }
    else
    {
      defns = defMapU.get(v.getVarNum());
    }
    
    if (defns == null)
    {
      return -1;
    }
    
    defns = (BitSet) defns.clone();
    
    defns.and(in);
    
    return defns.nextSetBit(0);
  }
  
  /**
   * Compute the user-variable mask (i.e., just OR all of the masks for the
   * user-defined variable.
   */
  public void computeMaskU()
  {
    maskU = new BitSet(setSize);
    
    for (BitSet m : defMapU.values())
    {
      maskU.or(m);
    }
  }
  
  public String toString()
  {
    return "For user defs: " + defMapU + ";\n\nFor temp defs: " + defMapT;
  }
}