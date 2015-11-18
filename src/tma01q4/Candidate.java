/*
 * Candidate.java
 *
 * Instances of this class represent candidates standing for election
 * A candidate can have one of three taxation policies
 *
 * You do need need to make any changes to this class.
 *
 * @author M362
 * Version 1.01 27 October 2009
 */

package tma01q4;

public class Candidate
{    
    private int id;         // unique identifier: 1 .. n
    private int taxPolicy;  // -1: progressive, 0: proportionate, 1: regressive
    
    public Candidate(int id, int taxPolicy)
    {
        this.taxPolicy = taxPolicy;
        this.id = id;
    }    
    
    public int getID()
    {
        return id - 1;
    }
    
    //This method is just to allow
    //citizens to assess candidates taxation policies.
    //Don't worry about the details
    float tax(int salary)
   {
      // -1: progressive (favouring poor)
      if (taxPolicy == -1)
         return ((float) salary / 10000f * 0.1f * 0.5f);
      
      //0: proportionate
      if (taxPolicy == 0)
         return  0.20f;
      
      // 1: regressive  (favouring rich)
      if (taxPolicy == 1)
         return  (1 - ((float) salary / 10000f * 0.1f)) * 0.5f ;
      
      return 1f;
   }
    
}