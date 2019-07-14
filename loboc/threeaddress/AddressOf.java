package threeaddress;

import java.util.LinkedList;

import mips.Instruction;
import mips.LoadAddress;
import mips.LoadImm;

/**
 * AddressOf.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Class representing the addressing of a variable
 */
public class AddressOf extends Result
{
  private Var v;
  
  public AddressOf(Var v)
  {
    this.v = v;
  }
  
  public String toString()
  {
    return "&" + v;
  }
  
  public void getLoadInst(LinkedList<Instruction> ins, int reg)
  {
    ins.add(new LoadImm(v.offset, reg));
    ins.add(new LoadAddress(reg, reg, v.isTemp()));
  }
  
  public Var getVar()
  {
    return v;
  }
}