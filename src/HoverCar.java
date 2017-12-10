import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;


public class HoverCar extends BallEntity {

	public double orientation;
	public int health;
	public double accelerationRate;
	public double maxVelocity;
	public boolean isComputerControlled;
	public Track myTrack;
	public int targetGateNodeIndex;
	
	public HoverCar(PhysicsEngine e) {
		
		super(e);
		entityType = "HoverCar";
		health = 100;
		maxVelocity = 1000.0;
		accelerationRate = 1000.0;
		radius = 35.0;
		targetGateNodeIndex = 1;
		isComputerControlled = true;
		c = new Color(0,255,0);
		
	}
	
	@Override
	public void collideWith(PhysicsEntity e) {
		super.collideWith(e);
		
		if (e.isCollidable) {
			health -= 5;
			myEngine.log(entityType + " Health: " + health);
		} else {
			
			if (myTrack != null) {
				if (e == myTrack.gateNodes.get(targetGateNodeIndex)) {
					targetGateNodeIndex++;
					if (targetGateNodeIndex == myTrack.gateNodes.size()) {
						targetGateNodeIndex = 0;
					}
				}	
			}

			
		}

	}
	
	@Override
	public void computeVelocity(double t) {
		
		//acceleration.AddVec(new Vector(origin.x, origin.y, velocity.theta, 10.0, new Color(255, 255, 255)));
		if (isComputerControlled) {
			computerPilot();
		}
		super.computeVelocity(t);
		
		if (velocity.magnitude > maxVelocity) {
			//testCar.velocity.magnitude = testCar.maxVelocity;
			//acceleration.AddVec(new Vector(origin.x, origin.y, velocity.theta + Math.PI, velocity.magnitude - maxVelocity, new Color(255, 0, 0)));
			velocity.magnitude = maxVelocity;
		}
	}
	
	public void setTrack(Track t) {
		myTrack = t;
	}
	
	public void setAtTrackStartPosition() {
		
		if (myTrack != null) {
			
			Vector startPos = myTrack.getNextStartPosition();
			
			origin.x = startPos.origin.x;
			origin.y = startPos.origin.y;
			orientation = startPos.theta;
			
			velocity.theta = startPos.theta;
			
		}
	}
	
	public void computerPilot() {
		
		if (myTrack != null) {
			
			//myEngine.log("target node: " + targetGateNodeIndex);
			Vector a = new Vector(origin, myTrack.gateNodes.get(targetGateNodeIndex).midPoint, new Color(255, 255, 255));
			a.magnitude = accelerationRate;
			acceleration.AddVec(a);
			acceleration.magnitude = accelerationRate;
			orientation = velocity.theta;
			
		}
		
	}
	
	@Override
	public void draw(Graphics g, CartesianPlane p) {
		
		if (!isComputerControlled) {
			p.plotGraphCircleEmpty(g, origin, radius, Color.red);
		}
		
		Point2D.Double head = new Point2D.Double(origin.x + radius* Math.cos(orientation), origin.y + radius* Math.sin(orientation) );
		Point2D.Double tail1 = new Point2D.Double(origin.x + radius* Math.cos(orientation + Vector.threeQuartersPi), origin.y + radius* Math.sin(orientation + Vector.threeQuartersPi) );
		Point2D.Double tail2 = new Point2D.Double(origin.x + radius* Math.cos(orientation - Vector.threeQuartersPi), origin.y + radius* Math.sin(orientation - Vector.threeQuartersPi) );
		p.plotGraphLine(g, head, tail1, c);
		p.plotGraphLine(g, head, tail2, c);
		p.plotGraphLine(g, origin, tail1, c);
		p.plotGraphLine(g, origin, tail2, c);		
		//velocity.plot(g, p);		
		//acceleration.plot(g, p);
	}	
	
}
