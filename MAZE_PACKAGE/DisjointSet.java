// For union-find algorithms

package MAZE_PACKAGE;

public class DisjointSet
{
	private int[] s;		// It's just an array!  (Treated in special ways.)

	
	// Constructor requires knowledge of size of desired set.
	public DisjointSet(int numElements)
	{
		s = new int[numElements];
		
		for ( int i = 0 ; i < s.length ; ++i)
			s[i] = -1;
	}

	
	// Find the root of a node's set
	public int find( int x )	
	{
		if( s[x] < 0)
			return x;
		else
			return s[x] = find(s[x]);	// This statement compresses the path.
	}
	
	
	// Union of two sets  !!! MUST USE ROOTS !!!
	public void unionSets ( int root1, int root2 )
	{
		if(s[root2] < s[root1])
			s[root1] = root2;
		else
		{
			if(s[root1] == s[root2])	// If both roots have the same... "degree"... then the path increased by 1.
				s[root1]--;
			s[root2] = root1;
		}
	}
	
	
	// See the entire set as it is internally represented.
	public String toString()
	{
		String outputStr = "";
		for( int i = 0 ; i < s.length ; i++)
		{
			outputStr += i + " -- " + s[i];
			outputStr += "\n";
		}
		return outputStr;
	}
	
	
	// Obvious usage
	public boolean isOneSet()
	{
		int rootCount = 0;
		for(int i = 0 ; i < s.length ; i++)
		{
			if(s[i] < 0)
				++rootCount;
			if(rootCount > 1)
				return false;
		}
		return true;
	}
	
	
	// For testing
	public static void main(String[] args)
	{
		DisjointSet mazeset = new DisjointSet(10);
		
		mazeset.unionSets(5,3);
		mazeset.unionSets(5,2);
		mazeset.unionSets(4,1);
		mazeset.unionSets(4,5);		// Remember to use find() if you don't know the root of a set.
		System.out.println(mazeset);
		// find() for testing compression
		mazeset.find(2);
		mazeset.find(3);
		System.out.println(mazeset);
		
	}
}
