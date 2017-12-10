import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class WallEntity extends PhysicsEntity {

	public Color c;
	public Vector wall;
	
	public WallEntity(PhysicsEngine myEngine) {
		
		c = new Color(0, 0, 0);
		wall = new Vector(0, 0, 0, 1.0, c);
		isReactive = false;
		this.myEngine = myEngine;
		this.status = new ArrayList<String>();
		baseLogLength = 6;
		entityType = "WallEntity";
		myEngine.log("Entity: " + entityType +" Created, ID: " + myEngine.numEntities);
		
	}
	
	public WallEntity(PhysicsEngine myEngine, double x1, double y1, double x2, double y2, Color c) {
		
		wall = new Vector(new Point2D.Double(x1, y1), new Point2D.Double(x2, y2), c);
		isReactive = false;
		this.myEngine = myEngine;
		this.status = new ArrayList<String>();
		baseLogLength = 6;
		entityType = "WallEntity";
		myEngine.log("Entity: " + entityType +" Created, ID: " + myEngine.numEntities);
		
	}
	
	@Override
	public void computeAcceleration(double t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void computeVelocity(double t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void move(double t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getCollisionTime(PhysicsEntity e) {
		// TODO Auto-generated method stub
		return -1.0;
	}

	@Override
	public void collideWith(PhysicsEntity e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw(Graphics g, CartesianPlane p) {

		wall.plot(g, p);
		
	}

	@Override
	public void generateLog() {

		status.add(0, "PhysicsEntity: WallEntity");
		status.add(1, "ID: " + serialID);
		status.add(2, "x1: " + wall.origin.x);
		status.add(3, "y1: " + wall.origin.y);
		status.add(4, "x2: " + wall.getComponentX() + wall.origin.x);
		status.add(5, "y2: " + wall.getComponentY() + wall.origin.y);
		
	}

	
}
