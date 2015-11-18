/*
 * Newspaper.java
 *
 * Instances of this class represent newspapers that ask citizens
 * who they are voting for in an election. Newspaper polls can be run as threads.
 *
 * You will need to complete the constructors in this class
 * and write the code to use the latch and semaphore when appropriate.
 * No new instance data or methods are required.
 *
 * @author M362
 * Version 1.01 27 October 2009
 */

package tma01q4;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Newspaper extends Thread
{
   private int id;              // newspaper id
   private int pollSize;        // number of samples
   private int pollTimes;       // number of polls
   private Election e;          // the election
   private int sampledVotes[];  // the sampled citizen votes
   
   //One of these mechanisms is to be used to control displaying of
   //election results at an appropriate time 
   private CountDownLatch pollsFinishedLatch;   //part (a)
   private Semaphore pollsFinishedSema;         //part (c)
   
   //The lock is to be used in parts (b) and (c)
   private Lock publishLock;
   
   //provided constructor - no changes required.
   public Newspaper(Election e, int id, int pollSize, int pollTimes)
   {
      this.e = e;
      this.id = id;
      this.pollSize = pollSize;
      this.pollTimes = pollTimes;
      
      // only count the sampledVotes for the listed candidates in the election
      sampledVotes = new int [e.getNumberOfCandidates()];
   }
   
   //TODO (a) add a constructor to initialise the latch
   public Newspaper(Election e, int id, int pollSize, int pollTimes, CountDownLatch pollsFinishedLatch)
   {
      this.e = e;
      this.id = id;
      this.pollSize = pollSize;
      this.pollTimes = pollTimes;
      this.pollsFinishedLatch = pollsFinishedLatch;
      sampledVotes = new int [e.getNumberOfCandidates()];
   }        
   
   //TODO (b) add a constructor to initialise the latch and the lock
   public Newspaper(Election e, int id, int pollSize, int pollTimes, CountDownLatch pollsFinishedLatch, Lock publishLock)
   {
      this.e = e;
      this.id = id;
      this.pollSize = pollSize;
      this.pollTimes = pollTimes;
      this.pollsFinishedLatch = pollsFinishedLatch;
      this.publishLock = publishLock;
      sampledVotes = new int [e.getNumberOfCandidates()];
   }
   
   //TODO (c) add a constructor to initialise the semaphore and lock
   public Newspaper(Election e, int id, int pollSize, int pollTimes, Semaphore pollsFinishedSema, Lock publishLock)
   {
      this.e = e;
      this.id = id;
      this.pollSize = pollSize;
      this.pollTimes = pollTimes;
      this.pollsFinishedSema = pollsFinishedSema;
      this.publishLock = publishLock;
      sampledVotes = new int [e.getNumberOfCandidates()];
   }
   
   //Thread start point
   //TODO inform the election thread that polls from this newspaper are finished
   //using the latch in part (a)/(b) and semaphore in part (c)
   public void run()
   {
      while (pollTimes > 0)
      {
            //try
            //{
                //pollsFinishedSema.acquire();
                resetVotes();
                pollVotes();
                publishPollResults();
                pollTimes--;
                //pollsFinishedSema.release();
                pollsFinishedLatch.countDown();
            //} catch (InterruptedException ex) {
                //Logger.getLogger(Newspaper.class.getName()).log(Level.SEVERE, null, ex);
            //}
      }
   }
   
   // make sure all sampledVotes are reset to zero
   private void resetVotes()
   {
      // initialise the vote counts in the poll
      for (int i = 0; i < sampledVotes.length; i++)
         sampledVotes[i] = 0;
   }
   
   // sample the citizens voting choices
   private  void pollVotes()
   {
      for (int n = 0; n < pollSize; n++)
      {
         // pick a person from the collection of people
         Citizen c = e.pickRandomCitizen();
         if (c != null)
         {
            int candidate = c.voteFor();
            if (candidate >= 0)
               sampledVotes[candidate] ++;
         }
      }
   }
   
   //Display the newspaper poll results based on the sample
   //TODO for part (b)/(c) control access to this method to avoid
   //interleaving of newspaper poll results
   private void publishPollResults()
   {
      publishLock.lock();
      try
      {
      System.out.print("According to newspaper " + (this.id + 1) + ", the chances for candidates (");
      
      for (int i = 0; i < sampledVotes.length; i++)
         System.out.print( ( i==0? "" : ",") + (i+1));
      
      System.out.print(") to win this election is (");
      
      //simulates some activity
      try
      {
         this.sleep(1000);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      
      for (int i = 0; i < sampledVotes.length; i++)
         System.out.print( ( i==0? "" : "%,") + (100*sampledVotes[i]/pollSize) ); // percentage
      
      System.out.println("%).");
      }
      finally
      {
          publishLock.unlock();
      }
   }

}