import java.awt.Color;

public class HeatSeekingBullet extends BallEntity {

	public int bounces;
	
	public HeatSeekingBullet(PhysicsEngine e) {
		super(e);
		c = new Color(255, 0, 0);
		entityType = "Bullet";
		bounces = 3;
		//mass = 100.0;
	}
	@Override
	public void computeVelocity(double t) {

		
		HoverCar hcptr = null;
		for (int i = 0; i < myEngine.entities.size(); i++) {
			if (myEngine.entities.get(i) instanceof HoverCar) {
				hcptr = (HoverCar) myEngine.entities.get(i);
			}
		}
		
		if (hcptr != null) {
			
			Vector a = new Vector(origin, hcptr.origin, new Color(255, 0, 0));
			a.magnitude = 1000.0;
			
			acceleration.AddVec(a);
			acceleration = a;
			
			//Vector v = new Vector(origin, hcptr.origin, new Color(255, 0, 0));
			//velocity.theta = v.theta;
			
		}
		
		super.computeVelocity(t);
	}
	
	@Override
	public void collideWith(PhysicsEntity e) {
		super.collideWith(e);
		
		if (e.isCollidable){
			bounces -= 1;
			
			if (e instanceof BallEntity) {
				if (!(e instanceof HoverCar)) {
					myEngine.entities.remove(e);
				}
				
			}
			
			if (bounces < 1) {
				myEngine.entities.remove(this);
			}
		}

		
	}
}