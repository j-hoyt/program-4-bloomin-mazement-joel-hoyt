package ca.unbsj.cs2383;

import edu.princeton.cs.algs4.UF;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.Graph;
import edu.princeton.cs.algs4.BreadthFirstPaths;
import edu.princeton.cs.algs4.Stack;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.BitSet;
import java.util.HashSet;
import java.lang.StringBuilder;

public class Maze
{
  static final int LEFT=0, RIGHT=1, UP=2, DOWN=3;

  static final int CELLSIZE=20;
  static final int twoDBooleanSet=1;
  static final int bitSetSet=2;
  static final int hashSetSet=3;
  static final int bloomFilterSet=4;

  static final int N = 40; // grid is NxN
  static final double GRAPHICSSCALE = 1.0/N;

  int topLeft, bottomRight; // node IDs
  Random r;
  UF uf;              // union find structures
  int wallCount;      // count of how many walls are removed


  // Case 1: 2d Boolean array
  // 2d array of walls, N x 2N
  // bottom wall of cell[i][j] corresponds to walls[i][2j]
  // right wall  of cell[i][j] corresponds to walls[i][2j+1]
  boolean[][] walls;

  // Case 2: BitSet
  BitSet bitsy;

  // Case 3: HashSet
  Set<String> hashy;

  // Case 4: Bloom Filter
  int filterSize;
  BloomFilter bitsyBloom;

  // For Solving and Drawing:
  Graph g;
  BreadthFirstPaths bfp;
  ArrayList<Integer> solvedPath; // ArrayList of cell IDs that are on the correct path





  // same as setKindKode
  int k;


  boolean drawOnGraphicsScreen;

  public static void main(String [] args)
  {
      int setKindCode = Integer.parseInt(args[0]);
      if (setKindCode < 0 || setKindCode > bloomFilterSet)
          throw new RuntimeException("illegal setKind Code on cmd line");

      Maze m = new Maze(setKindCode, true); // will draw on grahics

      m.generate();
      m.printAsciiGraphics(setKindCode);
      System.out.println(m.wallCount + "\n\n");


   }

  // a bunch of methods to map between cell ids (0,1,...N^2-1) and x,y coords
  int xyToId(int x, int y) { return y*N + x; }

  int idToX(int id) { return id % N;}
  int idToY(int id) { return id / N;}

  int idOfCellAbove(int id) { return id - N;}
  int idOfCellBelow(int id) { return id + N;}
  int idOfCellLeft(int id) { return id - 1;}
  int idOfCellRight(int id) { return id + 1;}


  public Maze(int setKindCode, boolean graphicsDraw)
  {
    k = setKindCode;
    uf = new UF(N*N);
    wallCount = 0;
    g =  new Graph(N * N);

    // our maze requires us to get from top left
    // to bottom right.

    topLeft = xyToId(0,0);
    bottomRight = xyToId(N-1,N-1);
    r = new Random();

    drawOnGraphicsScreen = graphicsDraw;
    if (graphicsDraw)
    {
      StdDraw.setCanvasSize(N*CELLSIZE,N*CELLSIZE);
      StdDraw.setPenColor(StdDraw.BLACK);
      StdDraw.setPenRadius(0.004);
      StdDraw.clear();

      // initial grid
      // StdDraw puts the origin at the BOTTOM left, so
      // things are basically "upside down" when visualized
      // with it.....unless we do these crazy (1-y) transform to
      // fix this.

      for (int i=0; i <= N; ++i) {
          StdDraw.line(0, 1- i/(double)N, 1, 1-i/(double) N);
          StdDraw.line(i/(double)N, 0, i/(double)N,  1);
      }
      StdDraw.setPenRadius(0.006); // don't want shadow of former line
    }


  }

  //First, generate initializes the right data structure, according to the setKindCode parameter

  void generate()
  {
    switch (k)
    {
      case twoDBooleanSet:
        // all walls initialized to true. Set to false when removed.
        walls = new boolean[N][2*N];
        for (int i = 0; i < N; i++)
        {
          for (int j = 0; j < 2 * N; j++)
          {
            walls[i][j] = true;
          }
        }
        break;

      case bitSetSet:
        // set all bits to true to indicate the presence of all walls
        // set them false when removed
        // right wall of cell(x,y)  : at the (y*N + x)th index
        // bottom wall of cell(x,y) : at the (y*N + x + (N*N))th index
        bitsy = new BitSet(2 * N * N);
        bitsy.set(0, 2 * N * N - 1, true);
        break;

      case hashSetSet:
        hashy =  new HashSet<String>(2 * N * N);
        break;

      case bloomFilterSet:
        // size of bitset for 1% false positive rate with N^2 insertions
        // expected number of insertions (walls removed) = 0.485 * N^2
        // Bloom filter holds two bitsets, for right and bottom walls.
        filterSize = (int) Math.ceil(-2 * 0.485 * N * N / -0.105361); // -2N^2 / ln(0.9)
        bitsyBloom = new BloomFilter(filterSize);
        break;
    }

    while (!uf.connected(topLeft, bottomRight))
    {
      // choose a random cell
      int randX = r.nextInt(N);
      int randY = r.nextInt(N);
      int randId = xyToId(randX,randY);

      // choose one of its 4 neighbours
      int randDir = r.nextInt(4);

      // knock down a wall, if present, between cell and
      // its chosen neighbour if they are not yet in the
      // same component

      switch(randDir) {
      case LEFT:
          if (randX != 0)
            connectIfNotConnected(randId, idOfCellLeft(randId), LEFT);
          break;
      case RIGHT:
          if (randX != N-1)
              connectIfNotConnected(randId, idOfCellRight(randId), RIGHT);
          break;
      case UP:
          if (randY != 0)
              connectIfNotConnected(randId, idOfCellAbove(randId), UP);
          break;
      case DOWN:
          if (randY != N-1)
              connectIfNotConnected(randId, idOfCellBelow(randId), DOWN);
          break;
      }
    }

    // find paths from topLeft to all other cells
    // graph G constructed in connectIfNotConnected() method below
    BreadthFirstPaths bfp = new BreadthFirstPaths(g, topLeft);

    // Stack of cells on shortest path from topleft to bottom right
    Stack<Integer> s = (Stack) bfp.pathTo(bottomRight);

    // pop the whole stack into an arrayList so later we can easily check if
    // each cell is on the shortest path
    solvedPath = new ArrayList<Integer>();

    while (!s.isEmpty())
    {
      solvedPath.add(s.pop());
    }

    // draw pretty blue dots on every cell on the shortest path
    if (drawOnGraphicsScreen)
    {
      for (int i : solvedPath)
      {
        int xpath = idToX(i);
        int ypath = idToY(i);
        StdDraw.setPenColor(StdDraw.BLUE);
        StdDraw.filledCircle((xpath + 0.5) / N, 1 - ((ypath + 0.5) / N), 0.007);
      }
   }
}

   // modified to take direction as another parameter
  void connectIfNotConnected(int id1, int id2, int direction) {
      if ( ! uf.connected(id1, id2))
      {
          // adding edges to graph
          g.addEdge(id1, id2);

          // knock out the wall and thereby merge components
          uf.union(id1,id2);

          int x1 = idToX(id1); int y1 = idToY(id1);
          int x2 = idToX(id2); int y2 = idToY(id2);

          if (direction == LEFT) {
            // go to the left cell and knock down the right wall
            removeRightWall(x2, y2);
          }

          else if (direction == RIGHT) {
            // knock down the right wall
            removeRightWall(x1, y1);
          }

          else if (direction == UP) {
            // go up and knock down the bottom wall
            removeBottomWall(x2,y2);
          }

          else if (direction == DOWN) {
            // knock down the bottom wall
            removeBottomWall(x1, y1);
          }

          wallCount++;


          if (drawOnGraphicsScreen)
          {
            // erase the wall
            StdDraw.setPenColor(StdDraw.WHITE);
            if (x1 == x2)
            {
                // vertical adjacency needs a horizontal line seg
                int greaterY=Math.max(y1,y2);
                StdDraw.line(x1*GRAPHICSSCALE+0.002, 1-greaterY*GRAPHICSSCALE,
                             (x1+1)*GRAPHICSSCALE-0.002, 1-greaterY*GRAPHICSSCALE);
            }
            else
            {
              // need a vertical line set
              int greaterX=Math.max(x1,x2);
              StdDraw.line(greaterX*GRAPHICSSCALE, 1-(y1*GRAPHICSSCALE+0.002),
                           greaterX*GRAPHICSSCALE, 1-((y1+1)*GRAPHICSSCALE-0.002));
            }
          }
        }
      else {
          // nothing.  Even if there is a wall here, it does
          // nothing except make the maze harder.  So leave
          // the wall.
      }
  }

  void removeRightWall(int x, int y)
  {
    switch (k)
    {
      case twoDBooleanSet:
        walls[y][(2 * x) + 1] = false;
        break;

      case bitSetSet:
        bitsy.set(y * N + x, false);
        break;

      case hashSetSet:
        hashy.add(x + ";" + y);
        break;

      case bloomFilterSet:
        bitsyBloom.removeRightWall(x, y);
        break;
    }
  }

  void removeBottomWall(int x, int y)
  {
    switch (k)
    {
      case twoDBooleanSet:
        walls[y][(2 * x)] = false;
        break;

      case bitSetSet:
        bitsy.set(y * N + x + (N * N), false);
        break;

      case hashSetSet:
        y += N;
        x += N;
        hashy.add(x + ";" + y);
        break;

      case bloomFilterSet:
        removeBottomWall(x,y);
        break;
    }
  }

  boolean isRightWall(int x, int y)
  {
    boolean isWall;
    switch (k)
    {
      case twoDBooleanSet:
        isWall = walls[y][(2 * x) + 1];
        break;

      case bitSetSet:
        isWall = bitsy.get(y * N + x);
        break;

      case hashSetSet:
        isWall = !hashy.contains(x + ";" + y);
        break;

      case bloomFilterSet:
        isWall = bitsyBloom.hasRight(x, y);
        break;
    }
    return isWall;
  }

  boolean isBottomWall(int x, int y)
  {
    boolean isWall;
    switch (k)
    {
      case twoDBooleanSet:
        isWall = walls[y][(2 * x)];
        break;

      case bitSetSet:
        isWall = bitsy.get((y * N + x) + (N * N));
        break;

      case hashSetSet:
        y += N;
        x += N;
        isWall = !hashy.contains(x + ";" + y);
        break;

      case bloomFilterSet:
        isWall = bitsyBloom.hasBottom(x, y);
        break;
    }
    return isWall;
  }

  // for debugging,
  // show members of the connected component of (0,0)
  // with blanks, * for non-members
  // Note: this is not a visualization of the maze



  void print(int howMuch)
  {
    if (howMuch > N) howMuch = N;
    for (int y=0; y < howMuch; ++y)
    {
        for (int x=0; x < howMuch; ++x)
            if (uf.connected(xyToId(x,y), topLeft))
                System.out.print(" ");
            else
                System.out.print("*");
        System.out.println();
    }
  }



  /*
  For printing: consider each cell in four parts:
  - the two upper left chearacters are always blanks
  - the top right character
  - the bottom left two characters
  - the bottom right character

  The tops of each cell are printed directly to the output stream
  At the same time, the bottoms of each cell are appended to a stringbuilder
  At the end of every line, the stringbuilder is printed all at once

  For each cell, if it lies on the shortes tpath (that is, if the solvedPath ArrayList
  contains the cell id), then print bullet points ("\u2022") instead of blank spaces in the cell
  */

  void printAsciiGraphics(int kind)
  {
   //top border +----//---+
    System.out.print("+");
    for (int i = 0; i < (N * 3) - 1; i++)
      System.out.print("-");
    System.out.println("+");


    StringBuilder nextLine;
    for (int y = 0; y < N; y++)
    {
      nextLine = new StringBuilder((isBottomWall(0,y) ? "+" : "|"));
      System.out.print("|");
      for (int x = 0; x < N; x++)
      {
        //top left two characters, blank space for cell
        if (solvedPath.contains(xyToId(x, y)))
          System.out.print("\u2022\u2022");
        else
          System.out.print("  ");

        // print top right char, | or space
        if (isRightWall(x,y))
          System.out.print("|");
        else
          if (solvedPath.contains(xyToId(x, y)))
            System.out.print("\u2022");
          else
            System.out.print(" ");

        //print two bottom left chars, blank or --
        if (isBottomWall(x,y))
          nextLine.append("--");
        else
          if (solvedPath.contains(xyToId(x, y)))
            nextLine.append("\u2022\u2022");
          else
            nextLine.append("  ");

        //print bottom right char, either + | - or blank
        if (isRightWall(x,y) && isBottomWall(x,y))
          nextLine.append("+");

        if (isRightWall(x,y) && !isBottomWall(x,y))
        {
          if (x != N - 1)
          {
            if (isBottomWall(x + 1, y))
              nextLine.append("+");
            else
              nextLine.append("|");
          }
          else
            nextLine.append("|");
        }

        if (!isRightWall(x,y)  && isBottomWall(x,y))
        {
          if (y != N - 1)
          {
            if (isRightWall(x, y + 1))
              nextLine.append("+");
            else
              nextLine.append("-");
          }
          else
            nextLine.append("-");
        }

        if (!isRightWall(x,y) && !isBottomWall(x,y))
        {
         if (solvedPath.contains(xyToId(x, y)))
            nextLine.append(" ");
         else
            nextLine.append(" ");
        }
      }
      System.out.println("\n" + nextLine);
    }
  }
}
