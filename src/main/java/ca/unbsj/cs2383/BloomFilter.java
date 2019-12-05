package ca.unbsj.cs2383;

import java.util.BitSet;

class BloomFilter
{
   int m;
   BitSet bitsyBloomRight;
   BitSet bitsyBloomBottom;

   public BloomFilter(int size)
   {
      m = size;

      bitsyBloomRight =  new BitSet(m);
      bitsyBloomBottom =  new BitSet(m);

      //set all values in BitSet to true, ie, all walls are there at the start
      bitsyBloomRight.set(0, m - 1, true);
      bitsyBloomBottom.set(0, m - 1, true);
   }

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
      if (!bitsyBloomRight.get(wall.hashCode() % m) && !bitsyBloomRight.get(hashinItUp(wall)))
          isWall = false;
      else
          isWall = true;
      return isWall;
   }


   public boolean hasBottom(int x, int y)
   {
      String wall = x + ";" + y;
      boolean isWall;
      if (!bitsyBloomBottom.get(wall.hashCode() % m) && !bitsyBloomBottom.get(hashinItUp(wall)))
          isWall = false;
      else
          isWall = true;
      return isWall;
   }

   // hash function for strings
   private int hashinItUp(String s)
   {
      int hash = 0;
      for (int i = 0; i < s.length(); i++)
      hash = (29 * hash + s.charAt(i));

      return hash % m;
  }


}
