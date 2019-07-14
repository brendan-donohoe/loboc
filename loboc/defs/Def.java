package defs;

/**
 * Def.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Common class for all def nodes.
 */

public abstract class Def
{
  /**
   * Determine whether or not this def represents an error.
   * @return True if this is an error def instance - false otherwise.
   */
  public abstract boolean isError();
}