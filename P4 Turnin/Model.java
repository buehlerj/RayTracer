import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import Jama.Matrix;

public class Model {
	String header = "";
	String extension;
	String materialName;
	Material material;
	String materialLibraryFile;
	ArrayList<Vertex> vertices = new ArrayList<Vertex>();
	ArrayList<Face> faces = new ArrayList<Face>();
	int numberOfVertices = 0;
	int numberOfFaces = 0;
	Matrix translation;
	Matrix axisAngleRotation;

	public Model() {
	}

	public Model(double x, double y, double z, double Rx, double Ry, double Rz, double Rw) {
		translation = new Matrix(new double[][] { { x }, { y }, { z } });
		axisAngleRotation = new Matrix(new double[][] { { Rx }, { Ry }, { Rz }, { Rw } });
	}

	public boolean read(String inputFileName) {
		int extensionPeriodIndex = inputFileName.lastIndexOf('.') + 1;
		extension = inputFileName.substring(extensionPeriodIndex, inputFileName.length());
		File inputFile = new File(inputFileName);
		try {
			Scanner input = new Scanner(inputFile);
			switch(extension) {
			case "ply":
				boolean readingHeader = true;
				int vertexCount = 0;
				int faceCount = 0;
				while (input.hasNext()) {
					if (readingHeader) {
						String next = input.nextLine();
						header += next + "\n";
						if (next.equals("end_header"))
							readingHeader = false;
						if (next.contains("element vertex"))
							numberOfVertices = Integer.valueOf(next.split("\\s+")[2]);
						if (next.contains("element face"))
							numberOfFaces = Integer.valueOf(next.split("\\s+")[2]);
					} else {
						if (vertexCount < numberOfVertices) {
							Vertex inputVertex = new Vertex(input.nextDouble(), input.nextDouble(), input.nextDouble());
							vertices.add(inputVertex);
							vertexCount++;
						} else if (faceCount < numberOfFaces) {
							String line = input.nextLine();
							if (!line.isEmpty()) {
								ArrayList<Integer> faceVertices = new ArrayList<Integer>();
								for (String s : line.split("\\s"))
									faceVertices.add(Integer.valueOf(s));
								if (!faceVertices.isEmpty()) {
									Face inputFace = new Face(faceVertices);
									faces.add(inputFace);
									faceCount++;
								}
							}
						} else
							input.nextLine();
					}
				}
				input.close();
				return true;
			case "obj":
				while (input.hasNext()) {
					String next = input.next();
					switch(next) {
					case "#":
						break;
					case "mtllib":
						materialLibraryFile = input.next();
						break;
					case "o":
						break;
					case "v":
						Vertex inputVertex = new Vertex(input.nextDouble(), input.nextDouble(), input.nextDouble());
						vertices.add(inputVertex);
						break;
					case "usemtl":
						materialName = input.next();
						break;
					case "f":
						Face inputFace = new Face();
						String line = input.nextLine();
						for (String s : line.split("\\s")) {
							String vertexIndex = s.split("//")[0];
							if (!vertexIndex.isEmpty())
								inputFace.addToVertexIndices(Integer.valueOf(s.split("//")[0]) - 1);
						}
						faces.add(inputFace);
						break;
					}
				}
				input.close();
				return true;
			}
			input.close();
		} catch (FileNotFoundException e) {
			System.err.println("Problem Read file: " + inputFileName);
			return false;
		}
		return false;
	}

	public ArrayList<Material> readMaterials() {
		ArrayList<Material> materials = new ArrayList<>();
		if (!materialLibraryFile.isEmpty()) {
			Material inputMaterial = null;
			File inputMaterialFile = new File(materialLibraryFile);
			Scanner materialInput;
			try {
				materialInput = new Scanner(inputMaterialFile);
				while (materialInput.hasNext()) {
					String next = materialInput.next();
					switch(next) {
					case "#":
						next = materialInput.nextLine();
						break;
					case "newmtl":
						inputMaterial = new Material(materialInput.next(), this);
						materials.add(inputMaterial);
						break;
					case "Ns":
						inputMaterial.setNs(materialInput.nextDouble());
						break;
					case "Ka":
						inputMaterial.setKa(materialInput.nextDouble(), materialInput.nextDouble(), materialInput.nextDouble());
						break;
					case "Kd":
						inputMaterial.setKd(materialInput.nextDouble(), materialInput.nextDouble(), materialInput.nextDouble());
						break;
					case "Ks":
						inputMaterial.setKs(materialInput.nextDouble(), materialInput.nextDouble(), materialInput.nextDouble());
						break;
					case "d":
						inputMaterial.setD(materialInput.nextDouble());
						break;
					case "illum":
						inputMaterial.setIllum(materialInput.nextDouble());
						break;
					}
				}
				materialInput.close();
			} catch (FileNotFoundException e) {
				System.err.println("Problem Read file: " + materialLibraryFile);
				return null;
			}
		}
		for (Material m : materials) {
			if (m.getName().equals(materialName)) {
				material = m;
			}
		}
		return materials;
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
		Vertex standardDeviation = new Vertex();
		Vertex divisor = new Vertex(numberOfVertices, numberOfVertices, numberOfVertices);
		for (Vertex vertex : sdVertices)
			standardDeviation.add(vertex);
		standardDeviation.divide(divisor);
		standardDeviation.squareRoot();
		return standardDeviation;
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

	public void swapYZ(){
		for (Vertex v : vertices) {
			double y = v.getY();
			double z = v.getZ();
			v.setZ(y);
			v.setY(z * -1);
		}
	}

	public String getMaterialName() {
		return materialName;
	}

	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public void rotateModel() {
		for (Vertex v : vertices) {
			double y = v.getY();
			double z = v.getZ();
			v.setY(z * -1);
			v.setZ(y);
		}
	}
}
