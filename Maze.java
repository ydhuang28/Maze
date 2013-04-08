/*
 * class Maze
 *
 * Data structure and logic for Maze
 * COSC 102, Colgate University
 *
 * You are supposed to modify various parts of this file.
 * The main program should be launched by running MazePlay.main()
 */
//hi

import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.util.*;


/**
 * Data structure for the Maze program.
 * <p>
 * The class <code>Maze</code> serves both as the data structure for a maze to be played and the place where the logic for the maze solver resides.
 * You are supposed to modify various parts of this file.
 * <p>
 * The two constructors for the Maze structure populate data that you must define so that the various get methods for the structure of the maze work properly.
 * One constructor that reads the maze structure from a file is already implemented.
 * You will have to implement the constructor that generates a random maze of a given number of rows and columns.
 * <p>
 * The <code>solve()</code> method needs to be implemented, and will contain the logic for the maze solver.
 * It should run a depth-first search from the start of the maze (cell [0,0]).
 * As the solver is running, it should update the display with its location using the <code>MazePlay.setState()</code> method.
 * The MazePlay GUI will reflect the solver state whenever this method is called by coloring parts of the maze in the GUI.
 */
public class Maze
{
    private int rows, cols;
    

    /**
     * Returns the width of the maze.
     * <p>
     * <b>This method has already been implemented.</b>
     *
     * @return  width (horizontal dimension, or number of columns) of the maze, provided on construction
     */
    public int width()
    { return cols; }
    
    /**
     * Returns the height of the maze.
     * <p>
     * <b>This method has already been implemented.</b>
     *
     * @return  height (vertical dimension, or number of rows) of the maze, provided on construction
     */
    public int height()
    { return rows; }
    
    /**
     * Gets the title of the maze, used in the window's title bar.
     * <p>
     * <b>This method has already been implemented.</b>
     *
     * @return  the maze title
     */
    public String title()
    { return title; }
 
    /**
     * Indicates whether or not a cell's right wall is present in the maze
     * <p>
     * <b>You must implement this method.</b>
     *
     * 
     * @param   row   row number (vertical coordinate) of the cell whose properties are being examined
     * @param   col   column number (horizontal coordinate) of the cell whose properties are being examined
     * @return  <code>true</code> if the right wall of the cell is present in the maze,
     *          false otherwise
     * @throws  IndexOutOfBoundsException   if <code>col &lt; 0</code>, <code>row &lt; 0</code>,
     *                      <code>col &gt; = width</code>, or <code>row &gt;= height</code>
     */
    public boolean getRight(int row, int col) throws IndexOutOfBoundsException
    {
  return false;
    }

    /**
     * Indicates whether or not a cell's bottom wall is present in the maze
     * <p>
     * <b>You must implement this method.</b>
     *
     * 
     * @param   row   row number (vertical coordinate) of the cell whose properties are being examined
     * @param   col   column number (horizontal coordinate) of the cell whose properties are being examined
     * @return  <code>true</code> if the bottom wall of the cell is present in the maze,
     *          false otherwise
     * @throws  IndexOutOfBoundsException   if <code>col &lt; 0</code>, <code>row &lt; 0</code>,
     *                      <code>col &gt; = width</code>, or <code>row &gt;= height</code>
     */
    public boolean getBot(int row, int col) throws IndexOutOfBoundsException
    {
        return false;
    }
 
 /**
     * Sets whether or not the right wall of a maze cell exists.
     * <p>
     * <b>You must implement this method.</b>
     *
     * 
     * @param   row   row number (vertical coordinate) of the cell whose properties are being changed
     * @param   col   column number (horizontal coordinate) of the cell whose properties are being changed
     * @param   b   <code>true</code> if the right wall should exist, <code>false</code> if it should not exist
     * @throws  IndexOutOfBoundsException   if <code>col &lt; 0</code>, <code>row &lt; 0</code>,
     *                      <code>col &gt; = width</code>, or <code>row &gt;= height</code>
     */
    public void setRight(int row, int col, boolean b) throws IndexOutOfBoundsException
    {

    }
    
    /**
     * Sets whether or not the bottom wall of a maze cell exists.
     * <p>
     * <b>You must implement this method.</b>
     *
     * 
     * @param   row   row number (vertical coordinate) of the cell whose properties are being changed
     * @param   col   column number (horizontal coordinate) of the cell whose properties are being changed
     * @param   b   <code>true</code> if the bottom wall should exist, <code>false</code> if it should not exist
     * @throws  IndexOutOfBoundsException   if <code>col &lt; 0</code>, <code>row &lt; 0</code>,
     *                      <code>col &gt; = width</code>, or <code>row &gt;= height</code>
     */
    public void setBot(int row, int col, boolean b) throws IndexOutOfBoundsException
    {

    }
    
    /**
     * Runs a depth-first search to explore the maze, starting at the top-left cell (row 0, column 0).
     * <p>
     * <b>You must implement this method.</b>
     * <p>
     * The parameter <code>player</code> given to this method gives access to the <code>MazePlay</code> object that represents the GUI for the maze.
     * You should use the <code>setState()</code> function of <code>MazePlay</code> to keep track of where your solver is going during its exploration.
     * This method is also hooked into the thread-control mechanism that keeps the solver running.
     *
     * 
     * @param   player          A reference to the MazePlay object that represents the GUI displaying the maze
  * @throws ThreadDeath  if the player window gets closed during execution of the search
     */
 public void solve(MazePlay player) throws ThreadDeath
 {
        
 }
 
    /**
     * Creates a randomly generated maze of a given size.
     * <p>
     * <b>You must implement this method.</b>
     *
     * 
     * @param   rows           the maze height (vertical dimension, number of rows)
     * @param   cols           the maze width (horizontal dimension, number of columns)
     */
    public Maze(int rows, int cols)
    {
  title = String.format("rand(%dx%d)", rows, cols);
        this.rows = rows;
        this.cols = cols;

    }
    
 
    /**
     * Creates a maze object from a file.
     * <p>
     * <b>This method has already been implemented.</b>
     *
     * 
     * @param   filename  The file containing the maze to load.
  * @throws IOException  if an input/output error occurs while trying to read the given input file
     */
    public Maze(String filename) throws IOException
    {
  Scanner scan = null;
  
  try {
   scan = new Scanner(new FileReader(filename));
  } catch (IOException e) {
   throw new IOException(prog + ": error opening filename " + filename);
  }
  

  if (!scan.hasNextLine())
      throw new IOException(prog + ": file " + filename + " is empty");

  String[] line = scan.nextLine().split("\\s+");
  if (line.length < 3 || !(line[0].equals("maze")))
   throw new IOException(prog + ": " + filename + " is not a maze");
            
  int w = 0, h = 0;
  try {
   w = Integer.parseInt(line[1]);
   h = Integer.parseInt(line[2]);
   if (w < 1 || h < 1)
    throw new NumberFormatException();
  } catch (NumberFormatException e) {
   throw new IOException(prog + ": " + filename + " contains a maze with illegal dimension(s)");
  }
        
        this.rows = h;
        this.cols = w;
            
  for(int i = 0; i < w * h; i++) {
   int col = i%w, row = i/w;

   if (!scan.hasNextLine())
    throw new IOException(prog + ": " + filename + " missing cell descriptions starting at [" + row + "," + col + "]");

   String cell[] = scan.nextLine().split("\\s+");
   if (cell.length < 2)
    throw new IOException(prog + ": " + filename + " contains bad description for cell[" + row + "," + col + "]");
   
   boolean r, b;
   
   try {
    r = (Integer.parseInt(cell[0]) != 0) ? true : false;
    b = (Integer.parseInt(cell[1]) != 0) ? true : false;
   } catch (NumberFormatException e) {
    throw new IOException(prog + ": " + filename + " contains bad description for cell[" + row + "," + col + "]");
   }
   
   setRight(row, col, r);
   setBot(row, col, b);
  }
  
  title = filename;
    }
 
    /**
     * Saves a maze object to a file.
     * <p>
     * <b>This method has already been implemented.</b>
     *
     * 
     * @param   filename  The file in which to store the maze.  If the file exists, it will be overwritten.
  * @throws IOException  if an error occurs while trying to write the maze to file
     */
    public void save(String filename) throws IOException
    {
        PrintWriter pw = new PrintWriter(filename);
        
        int w = width();
        int h = height();
        
        pw.printf("maze %d %d%n", w, h);
        
        for (int row = 0; row < h; row++) {
            for (int col = 0; col < w; col++) {
                try {
                    int r = getRight(row, col) ? 1 : 0;
                    int b = getBot(row, col) ? 1 : 0;
                    pw.printf("%d %d%n", r, b);
                } catch (IndexOutOfBoundsException e) {
                    pw.println();
                    String err = String.format("%s: error while writing file at cell [%d,%d]", prog, row, col);
                    pw.println(err);
                    pw.close();
                    throw new IOException(err);
                }
            }
        }
        pw.close();
        
        title += String.format(":%s", filename);
    }

 private final String prog = "MazePlay";
 private String title;
    
}
