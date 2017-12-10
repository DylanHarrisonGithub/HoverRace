import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class BallEntity extends PhysicsEntity {

	protected Color c;
	public double radius;
	public Vector velocity;
	public Vector acceleration;
	public Point2D.Double origin;
	protected double mass;

	
	public BallEntity(PhysicsEngine myEngine) {
		
		entityType = "BallEntity";
		c = new Color(0,0,0);
		radius = 1;
		origin = new Point2D.Double(0, 0);
		velocity = new Vector();
		velocity.magnitude = 1.0;
		acceleration = new Vector();
		acceleration.magnitude = 0.0;
		isReactive = true;
		this.myEngine = myEngine;
		this.status = new ArrayList<String>();
		mass = 1.0;
		baseLogLength = 6;
		myEngine.log("Entity: " + entityType +" Created, ID: " + myEngine.numEntities);
		
	}
	
	public BallEntity(PhysicsEngine myEngine, Point2D.Double origin, double radius, double mass, Vector velocity, Color c) {
		
		entityType = "BallEntity";
		this.origin = origin;
		this.radius = radius;
		this.velocity = velocity;
		acceleration = new Vector();
		acceleration.magnitude = 0.0;
		this.velocity.origin = this.origin;
		this.c = c;
		isReactive = true;
		this.myEngine = myEngine;
		this.status = new ArrayList<String>();
		this.mass = mass;
		baseLogLength = 6;
		myEngine.log("Entity: " + entityType +" Created, ID: " + myEngine.numEntities);

	}
	
	@Override
	public void computeAcceleration(double t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void computeVelocity(double t) {
		// TODO Auto-generated method stub
		Vector deltaVelocity = new Vector(new Point2D.Double(0,0), 
				new Point2D.Double(acceleration.getComponentX() * t,
				acceleration.getComponentY() * t), 
				new Color(255, 255, 255));
		
		velocity.AddVec(deltaVelocity);
		
		acceleration.magnitude = 0;
		
	}

	@Override
	public void move(double t) {

		Point2D.Double vHead = new Point2D.Double(velocity.getComponentX(), velocity.getComponentY());
		
		origin.x += vHead.x * t;
		origin.y += vHead.y * t;
		velocity.origin = origin;
		
	}

	@Override
	public double getCollisionTime(PhysicsEntity e) {
		
		double tempReturn = -1.0;
		
		if (e instanceof WallEntity) {
			
			tempReturn = getWallCollisionTime((WallEntity) e);
			//System.out.println("wall: " + tempReturn);
			
		} else if (e instanceof BallEntity) {
			
			tempReturn = getBallCollisionTime((BallEntity) e);
			//System.out.println("ball: " + tempReturn);
			
		}	
		
		
		return tempReturn;
	}
	
	private double getBallCollisionTime(BallEntity b) {
		
		Point2D.Double deltaV = new Point2D.Double(
				b.velocity.getComponentX() - velocity.getComponentX(), 
				b.velocity.getComponentY() - velocity.getComponentY()); 
		Point2D.Double deltaO = new Point2D.Double(
				b.origin.x - origin.x, 
				b.origin.y - origin.y);
		double A;
		double B;
		double C;
		double Eradius = radius + b.radius;
		
		A = (deltaV.x * deltaV.x) + (deltaV.y * deltaV.y);
		B = 2 * ( (deltaO.x * deltaV.x) + (deltaO.y * deltaV.y) );
		C = (deltaO.x * deltaO.x) + (deltaO.y * deltaO.y) - (Eradius * Eradius);
		
		if (A == 0.0) {
			
			return -1.0;
			
		} else {
				
			double descriminant = (B*B) - (4*A*C);
		
			if (descriminant >= 0.0) {

				if ((B >= 0.0) && (A > 0.0)) {
				
					return -1.0;
					
				} else {
							
					return ((-B - Math.sqrt(descriminant)) / (2 * A));
					
				}
			} else {
				
				return -1.0;
				
			}
		
		}
		
	}
	
	private double getWallCollisionTime(WallEntity w) {
		
		double tempReturn = -1.0;
		double pIntersect;
		Vector perpVec;

			
		perpVec = w.wall.getPerpendicularVec(origin, w.wall.c);
		pIntersect = w.wall.getParametricIntersection(perpVec);
		
		if ((pIntersect >= 0.0) && (pIntersect <= 1.0)) {
				
			tempReturn = (perpVec.magnitude - radius) / (velocity.magnitude * Math.cos(velocity.getAngleBetween2(perpVec))) ;
			
		}
		
		
		return tempReturn;
		
	}

	@Override
	public void collideWith(PhysicsEntity e) {

		if (e instanceof WallEntity) {
			
			if (e.isCollidable) {
				collideWithWall((WallEntity) e);
			}
			
		} else if (e instanceof BallEntity) {
			
			collideWithBall2((BallEntity) e);
			//velocity.rotate(Math.PI);
			//((BallEntity) e).velocity.rotate(Math.PI);
			
		}
		
	}
	
	public void collideWithWall(WallEntity w) {
		
		Vector x = w.wall.getPerpendicularVec(origin, new Color( w.wall.c.getRGB()) );
		//Vector y = new Vector(x.getHead(), velocity.getIntersectionPoint(w.wall), new Color( w.wall.c.getRGB()));
		Vector y = new Vector(x.getHead(), velocity.getParametricPoint(velocity.getParametricIntersection(w.wall)), new Color( w.wall.c.getRGB()));
		y.origin.x = origin.x;
		y.origin.y = origin.y;
		double t = velocity.getAngleBetween(x); //Math.abs(velocity.theta - x.theta);
		
		if (t > Vector.halfPi) {
			while (t > Vector.halfPi) {
				t -= Vector.halfPi;
			}
		}
		
		//System.out.println("angle: " + t * Vector.radiansToDegreesRatio);
		
		//myEngine.log("" + w.wall.getParametricIntersection(velocity)+ " 0: " + w.wall.theta);
		
		x.magnitude = velocity.magnitude * Math.cos(t);
		y.magnitude = velocity.magnitude * Math.sin(t);
		
		x.rotate(Math.PI);
		x.enforceAngleRules();
		//System.out.println(w.wall.getParametricIntersection(velocity));
		//System.out.println(velocity.getParametricIntersection(w.wall));
		//myEngine.additionalVectorRenderQueue.add(x);
		//myEngine.additionalVectorRenderQueue.add(y);
		//myEngine.additionalVectorRenderQueue.add(velocity.copy());
		
		velocity = Vector.addVecs(x, y);
		velocity.origin = origin;
		//myEngine.additionalVectorRenderQueue.add(velocity.copy());
		//velocity.magnitude = m;
		
	}
	
	public void collideWithBall(BallEntity b) {
		
		Vector thisToB = new Vector(this.origin, b.origin, new Color( b.c.getRGB(), false));
		Vector bToThis = new Vector(b.origin, this.origin, new Color(c.getRGB() , false));
		double thisCollisionAng = velocity.theta - thisToB.theta;
		double bCollisionAng = b.velocity.theta - bToThis.theta;
		double thisFinalMagnitude;
		double bFinalMagnitude;
		
		thisToB.magnitude = velocity.magnitude * Math.cos(thisCollisionAng);
		bToThis.magnitude = b.velocity.magnitude * Math.cos(bCollisionAng);		
		
		Vector thisYcomponent = new Vector(thisToB.getHead(), velocity.getHead(), new Color( 128, 128, 128));
		Vector bYcomponent = new Vector(bToThis.getHead(), b.velocity.getHead(), new Color( 128, 128, 128));

		thisYcomponent.origin = new Point2D.Double(origin.x, origin.y);
		bYcomponent.origin = new Point2D.Double(b.origin.x, b.origin.y);
		
		if (Math.abs(velocity.theta - b.velocity.theta) < Math.PI) {
			
			//thisFinalMagnitude = (thisToB.magnitude * (mass - b.mass)) / (mass + b.mass); 
			//bFinalMagnitude = (thisToB.magnitude * 2 * mass) / (mass + b.mass); 
			//bFinalMagnitude = ((mass - b.mass) * thisToB.magnitude + 2 * b.mass * bToThis.magnitude) / (mass + b.mass);
			bFinalMagnitude = (2 * mass * thisToB.magnitude + (mass - b.mass) * bToThis.magnitude) / (mass + b.mass);
			thisFinalMagnitude = (mass * (thisToB.magnitude - bFinalMagnitude) + b.mass * bToThis.magnitude) / b.mass;

			
		} else {
			//thisFinalMagnitude = (thisToB.magnitude * (mass - b.mass)) / (mass + b.mass); 
			//bFinalMagnitude = (thisToB.magnitude * 2 * mass) / (mass + b.mass); 

			myEngine.pause();
			thisFinalMagnitude = ((mass - b.mass) * thisToB.magnitude + 2 * b.mass * -bToThis.magnitude) / (mass + b.mass);
			bFinalMagnitude = (bToThis.magnitude * (mass - b.mass) + 2 * b.mass * -thisToB.magnitude ) / (mass + b.mass);
					//(2 * mass * thisToB.magnitude - (mass - b.mass) * bToThis.magnitude) / (mass + b.mass);
			//bFinalMagnitude = (mass * (thisToB.magnitude - thisFinalMagnitude) + b.mass * bToThis.magnitude) / b.mass;
		}
		
		status.add("" + Math.abs(velocity.theta - b.velocity.theta));
		
		thisToB.magnitude = thisFinalMagnitude;
		bToThis.magnitude = bFinalMagnitude;
		
		thisToB.rotate(Math.PI);
		bToThis.rotate(Math.PI);
		
		if (thisToB.magnitude < 0.0) {
			thisToB.magnitude = Math.abs(thisToB.magnitude);
			thisToB.rotate(Math.PI);
		}

		if (bToThis.magnitude < 0.0) {
			bToThis.magnitude = Math.abs(bToThis.magnitude);
			bToThis.rotate(Math.PI);
		}

		
		velocity.c = new Color(128, 128, 128);
		b.velocity.c  = new Color(128, 128, 128);
		myEngine.additionalVectorRenderQueue.add(velocity.copy());
		myEngine.additionalVectorRenderQueue.add(b.velocity.copy());
		velocity = Vector.addVecs(thisToB, thisYcomponent);
		b.velocity = Vector.addVecs(bToThis, bYcomponent);
		myEngine.additionalVectorRenderQueue.add(velocity.copy());
		myEngine.additionalVectorRenderQueue.add(b.velocity.copy());
		
		myEngine.additionalVectorRenderQueue.add(thisToB);
		myEngine.additionalVectorRenderQueue.add(bToThis);
		myEngine.additionalVectorRenderQueue.add(thisYcomponent);
		myEngine.additionalVectorRenderQueue.add(bYcomponent);

		//velocity = add()
		
		
	}
	
	public void collideWithBall2(BallEntity b) {
		
		double thisFinalMagnitude;
		double bFinalMagnitude;
		
		Vector CollisionAxis = new Vector(origin, b.origin, new Color(128, 128, 128));
		Vector thisPerpendicularVelocity = CollisionAxis.getPerpendicularVec(velocity.getHead(), new Color(128, 128, 128));
		thisPerpendicularVelocity = new Vector(thisPerpendicularVelocity.getHead(), thisPerpendicularVelocity.origin, new Color(128, 128, 128));
		Vector thisCollisionVec = new Vector(origin, thisPerpendicularVelocity.origin, new Color(0, 0, 128));
		thisPerpendicularVelocity.origin = new Point2D.Double(origin.x, origin.y);
		
		Vector bPerpendicularVelocity = CollisionAxis.getPerpendicularVec(b.velocity.getHead(), new Color(128,128,128));
		bPerpendicularVelocity = new Vector(bPerpendicularVelocity.getHead(), bPerpendicularVelocity.origin, new Color(128,128,128));
		Vector bCollisionVec = new Vector(b.origin, bPerpendicularVelocity.origin, new Color(0, 0, 128));
		bPerpendicularVelocity.origin = new Point2D.Double(b.origin.x, b.origin.y);
		
		
		if (Math.abs(thisCollisionVec.theta - bCollisionVec.theta) < 0.01) {
			
			//myEngine.pause();
			thisFinalMagnitude = (thisCollisionVec.magnitude * (mass - b.mass) + 2 * b.mass * bCollisionVec.magnitude) / (mass + b.mass);
			bFinalMagnitude = (bCollisionVec.magnitude * (b.mass - mass) + 2 * mass * thisCollisionVec.magnitude) / (mass + b.mass);
			//bFinalMagnitude = (mass * (thisCollisionVec.magnitude - thisFinalMagnitude) + b.mass * b.velocity.magnitude) / b.mass;
			thisCollisionVec.magnitude = thisFinalMagnitude;
			bCollisionVec.magnitude = bFinalMagnitude;
			
			velocity = Vector.addVecs(thisCollisionVec, thisPerpendicularVelocity);
			b.velocity = Vector.addVecs(bCollisionVec, bPerpendicularVelocity);
			
		} else {
			
			thisFinalMagnitude = (thisCollisionVec.magnitude * (mass - b.mass) + 2 * b.mass * bCollisionVec.magnitude) / (mass + b.mass);
			bFinalMagnitude = (bCollisionVec.magnitude * (b.mass - mass) + 2 * mass * thisCollisionVec.magnitude) / (mass + b.mass);
			//bFinalMagnitude = (mass * (thisCollisionVec.magnitude - thisFinalMagnitude) + b.mass * b.velocity.magnitude) / b.mass;

			thisCollisionVec.magnitude = thisFinalMagnitude;
			bCollisionVec.magnitude = bFinalMagnitude;
			
			thisCollisionVec.rotate(Math.PI);
			bCollisionVec.rotate(Math.PI);
			
			velocity = Vector.addVecs(thisCollisionVec, thisPerpendicularVelocity);
			b.velocity = Vector.addVecs(bCollisionVec, bPerpendicularVelocity);
			
		}
		
		//myEngine.additionalVectorRenderQueue.add(thisCollisionVec);
		//myEngine.additionalVectorRenderQueue.add(bCollisionVec);
		//myEngine.additionalVectorRenderQueue.add(thisPerpendicularVelocity);
		//myEngine.additionalVectorRenderQueue.add(bPerpendicularVelocity);
		//myEngine.additionalVectorRenderQueue.add(velocity.copy());
		//myEngine.additionalVectorRenderQueue.add(b.velocity.copy());
		//myEngine.pause();
		status.add("" + (thisCollisionVec.theta - bCollisionVec.theta));
		
	}

	@Override
	public void draw(Graphics g, CartesianPlane p) {
		// TODO Auto-generated method stub
		p.plotGraphCircle(g, origin, radius, c);
		//g.drawString("" + serialID, (int) p.graphToScreenCoords(origin).x,(int) p.graphToScreenCoords(origin).y);
		//velocity.plot(g, p);
		
	}


	@Override
	public void generateLog() {
		
		status.add(0, "PhysicsEntity: BallEntity");
		status.add(1, "ID: " + serialID);
		status.add(2, "x1: " + origin.x);
		status.add(3, "y1: " + origin.y);
		status.add(4, "velocity: " + velocity.magnitude);
		status.add(5, "heading: " + velocity.theta);
		
	}

}
