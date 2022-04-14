package ca.unbsj.cs2383;

import java.util.BitSet;
import java.lang.Math;

class BloomFilter
{
   final int m;
   final BitSet bitsyBloomRight;
   final BitSet bitsyBloomBottom;

   public BloomFilter(int size)
   {
      m = size;

      bitsyBloomRight =  new BitSet(m);
      bitsyBloomBottom =  new BitSet(m);

      //set all values in BitSet to true, ie, all walls are there at the start
      bitsyBloomRight.set(0, m - 1, true);
      bitsyBloomBottom.set(0, m - 1, true);
   }

   // construct string from coordinates, hash it two ways, and set the bits to be false
   public void removeRightWall(int x, int y)
   {
      String wall = x + ";" + y;
      bitsyBloomRight.set(wall.hashCode() % m, false);
      bitsyBloomRight.set(hashinItUp(wall), false);
   }

   public void removeBottomWall(int x, int y)
   {
      String wall = x + ";" + y;
      bitsyBloomBottom.set(wall.hashCode() % m, false);
      bitsyBloomBottom.set(hashinItUp(wall), false);
   }

   public boolean hasRight(int x, int y)
   {
      String wall = x + ";" + y;
      boolean isWall;
      if (bitsyBloomRight.get(wall.hashCode() % m)) {
         isWall = true;
      } else isWall = bitsyBloomRight.get(hashinItUp(wall));
      return isWall;
   }


   public boolean hasBottom(int x, int y)
   {
      String wall = x + ";" + y;
      boolean isWall;
      if (bitsyBloomBottom.get(wall.hashCode() % m)) {
         isWall = true;
      } else isWall = bitsyBloomBottom.get(hashinItUp(wall));
      return isWall;
   }

   // hash function for strings
   private int hashinItUp(String s)
   {
      int hash = 0;
      for (int i = 0; i < s.length(); i++)
      hash = (29 * hash + s.charAt(i));

      return Math.abs(hash) % m;
  }


}
