/*
 * class MazePlay
 * 
 * GUI program for Maze
 * COSC 102, Colgate University
 * (c) 2012 Prof. Vijay Ramachandran, all rights reserved.
 * 
 * DO NOT MODIFY THE CODE IN THIS FILE
 * 
 * Launch the program using command line arguments as specified in class doc.
 */


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

/**
 * Program to display and solve mazes.  Takes input from files in the maze file format or generates (and optionally saves) random mazes.
 * <p>
 * MazePlay takes as its first command-line argument either a switch that indicates that it should generate a random maze or a switch that indicates that it should open a saved maze file.  See the <code>main()</code> method for more information.
 * Regardless of the switch used, MazePlay opens a window and draws the maze.
 * <p>
 * MazePlay contains code to manage a computer maze solver.  To start the solver, click anywhere in the maze window.
 * The window will display the number of forward and backward steps, and the position of the
 * solver will be shown in the maze.
 * The solver can be suspended and resumed by clicking in the window.
 * <p>
 * <b>You will have to implement the logic for the solver</b>, but the logic for controlling the solver
 * thread (via mouse clicking) has already been implemented.
 * The solver code and the data structure for storing the maze is contained in the class <code>Maze</code>, so the source code for <code>MazePlay</code>
 * should not be modified.
 * <p>
 * You can close any window at any time.  The program should end when all its windows close
 * (although there is no explicit action to indicate this).
 * Closing a window will kill that window's solver thread.
 */
public class MazePlay extends JComponent implements Runnable, WindowListener, MouseListener
{
    private Maze maze;
	
    // Constants for cell-color states
    /**
     * Specifies an empty, unvisited cell state.
     */
    public static final int E = 0;
    /**
     * Cell state for forward traversal through the maze.
     */
    public static final int F = 1;
    /**
     * Cell state for backwards traversal through the maze.
     */
    public static final int B = 2;
    
    private static final Color colE = Color.white;
    private static final Color colF = Color.green;
    private static final Color colB = Color.red;
    
    
    // Data members for drawing state
    private int w = 0, h = 0;     // width and height
    private boolean[] right;      // right walls
    private boolean[] bot;        // bottom walls
    private Color[] color;        // cell-drawing color (and state)
    private int fw = 0, bk = 0;   // count of forward / backward steps
    
    // Drawing constants
    private final int Dth = 4;  // thickness of lines
    private final int Dsz = 14;  // size of cell
    private final int Dmg = 10; // margin

    // Thread control  [achieved through setState()] and cleanup
    private volatile boolean suspended = true;
    private volatile boolean alive = true;
	
	// Utility methods to convert from [0, w*h] to (x,y) and back
    private int onedim(int row, int col) throws IndexOutOfBoundsException
    {
        if (  col < 0 || col >= w
			|| row < 0 || row >= h )
            throw new IndexOutOfBoundsException(String.format("Cell [%d,%d] does not exist", row, col));
        else
            return row * w + col;
    }
    
    private int twodimcol(int c) throws IndexOutOfBoundsException
    {
        if ( c < 0  || c >= w * h )
            throw new IndexOutOfBoundsException();
        else
            return c % w;
    }
    
    private int twodimrow(int c) throws IndexOutOfBoundsException
    {
        if ( c < 0  || c >= w * h )
            throw new IndexOutOfBoundsException();
        else
            return c / w;
    }
    
	
    // Adjust outer borders
    private void fixCellBorders()
	{
		for (int i = 1; i < h; i++)
			right[(i * w) - 1] = true;
        
        if (w * h > 0)
            right[(w * h) - 1] = false;
        
		for (int i = w * (h-1); i < w * h; i++)
			bot[i] = true;
	}
	
	// Drawing state functions
    private boolean getRight(int c) throws IndexOutOfBoundsException
    {
        return right[c];
    }
    
    private boolean getRight(int row, int col) throws IndexOutOfBoundsException
    {
        return getRight(onedim(row, col));
    }
	
    private boolean getBot(int c) throws IndexOutOfBoundsException
    {
        return bot[c];
    }
    
    private boolean getBot(int row, int col) throws IndexOutOfBoundsException
    {
        return getBot(onedim(row, col));
    }
    
    private int getState(int c) throws IndexOutOfBoundsException
    {
        if (color[c] == colF)
            return F;
        else if (color[c] == colB)
            return B;
        else
            return E;
    }
    
    /**
     * Indicates a cell's traversal state (for use with the solver).
     * Traversal state gets set by <code>setState()</code>, to be used by the solver.
     * 
     * @param   row   row number (vertical coordinate) of the cell whose state is being queried
     * @param   col   column number (horizontal coordinate) of the cell whose state is being queried
     * @return  <code>MazePlay.E</code> if the cell has not been visited; <code>MazePlay.F</code> if the
     *          cell has been visited in the forward direction; <code>MazePlay.B</code> if the cell
     *          has been visited during backtracking
     * @throws  IndexOutOfBoundsException   if <code>col &lt; 0</code>, <code>row &lt; 0</code>,
     *                      <code>col &gt; = width</code>, or <code>row &gt;= height</code>
     */
    public int getState(int row, int col) throws IndexOutOfBoundsException
    {
        return getState(onedim(row, col));
    }
    
    private boolean visited(int c) throws IndexOutOfBoundsException
    {
        return (getState(c) == E ? false : true);
    }
    
    /**
     * Indicates a cell's traversal state (for use with the solver).
     * Traversal state gets set by <code>setState()</code>, to be used by the solver.
     * Shorthand for <code>(getState(row, col) != MazePlay.E)</code>.
     * 
     * @param   row   row number (vertical coordinate) of the cell whose state is being queried
     * @param   col   column number (horizontal coordinate) of the cell whose state is being queried
     * @return  <code>true</code> if the cell has been visited (<code>MazePlay.F</code> or <code>MazePlay.B</code>);
     *          <code>false</code> if the cell has not been visited (<code>MazePlay.E</code>
     * @throws  IndexOutOfBoundsException   if <code>col &lt; 0</code>, <code>row &lt; 0</code>,
     *                      <code>col &gt; = width</code>, or <code>row &gt;= height</code>
     */
    public boolean visited(int row, int col) throws IndexOutOfBoundsException
    {
        return visited(onedim(row, col));
    }
	
    private void setRight(int c, boolean b) throws IndexOutOfBoundsException
    {
        right[c] = b;
        fixCellBorders();
    }
    
    private void setRight(int row, int col, boolean b) throws IndexOutOfBoundsException
    {
        setRight(onedim(row, col), b);
    }
    
    private void setBot(int c, boolean b) throws IndexOutOfBoundsException
    {
        bot[c] = b;
        fixCellBorders();
    }
    
    private void setBot(int row, int col, boolean b) throws IndexOutOfBoundsException
    {
        setBot(onedim(row, col), b);
    }
    
    private boolean setState(int c, int v) throws IndexOutOfBoundsException, ThreadDeath
    {
        controlThread();
		
		boolean changed = false;
        
        switch (v) {
            case F:
				if (color[c] != colF) {
					color[c] = colF;
					fw++;
					changed = true;
				}
                break;
                
            case B:
				if (color[c] != colB) {
					color[c] = colB;
					bk++;
					changed = true;
				}
                break;
                
            case E:
				if (color[c] != colE) {
					color[c] = colE;
					changed = true;
				}
                break;
				
            default:
                break;
        }
        fillCell(twodimrow(c), twodimcol(c));
        drawBanner();
		
		return changed;
    }
    
    /**
     * Sets the solver-traversal state of a maze cell.
     * Also maintains thread control for the solver.
     * 
     * @param   row   row number (vertical coordinate) of the cell whose state is being changed
     * @param   col   column number (horizontal coordinate) of the cell whose state is being changed
     * @param   v   <code>MazePlay.F</code> to indicate forward traversal; <code>MazePlay.B</code> to indicate backwards traversal; <code>MazePlay.E</code> (or other value) to indicate unvisited (empty)
     * @return  <code>true</code> if the cell state is changed by this call;
     *          <code>false</code> if the cell state is not changed (either <code>v</code> is an unrecognized state or the state of the cell is already <code>v</code>)
     * @throws  IndexOutOfBoundsException   if <code>col &lt; 0</code>, <code>row &lt; 0</code>,
     *                      <code>col &gt; = width</code>, or <code>row &gt;= height</code>
     * @throws  ThreadDeath     if the solver thread has been killed
     */
    public boolean setState(int row, int col, int v) throws IndexOutOfBoundsException, ThreadDeath
    {
        return setState(onedim(row, col), v);
    }
	

    
	// Thread control
    
    /*
     * Toggles the state of the solver thread.
     */
    private void TOGGLE()
    {
        suspended = !suspended;
    }
    
    /*
     * Kills the solver thread.
     */
    private void KILL()
    {
        alive = false;
    }
    
    private void controlThread() throws ThreadDeath
    {
        while (suspended && alive) {
            drawBanner();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) { }
        }
		
        if (alive) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) { }
        } else
            throw new ThreadDeath();
    }
	

    // Constructor
	
    private MazePlay(Maze m)
    {
		super();
		
        this.maze = m;
		this.w = maze.width();
		this.h = maze.height();
		
		// Initialize drawing state
		
        right = new boolean[w * h];
        bot = new boolean[w * h];
        color = new Color[w * h];
		
		// Code to fill drawing state based on maze
        for (int i = 0; i < w * h; i++) {
            right[i] = maze.getRight(twodimrow(i), twodimcol(i));
            bot[i] = maze.getBot(twodimrow(i), twodimcol(i));
            color[i] = colE;
        }
        
		// Make component ready to display
		fixCellBorders();
        fw = 0;
        bk = 0;

		setBackground(Color.white);
		setOpaque(true);
    }
    
    /**
     * Thread to create a window to display the maze.
     * Assumes a <tt>MazePlay</tt> object has been initialized to contain the maze to display.
     */
    public void run()
    {        
        if (maze == null)
            return;

        JFrame f = new JFrame("MazePlay: " + maze.title());
        f.setSize(Math.max(20 + 20 * maze.width(), 300), Math.max(60 + 20 * maze.height(), 300));
        f.setBackground(Color.white);
        f.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        
        f.setContentPane(this);
        f.addWindowListener(this);
        f.addMouseListener(this);
        
        f.setVisible(true);
        f.toFront();
		
		// Invoke the solver thread
        suspended = true;
		alive = true;

        new Thread(){
            private MazePlay player = null;
            private Maze maze = null;
            
            Thread init(MazePlay player, Maze maze) {
                this.player = player;
                this.maze = maze;
                return this;
            }
            
            public void run() {
                maze.solve(player);
            }
        }.init(this, maze).start();
    }

    /**
     * Window handler.
     * Kills the solver thread and disposes the window being closed.
     */
    public void windowClosing(WindowEvent e)
    {
        KILL();
        e.getWindow().dispose();
    }
    
    /**
     * Does nothing; declared to implement the <tt>WindowListener</tt> interface.
     */
    public void windowActivated(WindowEvent e)
        { return; }

    /**
     * Does nothing; declared to implement the <tt>WindowListener</tt> interface.
     */
    public void windowClosed(WindowEvent e)
        { return; }

    /**
     * Does nothing; declared to implement the <tt>WindowListener</tt> interface.
     */
    public void windowDeactivated(WindowEvent e)
        { return; }

    /**
     * Does nothing; declared to implement the <tt>WindowListener</tt> interface.
     */
    public void windowDeiconified(WindowEvent e)
        { return; }

    /**
     * Does nothing; declared to implement the <tt>WindowListener</tt> interface.
     */
    public void windowIconified(WindowEvent e)
        { return; }
        
    /**
     * Does nothing; declared to implement the <tt>WindowListener</tt> interface.
     */
    public void windowOpened(WindowEvent e)
        { return; }

    /**
     * Mouse handler.
     * Toggles the state of the solver thread (suspends or resumes).
     */
    public void mouseClicked(MouseEvent e)
    {
        TOGGLE();
    }
    
    /**
     * Does nothing; declared to implement the <tt>MouseListener</tt> interface.
     */
    public void mouseEntered(MouseEvent e)
        { return; }

    /**
     * Does nothing; declared to implement the <tt>MouseListener</tt> interface.
     */
    public void mouseExited(MouseEvent e)
        { return; }

    /**
     * Does nothing; declared to implement the <tt>MouseListener</tt> interface.
     */
    public void mousePressed(MouseEvent e)
        { return; }

    /**
     * Does nothing; declared to implement the <tt>MouseListener</tt> interface.
     */
    public void mouseReleased(MouseEvent e)
        { return; }    
    

    
	// Painting functions
    
    private void drawBanner()
    {
        Graphics g = getGraphics();
        
        if (g != null)
            drawBanner(g);
    }
	
    private void drawBanner(Graphics g)
    {
        Color c = g.getColor();
        g.setColor(Color.white);
        g.fillRect(0, 0, getWidth(), 30);
        g.setColor(Color.black);
        g.drawString("fw(" + fw + ") bk(" + bk + ") tot(" + (fw + bk) + ")", Dmg+120, 25);
        
        if (suspended) {
            g.setColor(Color.red);
            g.drawString("Solver suspended", Dmg, 25);
        } else {
            if (getState(h-1, w-1) == F) {
                g.setColor(Color.blue);
                g.drawString("Solver complete", Dmg, 25);
            } else {
                g.setColor(new Color(0, 128, 0));
                g.drawString("Solver running", Dmg, 25);
            }
        }
        
        g.setColor(c);
    }
    
    private void fillCell(int row, int col)
    {
        Graphics g = getGraphics();
        
        if (g != null)
        {
            g.translate(Dth + Dmg, Dth + Dmg + 40);
            fillCell(g, row, col);
        }
    }
    
    private void fillCell(Graphics g, int row, int col)
    {
        Color c = g.getColor();
        switch (getState(row, col))
		{
			case F:
				g.setColor(colF);
				break;
				
			case B:
				g.setColor(colB);
				break;
				
			default:
				g.setColor(colE);
				break;
		}
        g.fillRect( col * Dsz, row * Dsz, Dsz - Dth, Dsz - Dth);
        g.setColor(c);
    }
	
	
    /**
     * Draws the maze in the player window.
     * 
     * @param   g   display context for GUI
     */
    public void paint(Graphics g) {
		
        super.paint(g);
        
        // draw text
        drawBanner(g);
        
        // for cell drawing, ignore margins and header
        g.translate(Dth + Dmg, Dth + Dmg + 40);
		
        // draw top line
        g.setColor(Color.black);
        g.fillRect(-Dth, -Dth, w * Dsz + Dth, Dth);
		
        // draw left line
        g.fillRect(-Dth, Dsz - Dth, Dth, (h - 1) * Dsz + Dth);
        
        for (int row = 0; row < h; row++)
        {
            for (int col = 0; col < w; col++)
            {
                if (getRight(row, col))
                    g.fillRect(col * Dsz + Dsz - Dth, row * Dsz - Dth, Dth, Dsz + Dth); 
                if (getBot(row, col))
                    g.fillRect(col * Dsz - Dth, row * Dsz + Dsz - Dth, Dsz + Dth, Dth);
                
                fillCell(g, row, col);
            }
            
        }
    }    


        /**
         * Starts the MazePlay program.
         * <p>
         * <b><i>Usage:</i></b><ul>
         * <li><tt>java MazePlay -r <i>rows</i> <i>cols</i> [<i>filename</i>]</tt>
         * <blockquote>
         * Generates a random maze with dimensions <i>rows</i> x <i>cols</i>
         * (the width is <i>cols</i> and the height is <i>rows</i>).
         * If <i>filename</i> is provided, the maze will be saved as the given filename in addition to being opened in a window.
         * </blockquote></li>
         * <li><tt>java MazePlay -f <i>filename</i></tt>
         * <blockquote>
         * Reads the maze data from the given filename and displays the maze in a window.
         * </blockquote></li>
         * </ul>
         *
         * @param   args    Command-line arguments following usage instructions described above.
         */
    public static void main(String[] args)
    {
        if (args.length < 2) {
			System.err.println("MazePlay: error, incorrect arguments given");
			return;
		}
		
		Maze m = null;
		
		if (args[0].equals("-r")) {
			// randomly generate maze
			if (args.length < 3) {
				System.err.println("MazePlay: error, random maze generation requires at least two integer arguments");
				return;
			}
			
			int h, w;
			try {
				h = Integer.parseInt(args[1]);
				w = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				System.err.println("MazePlay: error, random maze generation requires at least two integer arguments");
				return;
			}
			
			m = new Maze(h, w);
			
			if (args.length >= 4) {
				try {
					m.save(args[3]);
				} catch (IOException e) {
					System.err.print("MazePlay: error writing maze to file ");
					System.err.println(args[3]);
                    System.err.println(e.getMessage());
				}
			}
		}
		else if (args[0].equals("-f")) {
			// open filename
			
			try {
				m = new Maze(args[1]);
			} catch (IOException e) {
				System.err.print("MazePlay: error opening or reading file ");
				System.err.println(args[1]);
                System.err.println(e.getMessage());
				return;
			}
		}
		else {
			System.err.print("MazePlay: did not recognize command ");
			System.err.println(args[0]);
			return;
		}
				 
        if (m != null)
            SwingUtilities.invokeLater(new MazePlay(m));
	}
}