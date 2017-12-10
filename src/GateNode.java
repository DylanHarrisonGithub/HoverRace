import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;


public class GateNode extends WallEntity {

	public Point2D.Double midPoint;
	
	public GateNode(PhysicsEngine e, Point2D.Double p1, Point2D.Double p2) {
		super(e, p1.x, p1.y, p2.x, p2.y, new Color(255, 255, 0));
		midPoint = new Point2D.Double();
		establishMidPoint();
		isCollidable = false;
		
	}
	
	public void establishMidPoint() {
		
		Point2D.Double head = wall.getHead();
		midPoint.x = (head.x + wall.origin.x) * 0.5;
		midPoint.y = (head.y + wall.origin.y) * 0.5;
		
		
	}
	
	@Override
	public void draw(Graphics g, CartesianPlane p) {

		wall.plot(g, p);
		p.plotGraphCircleEmpty(g, midPoint, wall.magnitude * .05, wall.c);
		
	}	
	
}
