/*
 * Citizen.java
 *
 * Instances of this class represent people who can take part in a poll
 * and have an opinion on who they might vote for
 *
 * You do not need to make any changes to this class.
 *
 * @author M362
 * Version 1.01 27 October 2009
 */

package tma01q4;

import java.util.Random;

public class Citizen
{
    static Random random = new Random();
    private int salary; // 0 .. 100000
    private Election e;
    
    public Citizen(Election e)
    {
        this.e = e;
        
        // initialise the salary randomly based on a normal distribution
        do
        {
            this.salary = (int) (46900 + random.nextGaussian() * 50000);
        } 
        while (this.salary < 0 || this.salary > Integer.MAX_VALUE / 2);
    }
    
    //return the id of the candidate to vote for
    //(could be 'undecided' = -1)
    public int voteFor()
    {
        int favourite = -1;
        float appeal = 0f;
        
        for (Candidate c: e.getCandidates())
        {
            float factor = salary - c.tax(salary)*salary;   
            
            if (factor > appeal) //this candidate seems more appealing
            {
                int id = c.getID();
                
                favourite = id;
                appeal = factor;
            }
        }
        return favourite;
    }    
}