import java.awt.Color;

public class Bullet extends BallEntity {

	public int bounces;
	
	public Bullet(PhysicsEngine e) {
		super(e);
		c = new Color(255, 0, 0);
		entityType = "Bullet";
		bounces = 3;
		//mass = 100.0;
	}

	
	@Override
	public void collideWith(PhysicsEntity e) {
	
		super.collideWith(e);
		
		if (e.isCollidable) {

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
