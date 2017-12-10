import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public abstract class PhysicsEntity {
	
	private Point2D.Double origin;
	private double mass;
	private double theta;
	private double angularVelocity;
	private double angularAcceleration;
	//private Vector velocity;
	private Vector acceleration;
	private Vector appliedForce;
	private boolean robustDraw = true;
	private boolean isVisible = true;
	public boolean isCollidable = true;
	public boolean isReactive;
	public PhysicsEngine myEngine;
	public ArrayList<String> status;
	protected int serialID;
	public int baseLogLength;
	public String entityType;
	
	
	public abstract void computeAcceleration(double t);
	public abstract void computeVelocity(double t);
	public abstract void move(double t);
	public abstract double getCollisionTime(PhysicsEntity e);
	public abstract void collideWith(PhysicsEntity e);
	public abstract void draw(Graphics g, CartesianPlane p);
	public abstract void generateLog();
	
}
