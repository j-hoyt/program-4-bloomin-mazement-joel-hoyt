package ca.unbsj.cs2383;

import org.junit.jupiter.api.*;
import static  org.junit.jupiter.api.Assertions.*;
import static java.time.Duration.ofMillis;

import java.util.HashSet;
import java.util.Random;


public class Prog4Test
{
  @Test
  public void ErrorRateTest()
  {
      int n = (int) (0.485 * 40 * 40);
      int m = (int) Math.ceil(-2 * n / -0.105361);
      BloomFilter bitsyBloom = new BloomFilter(m);
      int falsePositives = 0;
      int successfulRuns = 0;

      for (int j = 0; j < 10; j++)
      {
         // n insertions
         for (int i = 0; i < n; i++)
         {
           bitsyBloom.removeRightWall(i, 6);
         }

         falsePositives = 0;
         // checking n different coordinates, none of which were added
         for (int i = 0; i < n; i++)
         {
            if (!bitsyBloom.hasRight(i + 40, 46 ))
               falsePositives++;
         }
         if (falsePositives > 0.005 * n && falsePositives < 0.02 * n)
            successfulRuns++;
      }
      System.out.println("Test 1: False positive rates between 0.5% and 2%:");
      System.out.println("\n\n" + successfulRuns + "/10 successful runs.\n\n False Positive Rate on last run: " + (double) falsePositives/n + "%\n\n");
      assertTrue(successfulRuns >= 5, successfulRuns + " runs with false positive rate between 0.5% and 2%\t" + (double) falsePositives/n);
  }

  @Test
  //checking for false negatives, ie, it says it has a wall (the wall is NOT
  // in the set of removed walls), when it doesn't have a wall.
  public void NoMeansNoRight()
  {
    int n = (int) (0.49 * 40 * 40);
    int m = (int) Math.ceil(-2 * n / -0.105361);
    System.out.println("\n\n" + n + "\n\n" + m);
    BloomFilter bitsyBloom = new BloomFilter(m);
    HashSet<String> hashy = new HashSet<String>();
    int falseNegatives = 0;

     // n insertions
     for (int i = 0; i < 0.49 * 40; i++)
       for (int j = 0; j < 40; j++)
          bitsyBloom.removeRightWall(i, j);


     // checking n different coordinates, none of which were added
     for (int i = 0; i < 0.49 * 40; i++)
     {
       for (int j = 0; j < 40; j++)
       {
          if (bitsyBloom.hasRight(i, j))
            falseNegatives++;
        }
      }

     //assertTrue(true);
     System.out.println("\n\nTest 2 - NoMeansNo.\n"+n+" insertions\n"+falseNegatives+" false negatives\n\n");
     assertTrue(falseNegatives == 0, falseNegatives + " false negatives found by bloom filter");
  }

  @Test
  public void NoMeansNoBottom()
  {
    int n = (int) (0.49 * 40 * 40);
    int m = (int) Math.ceil(-2 * n / -0.105361);
    System.out.println("\n\n" + n + "\n\n" + m);
    BloomFilter bitsyBloom = new BloomFilter(m);
    HashSet<String> hashy = new HashSet<String>();
    int falseNegatives = 0;

     // n insertions
     for (int i = 0; i < 0.49 * 40; i++)
       for (int j = 0; j < 40; j++)
          bitsyBloom.removeBottomWall(i, j);


     // checking n different coordinates, none of which were added
     for (int i = 0; i < 0.49 * 40; i++)
     {
       for (int j = 0; j < 40; j++)
       {
          if (bitsyBloom.hasBottom(i, j))
            falseNegatives++;
        }
      }

     //assertTrue(true);
     System.out.println("\n\nTest 3 - NoMeansNo.\n"+n+" insertions\n"+falseNegatives+" false negatives\n\n");
     assertTrue(falseNegatives == 0, falseNegatives + " false negatives found by bloom filter");
  }
}
