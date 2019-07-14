package threeaddress;

import java.util.LinkedList;

import mips.Instruction;
import mips.LoadImm;

/**
 * Imm.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Result representing a numerical constant.
 */
public class Imm extends Result
{
  private int bits;
  
  public static final Imm IMM_ZERO = new Imm(0);
  public static final Imm IMM_ONE = new Imm(1);
  
  public Imm(int bits)
  {
    this.bits = bits;
  }
  
  public int getBits()
  {
    return bits;
  }
  
  public String toString()
  {
    return Integer.toString(bits);
  }
  
  public boolean isImm()
  {
    return true;
  }
  
  public void getLoadInst(LinkedList<Instruction> ins, int reg)
  {
    ins.add(new LoadImm(bits, reg));
  }
  
  public Var getVar()
  {
    return null;
  }
}