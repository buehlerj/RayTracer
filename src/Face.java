import java.util.ArrayList;

public class Face {
	ArrayList<Integer> vector_indices = new ArrayList<Integer>();

	Face(ArrayList<Integer> vector_indices) {
		this.vector_indices = vector_indices;
	}

	public String toString() {
		String faces = "";
		for (int index : vector_indices)
			faces += index + " ";
		return faces;
	}
}
