package ca.unbsj.cs2383;

import edu.princeton.cs.algs4.UF;
import edu.princeton.cs.algs4.StdDraw;

import java.util.Random;
import java.util.HashSet;
import java.util.Set;

public class Maze {
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
    UF uf;  // union find structures
    Set<String> removedWalls;

    // 2d array of walls, N x 2N
    // bottom wall of cell[i][j] corresponds to walls[i][2j]
    // right wall  of cell[i][j] corresponds to walls[i][2j+1]
    boolean[][] walls;


    boolean drawOnGraphicsScreen;

    public static void main(String [] args) {
        int setKindCode = Integer.parseInt(args[0]);
        if (setKindCode < 0 || setKindCode > bloomFilterSet)
            throw new RuntimeException("illegal setKind Code on cmd line");

        Maze m = new Maze(setKindCode,true); // will draw on grahics

        m.generate();
        m.printAsciiGraphics();
     }

    // a bunch of methods to map between cell ids (0,1,...N^2-1) and x,y coords
    int xyToId(int x, int y) {
        return y*N + x;
    }

    int idToX(int id) { return id % N;}
    int idToY(int id) { return id / N;}

    int idOfCellAbove(int id) { return id - N;}
    int idOfCellBelow(int id) { return id + N;}
    int idOfCellLeft(int id) { return id-1;}
    int idOfCellRight(int id) { return id+1;}


    public Maze(int setKindCode, boolean graphicsDraw) {
        uf = new UF(N*N);
        walls = new boolean[N][2*N];
        for (boolean[] a : walls)
          for (boolean b : a)
            b = true;

        // our maze requires us to get from top left
        // to bottom right.


        topLeft = xyToId(0,0);
        bottomRight = xyToId(N-1,N-1);
        r = new Random();

        drawOnGraphicsScreen = graphicsDraw;
        if (graphicsDraw) {
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

    void generate() {
        while (!uf.connected(topLeft, bottomRight)) {
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
                    connectIfNotConnected(randId, idOfCellLeft(randId));
                break;
            case RIGHT:
                if (randX != N-1)
                    connectIfNotConnected(randId, idOfCellRight(randId));
                break;
            case UP:
                if (randY != 0)
                    connectIfNotConnected(randId, idOfCellAbove(randId));
                break;
            case DOWN:
                if (randY != N-1)
                    connectIfNotConnected(randId, idOfCellBelow(randId));
                break;
            }
        }
    }


    void connectIfNotConnected(int id1, int id2) {
        if ( ! uf.connected(id1, id2))
        {
            // knock out the wall and thereby merge components
            uf.union(id1,id2);

            int x1 = idToX(id1); int y1 = idToY(id1);
            int x2 = idToX(id2); int y2 = idToY(id2);




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

    // for debugging,
    // show members of the connected component of (0,0)
    // with blanks, * for non-members
    // Note: this is not a visualization of the maze

    void print(int howMuch) {
        if (howMuch > N) howMuch = N;
        for (int y=0; y < howMuch; ++y) {
            for (int x=0; x < howMuch; ++x)
                if (uf.connected(xyToId(x,y), topLeft))
                    System.out.print(" ");
                else
                    System.out.print("*");
            System.out.println();
        }
    }


    void printAsciiGraphics()
    {
        System.out.println("printAsciiGraphics: write me");
    }

}
