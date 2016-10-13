import java.util.ArrayList;

public class Face {
	ArrayList<Integer> vertex_indices = new ArrayList<Integer>();

	Face(ArrayList<Integer> vertex_indices) {
		this.vertex_indices = vertex_indices;
	}

	public String toString() {
		String faces = "";
		for (int index : vertex_indices)
			faces += index + " ";
		return faces;
	}
}
