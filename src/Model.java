import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Model {
	String header = "";
	ArrayList<Vertex> vertices = new ArrayList<Vertex>();
	ArrayList<Face> faces = new ArrayList<Face>();
	int numberOfVertices = 0;
	int numberOfFaces = 0;

	Model() {
	}

	public boolean read(String input_file_name) {
		File input_file = new File(input_file_name);
		boolean reading_header = true;
		int vertex_count = 0;
		int face_count = 0;
		try {
			Scanner input = new Scanner(input_file);
			while (input.hasNext()) {
				if (reading_header) {
					String next = input.nextLine();
					header += next + "\n";
					if (next.equals("end_header"))
						reading_header = false;
					if (next.contains("element vertex"))
						numberOfVertices = Integer.valueOf(next.split("\\s+")[2]);
					if (next.contains("element face"))
						numberOfFaces = Integer.valueOf(next.split("\\s+")[2]);
				} else {
					if (vertex_count < numberOfVertices) {
						Vertex input_vertex = new Vertex(input.nextDouble(), input.nextDouble(), input.nextDouble());
						vertices.add(input_vertex);
						vertex_count++;
					} else if (face_count < numberOfFaces) {
						String line = input.nextLine();
						if (!line.isEmpty()) {
							ArrayList<Integer> face_vertices = new ArrayList<Integer>();
							for (String s : line.split("\\s"))
								face_vertices.add(Integer.valueOf(s));
							if (!face_vertices.isEmpty()) {
								Face input_face = new Face(face_vertices);
								faces.add(input_face);
								face_count++;
							}
						}
					} else
						input.nextLine();
				}
			}
			input.close();
		} catch (FileNotFoundException e) {
			System.err.println("Problem Read file: " + input_file_name);
			return false;
		}
		return true;
	}

	@SuppressWarnings("resource")
	public boolean write(String outputFileName, String nameModifier) {
		if (nameModifier.length() > 0)
			nameModifier = "_" + nameModifier;
		String[] fileSplit = outputFileName.split("\\.");
		String newFileName = "";
		for (int i = 0; i < fileSplit.length - 1; i++) {
			newFileName += fileSplit[i];
		}
		newFileName += nameModifier + "." + fileSplit[fileSplit.length - 1];
		try {
			PrintWriter output = new PrintWriter(newFileName);
			switch (fileSplit[fileSplit.length - 1]) {
			case "ply":
				output.print(header);
				for (Vertex vertex : vertices)
					output.println(vertex);
				for (Face face : faces)
					output.println(face);
				break;
			case "ppm":
				output.print("unimplemented ppm");
				break;
			default:
				return false;
			}
			output.close();
		} catch (FileNotFoundException e) {
			System.err.println("Problem Writing file: " + newFileName);
		}
		return true;
	}

	public boolean center(String outputFileName) {
		Vertex average = getAverageVertex();
		for (Vertex vertex : vertices) {
			vertex.subtract(average);
		}
		return write(outputFileName, "centered");
	}

	public boolean whiten(String outputFileName) {
		Vertex scalar = getStandardDeviationVertex();
		for (Vertex vertex : vertices)
			vertex.divide(scalar);
		return write(outputFileName, "rounded");
	}

	public void printConsoleInfo(String command) {
		DecimalFormat df = new DecimalFormat("#.######");
		Vertex minVertex = getMinVertex();
		Vertex maxVertex = getMaxVertex();
		Vertex standardDeviation = getStandardDeviationVertex();
		System.out.println("=== " + command);
		System.out.println(numberOfVertices + " vertices, " + numberOfFaces + " polygons");
		System.out.println("Mean Vertex = " + getAverageVertex().toStringVertex());
		System.out.println("Bounding Box: " + df.format(minVertex.getX()) + " <= x <= " + df.format(maxVertex.getX())
				+ ", " + df.format(minVertex.getY()) + " <= y <= " + df.format(maxVertex.getY()) + ", "
				+ df.format(minVertex.getZ()) + " <= z <= " + df.format(maxVertex.getZ()));
		System.out.println("Standard Deviations: x = " + df.format(standardDeviation.getX()) + ", y = "
				+ df.format(standardDeviation.getY()) + ", z = " + df.format((standardDeviation.getZ())));
	}

	private Vertex getAverageVertex() {
		Vertex average = new Vertex();
		Vertex divisor = new Vertex(numberOfVertices, numberOfVertices, numberOfVertices);
		for (Vertex vertex : vertices)
			average.add(vertex);
		average.divide(divisor);
		return average;
	}

	private Vertex getMinVertex() {
		ArrayList<Double> xValues = new ArrayList<Double>();
		ArrayList<Double> yValues = new ArrayList<Double>();
		ArrayList<Double> zValues = new ArrayList<Double>();
		for (Vertex v : vertices) {
			xValues.add(v.getX());
			yValues.add(v.getY());
			zValues.add(v.getZ());
		}
		Collections.sort(xValues);
		Collections.sort(yValues);
		Collections.sort(zValues);
		return new Vertex(xValues.get(0), yValues.get(0), zValues.get(0));
	}

	private Vertex getMaxVertex() {
		ArrayList<Double> xValues = new ArrayList<Double>();
		ArrayList<Double> yValues = new ArrayList<Double>();
		ArrayList<Double> zValues = new ArrayList<Double>();
		for (Vertex v : vertices) {
			xValues.add(v.getX());
			yValues.add(v.getY());
			zValues.add(v.getZ());
		}
		Collections.sort(xValues);
		Collections.sort(yValues);
		Collections.sort(zValues);
		return new Vertex(xValues.get(xValues.size() - 1), yValues.get(yValues.size() - 1),
				zValues.get(zValues.size() - 1));
	}

	private Vertex getStandardDeviationVertex() {
		Vertex average = getAverageVertex();
		ArrayList<Vertex> sdVertices = new ArrayList<Vertex>();
		for (int i = 0; i < vertices.size(); i++) {
			double x = Math.pow((vertices.get(i).getX() - average.getX()), 2);
			double y = Math.pow((vertices.get(i).getY() - average.getY()), 2);
			double z = Math.pow((vertices.get(i).getZ() - average.getZ()), 2);
			sdVertices.add(new Vertex(x, y, z));
		}
		Vertex standard_deviation = new Vertex();
		Vertex divisor = new Vertex(numberOfVertices, numberOfVertices, numberOfVertices);
		for (Vertex vertex : sdVertices)
			standard_deviation.add(vertex);
		standard_deviation.divide(divisor);
		standard_deviation.squareRoot();
		return standard_deviation;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String newHeader) {
		header = newHeader;
	}

	public void setDefaultHeader() {
		header = "ply\nformat ascii 1.0\nelement vertex " + numberOfVertices
				+ "\nproperty float32 x\nproperty float32 y\nproperty float32 z\nelement face " + numberOfFaces
				+ "\nproperty list uint8 int32 vertex_indices\nend_header\n";
	}

	public ArrayList<Vertex> getVertices() {
		return vertices;
	}

	public void addVertex(Vertex vertex) {
		vertices.add(vertex);
	}

	public void removeVertex(Vertex vertex) {
		vertices.remove(vertex);
	}

	public ArrayList<Face> getFaces() {
		return faces;
	}

	public void addFace(Face face) {
		faces.add(face);
	}

	public void removeFace(Face face) {
		faces.remove(face);
	}

	public int getNumberOfVertices() {
		return numberOfVertices;
	}

	public void setNumberOfVertices(int newNumberOfVertices) {
		numberOfVertices = newNumberOfVertices;
	}

	public int getNumberOfFaces() {
		return numberOfFaces;
	}

	public void setNumberOfFaces(int newNumberOfFaces) {
		numberOfFaces = newNumberOfFaces;
	}
}
