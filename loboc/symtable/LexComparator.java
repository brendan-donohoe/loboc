package symtable;

import java.util.Comparator;
import java.util.Map.Entry;

/**
 * EntryComparator.java
 * 
 * @author Brendan Donohoe
 *
 * @version 1.0
 * 
 * Simple comparator class that orders entries of the symbol table by the
 * lexicographical ordering of their string keys.
 */
public class LexComparator implements Comparator<Entry<String, SymbolData>>
{
  /**
   * Create a static entry comparator so we don't need to instantiate a new
   * one each time we want to sort the keys in the symbol table. 
   */
  public static final LexComparator LEX_COMP = new LexComparator();
  
  public int compare(Entry<String, SymbolData> o1,
    Entry<String, SymbolData> o2)
  {
    return o1.getKey().compareTo(o2.getKey());
  }
}