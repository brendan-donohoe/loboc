package symtable;

import general.GeneralUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;

/**
 * SymbolTable.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Class containing the mappings between symbols and relevant data.  Basically
 * a glorified wrapper for a hash map.
 */
public class SymbolTable
{
  /**
   * The main table where mappings are stored.
   */
  private HashMap<String, SymbolData> table;
  
  /**
   * A reference to the table in the parent scope of this one - null if this is
   * the symbol table for the topmost scope.
   */
  private SymbolTable parent;
  
  public SymbolTable()
  {
    this.table = new HashMap<String, SymbolData>();
    this.parent = null;
  }
  
  public SymbolTable(SymbolTable parent)
  {
    this.table = new HashMap<String, SymbolData>();
    this.parent = parent;
  }
  
  /**
   * Return true if this table contains at least one mapping, and false
   * otherwise.
   * @return A boolean value denoting whether this table contains any mappings.
   */
  public boolean isEmpty()
  {
    return table.isEmpty();
  }
  
  /**
   * Given a key, return the SymbolData object mapped to this key in the table.
   * @param key - The symbol used to index into the table.
   * @return The SymbolData object corresponding to this symbol in the table,
   * or null if no such SymbolData object could be found.
   */
  public SymbolData get(String key)
  {
    return table.get(key);
  }
  
  /**
   * Given a key, return the SymbolData object mapped to this key in either
   * this table, or in the parent scope most local to this one.
   * @param key - The symbol used to index into the table.
   * @return The SymbolData object corresponding to this symbol in the table or
   * one of the parent tables, or null if no such SymbolData object could be
   * found in any parent table.
   */
  public SymbolData deepGet(String key)
  {
    SymbolData result = table.get(key);
    
    if (result != null)
    {
      return result;
    }
    
    if (parent == null)
    {
      return null;
    }
    
    return parent.deepGet(key);
  }
  
  /**
   * Add the given symbol to the symbol table with the given SymbolData.
   * @param key - The symbol used to index into the table.
   * @param value - The SymbolValue corresponding to this key.
   * @return The previous mapping to this key (or null if there was no such
   * mapping).
   */
  public SymbolData put(String key, SymbolData value)
  {
    return table.put(key, value);
  }
  
  /**
   * Get a list all mappings in the table.
   * @return All mappings in this table, as a set.
   */
  public Set<Entry<String, SymbolData>> entrySet()
  {
    return table.entrySet();
  }
  
  /**
   * Return the variable summary report corresponding to this table, as a
   * string.
   * @param offset - The amount of padding (sequences of two spaces) to be
   * appended to the beginning of each line of the VSR (always 0 for Spike 3,
   * but may be of more use for later spikes).
   * @return The variable summary report.
   */
  public String getVSR(int offset)
  {
    // First, generate our string of padding (not really necessary for spike 3,
    // as such a report will only come from the block at the topmost level, but
    // seems like it may be relevant for future spikes).
    
    String padding = GeneralUtils.getPadding(offset);
    
    // Next, we sort the entries of the map by name.
    
    LinkedList<Entry<String, SymbolData>> sortedSyms =
      new LinkedList<Entry<String, SymbolData>>(table.entrySet());
    
    Collections.sort(sortedSyms, LexComparator.LEX_COMP);
    
    // Now that we have the symbols sorted by name, we're free to print out the
    // entries of the map in Spike 3 VDI format.
    
    StringBuilder output = new StringBuilder();
    
    for (Entry<String, SymbolData> e : sortedSyms)
    {
      output.append(padding + e.getKey() + " " + e.getValue().toString() + '\n');
    }
    
    return output.toString();
  }
}