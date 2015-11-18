/*
 * Election.java
 *
 * An instance of this class simulates an election with three candidates.
 * Three newspapers conduct polls to find out who the people might vote for.
 * The citizens also cast their votes while the polls are going on.
 *
 * You will need to complete some methods in this class but you will not
 * need to add any instance data or constructors.
 *
 * @author M362
 * Version 1.01 27 October 2009, revised 2 January 2013
 */

package tma01q4;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Election
{

    private List<Citizen> people;

    private List<Candidate> candidates;

    private List<Newspaper> newspapers;

    // votes of the election
    private int votes[];

    //TODO initialise as necessary
    private static final CountDownLatch newspapersLatch = new CountDownLatch(9);

    private Lock pickCitizenLock = new ReentrantLock();

    private Semaphore newspaperSema = new Semaphore(1);

    //provided constructor
    public Election(int population)
    {
        people = new LinkedList<Citizen>();
        candidates = new LinkedList<Candidate>();
        newspapers = new LinkedList<Newspaper>();

        // initialise the election with the population
        for (int i = 0; i < population; i++)
        {
            Citizen c = new Citizen(this);
            people.add(c);
        }

        // create candidates with different policies
        candidates.add(new Candidate(1, 0));
        candidates.add(new Candidate(2, -1));
        candidates.add(new Candidate(3, 1));
        votes = new int[getNumberOfCandidates()];
    }

    public List<Candidate> getCandidates()
    {
        return candidates;
    }

    //This method demonstrates the problems that arise with the threads
    //Do not alter this method.
    public void startNewspaperPolls()
    {
        newspapers.add(new Newspaper(this, 0, 100, 3));
        newspapers.add(new Newspaper(this, 1, 100, 3));
        newspapers.add(new Newspaper(this, 2, 100, 3));

        // start the polls
        for (Newspaper m : newspapers)
        {
            m.start();
        }
    }

    //TODO complete this method
    //Note that newspaper output may still be interleaved because the
    //Newspaper publishResults method is only to be fixed in the next part
    public void startNewspaperPollsWithLatch() throws InterruptedException
    {
        newspapers.add(new Newspaper(this, 0, 100, 3, newspapersLatch));
        newspapers.add(new Newspaper(this, 1, 100, 3, newspapersLatch));
        newspapers.add(new Newspaper(this, 2, 100, 3, newspapersLatch));
        for (Newspaper m : newspapers)
        {
            m.start();
        }
        newspapersLatch.await();
    }

    //TODO complete this method
    public void startNewspaperPollsWithLatchAndLock() throws InterruptedException
    {
        newspapers.add(new Newspaper(this, 0, 100, 3, newspapersLatch, pickCitizenLock));
        newspapers.add(new Newspaper(this, 1, 100, 3, newspapersLatch, pickCitizenLock));
        newspapers.add(new Newspaper(this, 2, 100, 3, newspapersLatch, pickCitizenLock));
        for (Newspaper m : newspapers)
        {
            m.start();
        }
        newspapersLatch.await();

    }

    //TODO complete this method
    public void startNewspaperPollsWithSemaphoreAndLock() throws InterruptedException
    {
        newspapers.add(new Newspaper(this, 0, 100, 3, newspaperSema, pickCitizenLock));
        newspapers.add(new Newspaper(this, 1, 100, 3, newspaperSema, pickCitizenLock));
        newspapers.add(new Newspaper(this, 2, 100, 3, newspaperSema, pickCitizenLock));
        for (Newspaper m : newspapers)
        {
            m.start();
        }
        //newspapersLatch.await();
    }

    //TODO in this method you need to prevent multiple threads from
    //attempting to modify the people collection at the same time
    //Use a reentrant lock
    //Picks a citizen randomly (each citizen should only be chosen once)
    public Citizen pickRandomCitizen()
    {
        pickCitizenLock.lock();
        try
        {
        Citizen randomCitizen;

        // first get a random index
        int index = (int) (Math.random() * getPopulationSize());

        randomCitizen = people.remove(index); //returns the removed element

        return randomCitizen;
        }
        finally
        {
            pickCitizenLock.unlock();
        }
    }

    // counting the votes
    public void castVotes()
    {
        for (int h = 0; h < getPopulationSize(); h++)
        {
            Citizen c = people.get(h);
            int i = c.voteFor();

            if (i >= 0)
            {
                votes[i]++;
            }
        }
    }

    // tell the size of population
    int getPopulationSize()
    {
        return people.size();
    }

    // tell the number of candidates
    int getNumberOfCandidates()
    {
        return candidates.size();
    }

    // display the actual election results as percentages of total votes cast
    public void revealResults()
    {
        System.out.println
        ("The election results for the respective candidates are as follows.");
        int candidates = votes.length;
        int population = getPopulationSize();
        String results = "(";
        for (int i = 0; i < candidates; i++)
        {
            String separator = (i < candidates - 1)? ", " : "";
            results = results + (i + 1) + ":"
            + (100 * votes[i]/population) + "%" + separator;
        }
        results = results + ")";
        System.out.println(results);
    }

    //TODO You should use ONE of the 'startNewspaperPolls' methods at a time
    public static void main(String[] args) throws InterruptedException
    {
        // initialise election among the people
        Election e = new Election(50000);

        //This method is only for initial demonstration
        //of the problems that arise with the threaded code
        //comment it out when you start working on the other parts
        //e.startNewspaperPolls();

        //TODO for part (a), complete and use this method
        //e.startNewspaperPollsWithLatch();

        //TODO for part (b), complete and use this method
        //Also make sure that access to pickRandomCitizen is controlled
        e.startNewspaperPollsWithLatchAndLock();

        //TODO for part (c), complete and use this method
        //pickRandomCitizen is unchanged
        //e.startNewspaperPollsWithSemaphoreAndLock();

        e.castVotes();

        // reveal the results of election
        e.revealResults();
    }

}
