import java.util.LinkedList;
import java.util.List;

class QueueElement{
	int destiantion;
	int source;
	QueueElement(int d, int s){
		destiantion = d;
		source = s;
	}
}

public class Solution {
	public static void main(String input[]) {
		System.out.println(solution(new int[]{0}, new int[]{3},new int[][]{{0, 7, 0, 0}, {0, 0, 6, 0}, {0, 0, 0, 8}, {9, 0, 0, 0}}));
		
		System.out.println(solution(new int[]{0,1}, new int[]{4,5},new int[][]{
			{0, 0, 4, 6, 0, 0}, 
			{0, 0, 5, 2, 0, 0},
			{0, 0, 0, 0, 4, 4},
			{0, 0, 0, 0, 6, 6},
			{0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0}}));
	}
	
	public static int solution(int[] entrances, int[] exits, int[][] path) {
		int nodesNum = path.length + 2; // add one input and one output
		int startIdx = nodesNum - 2;
		int endIdx = nodesNum - 1;
		
		int[][] maxCapacity = new int[nodesNum][nodesNum];
		
		for (int xIdx = 0; xIdx < path.length; ++xIdx) {
			for (int yIdx = 0; yIdx < path.length; ++yIdx) {
				maxCapacity[xIdx][yIdx] = path[xIdx][yIdx];
			}
		}
		
		// set capacity for edges from old outputs to new outputs
		for (int idx = 0; idx < exits.length; ++idx) {
			maxCapacity[exits[idx]][endIdx] = Integer.MAX_VALUE;
		}
		
		// set capacity for edges from new input to old inputs
		for (int idx = 0; idx < entrances.length; ++idx) {
			maxCapacity[startIdx][entrances[idx]] = Integer.MAX_VALUE;
		}
		
		int[][] usedCapacity = new int[nodesNum][nodesNum];
		
		LinkedList<Integer> latestPath = findNewPath(usedCapacity, maxCapacity, startIdx, endIdx);
		while(!latestPath.isEmpty()) {
			int flow = findHighestFlow(latestPath, usedCapacity, maxCapacity);
			updateCapacity(flow, latestPath, usedCapacity);
			latestPath = findNewPath(usedCapacity, maxCapacity, startIdx, endIdx);
		}
		
		int result = 0;
		// sum flow from new input to old inputs for total number of minions 
		for (int idx = 0; idx < entrances.length; ++idx) {
			result += usedCapacity[nodesNum-2][entrances[idx]];
		}
		
		return result;
	}

	private static void updateCapacity(int flow, List<Integer> latestPath, int[][] usedCapacity) {
		int lastIdx = -1;
		for (int index : latestPath) {
            if (lastIdx == -1) {
            	lastIdx = index;
            	continue;
            };
            usedCapacity[index][lastIdx] += flow;
    		lastIdx = index;
        }		
	}

	private static int findHighestFlow(List<Integer> latestPath, int[][] usedCapacity, int[][] maxCapacity) {
		int highestCapacity = Integer.MAX_VALUE; 
		int lastIdx = -1;
		for (int index : latestPath) {
            if (lastIdx == -1) {
            	lastIdx = index;
            	continue;
            };
            int uc = usedCapacity[index][lastIdx];
            int mc = maxCapacity[index][lastIdx];
            int rc = usedCapacity[lastIdx][index];
            
            int capacity = mc - uc - rc;
            if (capacity < highestCapacity) highestCapacity = capacity;

    		lastIdx = index;
        }
		return highestCapacity;
	}


	private static LinkedList<Integer> findNewPath(int[][] usedCapacity, int[][] maxCapacity, int startIdx, int endIdx) {
		int[] source= new int[endIdx + 1];
		for (int idx = 0; idx <= endIdx; ++idx) {
			source[idx] = -1;
		}
		
		LinkedList<QueueElement> queue = new LinkedList<>();
		queue.add(new QueueElement(startIdx, startIdx));
		
		boolean found = false;
		
		while(!queue.isEmpty()) {
			QueueElement currentElement = queue.pollFirst();
			int currentIdx = currentElement.destiantion;
			if (source[currentIdx] != -1) continue; // already visited
			
			source[currentIdx] = currentElement.source;
			
			if (currentIdx == endIdx) {
				found = true;
			}
			
			for (int neighbourIdx = 0; neighbourIdx <= endIdx; ++neighbourIdx) {
				if(usedCapacity[currentIdx][neighbourIdx] < maxCapacity[currentIdx][neighbourIdx] ||
						usedCapacity[neighbourIdx][currentIdx] < 0
						) {
					queue.add(new QueueElement(neighbourIdx, currentIdx));
				}
			}
		}
		
		LinkedList<Integer> result = new LinkedList<>();
		if (found) {
			int currentIdx = endIdx;
			while (currentIdx != startIdx) {
				result.add(currentIdx);
				currentIdx = source[currentIdx];
			}
			result.add(startIdx);
		}
		return result;
	}
}
