package app;

import java.util.Iterator;

import structures.*;

public class Driver {

	public static void main(String[] args) {
		String fileName = "graph2.txt";
		
		Graph testGraph = null;
		try { 
			testGraph = new Graph(fileName);
		}
		catch (Exception e) {
			System.out.println("Took an L on file-reading");
		}
		
		PartialTreeList pt1 = PartialTreeList.initialize(testGraph);
		Iterator<PartialTree> iter = pt1.iterator();
		while (iter.hasNext()) {
		    System.out.println(iter.next());
		}
		
	}
}
