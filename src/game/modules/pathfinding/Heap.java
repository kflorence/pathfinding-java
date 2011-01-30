package game.modules.pathfinding;

import java.util.ArrayList;

// The Heap class
public final class Heap
{
    ArrayList<Node> heapList;
    
    // Constructor
    public Heap() {
        heapList = new ArrayList<Node>();
    }
    
    public int indexOf(Node node) {
        return heapList.indexOf(node);
    }
    
    // Grab the last element 
    public int lastElement() {
        return heapList.size() - 1;
    }
    
    // The current size of the list
    public int size() {
        return heapList.size();
    }
    
    // Grab the Node as (int) pos
    public Node get(int pos) {
        return heapList.get(pos);
    }
    
    // See if Node is in the list
    public boolean contains(Node node) {
        return heapList.contains(node);
    }
    
    // Add an item to the end of the list
    public void add(Node node) {
        heapList.add(node);
    }
    
    // Add an item to pos in list
    public void add(int pos, Node node) {
        heapList.add(pos, node);
    }
    
    // Remove an item from the list
    public void remove(Node node) {
        this.remove(heapList.indexOf(node));
    }
    
    // Remove the top item from the list
    public void remove(int pos)
    {
        Node node;
        int child, parent, newParent = pos;
        
        // Start by moving the last value in the list to the position removed
        // then decrease the number of Nodes in our list.
        heapList.set(pos, heapList.get(lastElement()));
        heapList.remove(lastElement());
        
        // Resort the list by comparing the new parent Node with it's child Nodes            
        while (true)
        {
            // Set the parent as the current lowest totalCost Node
            parent = newParent;
            
            // Check to see if the item has child Nodes
            if ((child = (2 * parent)) <= lastElement())
            {                
                // See if the first child Node has a lower totalCost than it's parent
                if (heapList.get(child).totalCost() <= heapList.get(parent).totalCost()) newParent = child;
                
                // Check for another child Node
                if ((child = (2 * parent + 1)) <= lastElement())
                {                    
                    // See if the second child Node has a lower totalCost than it's parent
                    if (heapList.get(newParent).totalCost() >= heapList.get(child).totalCost()) newParent = child;
                }
            }
            
            // If the parent Node has a child Node with a lower totalCost, swap them
            if (parent != newParent)
            {
                node = heapList.get(parent);
                heapList.set(parent, heapList.get(newParent));
                heapList.set(newParent, node);
            }
            
            // The parent Node has a lower totalCost than all it's children
            else break;
        }
    }
    
    // Add an item to the list in it's correct, sorted location
    public void push(Node node)
    {
    	int n;
        Node temp;
        
        // Add our new Node to the list
        add(node);
        
        // Store a reference to the last element on the list
        n = lastElement();
        
        // Keep sorting until the new Node reaches the top of the list or we exit the loop
        while (n > 1)
        {        	
        	// If the child Node has a lower totalCost than it's parent, swap them
            if (heapList.get(n).totalCost() <= heapList.get(n / 2).totalCost())
            {            	
                temp = heapList.get(n / 2);
                heapList.set((n / 2), heapList.get(n));
                heapList.set(n, temp);
                
                // Cut n in half and try again
                n = (n / 2);
            }
            
            // Parent node has a lower totalCost than child, exit loop
            else break;
        }
    }
    
    // Return the item at the top of the list and remove it
    public Node pop()
    {
        Node node = heapList.get(0);
        
        // Make sure Node exists
        if (node != null)
        {        	
        	// If there is more than one item left, resort our list
        	if (lastElement() > 0) remove(0);
            
            // If theres only one item in our list, just remove it (no need to resort)
            else heapList.remove(0);
        }
        
    	// Return the node we removed (or null if node doesn't exist)
        return node;
    }
}
