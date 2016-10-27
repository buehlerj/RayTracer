
public class Ray {
	Vertex origin;
	Vertex direction;

	Ray(Vertex origin, Vertex direction) {
		this.origin = origin;
		this.direction = direction;
		this.direction.normalize();
	}

	public Vertex getOrigin() {
		return origin;
	}

	public void setOrigin(Vertex new_origin) {
		origin = new_origin;
	}

	public Vertex getDirection() {
		return direction;
	}

	public void setDirection(Vertex new_D) {
		direction = new_D;
	}

	public Vertex move (double t) {
		Vertex movement = new Vertex(direction);
		movement.scale(t);
		return origin.plus(movement);
	}
}
