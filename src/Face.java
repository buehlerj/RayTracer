import java.util.ArrayList;

public class Face {
	int numberOfVertices = 0;
	ArrayList<Integer> vertexIndices;

	Face(ArrayList<Integer> vertexIndices) {
		this.numberOfVertices = vertexIndices.get(0);
		this.vertexIndices = vertexIndices;
		this.vertexIndices.remove(0);
	}

	Face() {
		vertexIndices = new ArrayList<>();
	}

	public int getNumberOfVertices() {
		return numberOfVertices;
	}

	public void setNumberOfVertices(int newNumberOfVertices) {
		numberOfVertices = newNumberOfVertices;
	}

	public ArrayList<Integer> getVertexIndices() {
		return vertexIndices;
	}

	public void addToVertexIndices(int newVertexIndex) {
		vertexIndices.add(newVertexIndex);
		numberOfVertices++;
	}

	public void removeFromVertexIndices(int removeIndex) {
		vertexIndices.remove(removeIndex);
		numberOfVertices--;
	}

	public String toString() {
		String faces = "";
		faces += numberOfVertices + " ";
		for (int index : vertexIndices)
			faces += index + " ";
		return faces;
	}
}
