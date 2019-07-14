package smt;

import defs.Def;
import defs.IdDef;
import error.SemanticError;
import general.Consts;
import general.GeneralUtils;
import general.LabelFactory;
import general.RefInt;
import general.TempFactory;
import threeaddress.Assign;
import threeaddress.Imm;
import threeaddress.Return;
import threeaddress.ThreeAddress;
import threeaddress.Var;
import type.Type;

import java.util.LinkedList;

import lexing.Token;
import symtable.SymbolData;
import symtable.SymbolTable;

/**
 * BlockSmt.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Class holding information for a single block statement, or a single closure.
 * This includes the definitions at the beginning of the block, the statements
 * within the block, and the symbol table holding the variables within the
 * scope of the block.
 */
public class BlockSmt extends Smt
{
  /**
   * Definitions made within the block.
   */
  private LinkedList<Def> defs;
  
  /**
   * Statements within the block - to be used in printing the syntax tree
   * report.
   */
  private LinkedList<Smt> smts;
  
  /**
   * The symbol table holding information about all of the variables of this
   * block's scope.  Used to print out the VSR.
   */
  private SymbolTable tb;
  
  public BlockSmt(LinkedList<Def> defs, LinkedList<Smt> smts, Token startToken)
  {
    super(startToken);
    this.defs = defs;
    this.smts = smts;
    this.tb = null;
  }
  
  public BlockSmt(LinkedList<Def> defs, LinkedList<Smt> smts, SymbolTable tb,
    Token startToken)
  {
    super(startToken);
    this.defs = defs;
    this.smts = smts;
    this.tb = tb;
  }
  
  public String getBOFPIF(int offset)
  {
    // Note that the top level block is assumed to have an offset of -1.
    
    StringBuilder output = new StringBuilder();
    
    String padding;
    
    if (offset <= 0)
    {
      padding = "";
    }
    else
    {
      padding = GeneralUtils.getPadding(offset);
    }
    
    // Print out curly brackets if we are not at the top level.
    
    if (offset != -1)
    {
      output.append(padding).append("{\n");
    }
    
    // Print the VSR corresponding to the table.
    
    if (tb != null && !tb.isEmpty())
    {
      output.append(tb.getVSR(offset + 1)).append('\n');
    }
    
    // Print out each of our statements (with the appropriate offset).
    
    for (Smt s : smts)
    {
      output.append(s.getBOFPIF(offset + 1)).append('\n');
    }
    
    // And print the corresponding close bracket if we are not at the top
    // level.
    
    if (offset != -1)
    {
      output.append(padding).append("}");
    }
    
    return output.toString();
  }
  
  public boolean isBlock()
  {
    return true;
  }
  
  public void labelExprs(LinkedList<SemanticError> errors)
  {
    // Label each of our statements with types.
    
    for (Smt s : smts)
    {
      s.labelExprs(errors);
    }
  }
  
  public Smt fold()
  {
    // Just fold over all of our contained statements.
    
    LinkedList<Smt> foldedSmts = new LinkedList<Smt>();  
    
    for (Smt s : smts)
    {
      Smt foldS = s.fold();
      
      if (foldS != null)
      {
        foldedSmts.add(foldS);
      }
    }
    
    smts = foldedSmts;
    return this;
  }
  
  public void getOffsetsAndLabel(int prevOffset, int prevSize, RefInt curId)
  {
    // Label the offsets for each of our variables in the symbol table.  To get
    // the variables in the order they were declared, we will iterate through
    // our definitions, which we just so happened to conveniently save.
    
    int pOffset = prevOffset;
    int pSize = prevSize;
    
    for (Def d : defs)
    {
      // For each definition, get the list of variables declared...

      IdDef idDef = (IdDef) d;
      LinkedList<String> names = idDef.getNames();
      
      Type type = idDef.getType();
      int curAlign = type.getAlignment();
      int curSize = type.getSize();
      
      for (String name : names)
      {
        // ...and compute the offset for each variable.
        
        SymbolData data = tb.get(name);
        
        int curOffset = (pOffset + pSize + curAlign - 1) / curAlign * curAlign;
        
        data.setId(curId.incr());
        data.setOffset(curOffset);
        
        pOffset = curOffset;
        pSize = curSize;
      }
    }
    
    for (Smt s : smts)
    {
      s.getOffsetsAndLabel(pOffset, pSize, curId);
    }
  }
  
  public void setLastSmts()
  {
    // Only the last statement in this block can be a "last statement" if the
    // input is non-empty.  If the input is empty, we consider this a last
    // statement, and will return 0.
    
    if (!smts.isEmpty())
    {
      smts.getLast().setLastSmts();
    }
    else
    {
      lastSmt = true;
    }
  }
  
  public void threeAddress(TempFactory tFac, LabelFactory lFac,
    LinkedList<ThreeAddress> addresses)
  {
    // Generate the three address code for the statements contained
    // within this scope.  If this block was the last statement, we will also
    // return 0.
    
    for (Smt s : smts)
    {
      s.threeAddress(tFac, lFac, addresses);
    }
    
    if (lastSmt)
    {
      Var v = tFac.gen(Consts.INT_SIZE, Consts.INT_ALIGN);
      addresses.add(new Assign(v, Imm.IMM_ZERO));
      addresses.add(new Return(v));
    }
  }
}