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
      int n = (int) (0.49 * 40 * 40);
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
            if (bitsyBloom.hasRight(4, i))
               falsePositives++;
         }
         if (falsePositives > 0.005 * n && falsePositives < 0.02 * n)
            successfulRuns++;
      }

      System.out.println("\n\n" + successfulRuns + "/10 successful runs.\n\n False Positive Rate on last run: " + (double) falsePositives/n + "%\n\n");
      assertTrue(successfulRuns >= 5, successfulRuns + " runs with false positive rate between 0.5% and 2%\t" + (double) falsePositives/n);
  }

  @Test
  public void NoMeansNo()
  {
      int n = (int) (0.49 * 40 * 40);
      int m = (int) Math.ceil(-2 * n / -0.105361);
      BloomFilter bitsyBloom = new BloomFilter(m);
      HashSet<String> hashy = new HashSet<String>();
      int falseNegatives = 0;

         // n insertions
         for (int i = 0; i < n; i++)
         {
           bitsyBloom.removeRightWall(i, 6);
           hashy.add(i + ";" + 6);
         }

         // checking n different coordinates, none of which were added
         for (int i = n; i < 2 * n; i++)
         {
            if (!bitsyBloom.hasRight(i, 6));
               falseNegatives++;
         }


      assertTrue(falseNegatives == 0, falseNegatives + " false negatives found by bloom filter");
  }
}
