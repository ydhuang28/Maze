/*
 * class DisjointSets
 * 
 * COSC 102, Colgate University
 * 
 * Contains an implementation of the union-find data structure.
 * To be used in the maze-generation algorithm.
 * 
 */


/**
 * Provides an implementation of weighted union-by-size with full path compression.
 * Most of this code is from Chap. 24 of <a href="http://www.aw-bc.com/catalog/academic/product/0,1144,0321322134,00.html"><i>Data Structures and Problem Solving Using Java 3/E</i></a>
 * by <a href="http://www.cs.fiu.edu/~weiss/">Mark Allen Weiss</a>.
 * The data structure operation is also explained in Chap. 1 of <i>Algorithms in Java 3/E</i> by Sedgewick.
 * <p>
 * The structure is initialized with a size representing the number of singleton sets.
 * The elements are assumed to have indices <tt>0</tt> to <tt>size-1</tt>.
 * The two main operations are union and find.  Union joins the sets containing two elements;
 * Find returns the representative element of the set containing a specific element.
 * <p>
 * If two elements <tt>x</tt> and <tt>y</tt> belong to the same set, their find operations should
 * return the same value.  Example:
 * <pre>
 *      DisjointSets d = DisjointSets(5);
 *              // creates a new structure with 5 singleton sets,
 *              // {0}, {1}, {2}, {3}, {4}
 *      
 *      d.union(1, 3);
 *      d.union(3, 4);
 *              // now the structure represents 3 sets,
 *              // {0}, {1, 3, 4}, {2}
 *              
 *      int x = d.find(1);
 *      int y = d.find(2);
 *      int z = d.find(3);
 *              // x == z, but x != y
 *              
 *      int c = d.count();
 *      int s = d.size();
 *              // c == 3   (number of sets)
 *              // s == 5   (size of structure)
 * </pre>
 * <p>
 * <b>COSC 102, Colgate University</b>
 * <br>
 * Part of source code for the maze lab
 * 
 * @author  Vijay Ramachandran
 */
public class DisjointSets 
{

    // Array containing sizes
    private int[] s;

    /**
     * Initializes a new DisjointSets object with <tt>n</tt> initial singleton sets.
     * 
     * @param n     number of initial singleton sets
     */
    public DisjointSets(int n)
    {
        s = new int[n];
        
        for (int i = 0; i < n; i++)
            s[i] = -1;
    }

    /**
     * Returns the internal representation of the union-find data structure.
     * 
     * @return      a String containing the values of the array used to hold set pointers and sizes
     */
    public String toString()
    {
        String tmp = "{ ";
        
        for(int i = 0; i < s.length; i++)
            tmp += s[i] + ", ";
            
        return tmp + " }";
    }
    
    // Prints index, size/parent pairs to stdout
    /**
     * Prints debugging information to stdout.
     * For each element of the array, its index and value are printed on a separate line.
     */
    public void Dump()
    {
        for(int i = 0; i < s.length; i++)
            System.out.println(i + "\t" + s[i]);
    }
    
    /**
     * Implements find with full path compression.
     * Returns an integer representing the set containing <tt>x</tt>.
     * If <i>i</i> and <i>j</i> are in the same set, then
     * <tt>find(i)==find(j)</tt>.
     * 
     * @param x     the item whose container set to find
     * @return      a representative element for the set containing <tt>x</tt>
     * @throws IndexOutOfBoundsException    if <tt>x &lt; 0</tt> or <tt>x &gt;= size()</tt>
     */
    public int find(int x)
    {
        // if x is out of bounds, throw an exception
        if (x < 0 || x >= s.length)
            throw new IndexOutOfBoundsException("DisjointSets find() called with out-of-bounds element");
                
        // 
        if (s[x] < 0) return x;
        else return s[x] = find(s[x]);
    }
    
    /**
     * Implements weighted union by size.
     * Joins the sets containing <tt>x</tt> and <tt>y</tt>, such that subsequent find operations on any elements
     * in the sets containing <tt>x</tt> and <tt>y</tt> will return the same value.
     * 
     * @param x     item whose set should be joined
     * @param y     item whose set should be joined
     * @return      the size of the unioned set, or <tt>-1</tt> if <tt>x</tt> and <tt>y</tt> are already in the same set
     */
    public int union(int x, int y)
    {
        // get the roots of the elements now
        int[] r = { find(x), find(y) };
        
        // if the elements are in the same set already,
        // no need to perform the union
        if (r[0] == r[1])
            return -1;
        
        // compare sizes;  join the smaller set to the larger
        // remember that sizes are stored as negative numbers!
        
        // set l to be the index of the larger set
        // then the smaller set is l XOR 1
        int l = (s[r[0]] < s[r[1]]) ? 0 : 1;
        
        s[r[l]] += s[r[l^1]];   // update size of larger set
        s[r[l^1]] = r[l];       // make smaller point to larger
        
        return -s[r[l]];        // return the size of the set
    }

    /**
     * Returns the size of the data structure (provided at the time of construction).
     * Unlike <tt>count()</tt>, the size of the structure does not change once created.
     * 
     * @return  the number of original singleton sets
     */
    public int size()
    {
        return s.length;
    }
    
    /**
     * Returns the number of disjoint sets currently in the data structure.
     * Each successful union operation reduces the number of disjoint sets
     * by one.
     * 
     * @return  the number of disjoint sets
     */
    public int count()
    {
		int c = 0;
		for (int r : s)
			if (r < 0)
				c++;
		
		return c;
    }
}
