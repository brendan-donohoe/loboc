package general;

/**
 * LabelFactory.java
 *
 * @version 1.0
 *
 * @author Brendan Donohoe
 * 
 * Factory class for generating unique labels of the form "L" + n, where n is
 * updated with the generation of each label.
 */

public class LabelFactory
{
  int labelNum;
  
  public LabelFactory()
  {
    labelNum = 0;
  }
  
  public String gen()
  {
    String lbl = "L" + labelNum;
    labelNum++;
    return lbl;
  }
}