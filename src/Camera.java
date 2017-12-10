import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Camera {

	public ArrayList<BallEntity> trackedEntities;
	private CartesianPlane myPlane;
	private int mode;
	public double chaseCameraModeZoom;
	public static final int FREE_CAMERA_MODE = 0;
	public static final int CHASE_CAMERA_MODE = 1;
	public static final int OVERVIEW_CAMERA_MODE = 2;
	
	public Camera(CartesianPlane p) {
		
		trackedEntities = new ArrayList<BallEntity>();
		myPlane = p;
		mode = OVERVIEW_CAMERA_MODE;
		chaseCameraModeZoom = 0.5;
		
		
	}
	
	public void setMode(int m) {
		
		switch (m) {
		
		case FREE_CAMERA_MODE:
			mode = m;
			break;
			
		case CHASE_CAMERA_MODE:
			mode = m;
			myPlane.setZoom(new Point2D.Double(chaseCameraModeZoom, chaseCameraModeZoom));
			break;
			
			
		case OVERVIEW_CAMERA_MODE:
			mode = m;
			break;
			
		}
		
	}
	
	public void operate() {
		
		Point2D.Double center = new Point2D.Double(0, 0);
		double greatestDistance = 0.0;
		Vector midpointVec;
		HoverCar chaseModeCar = null;
		
		if (trackedEntities.size() > 0) {
			
			switch (mode) {
			
			case CHASE_CAMERA_MODE:
				
				chaseModeCar = null;
				for (int i = 0; i < trackedEntities.size(); i++) {
					if (trackedEntities.get(i) instanceof HoverCar) {
						if (!((HoverCar) trackedEntities.get(i)).isComputerControlled) {
							chaseModeCar = (HoverCar) trackedEntities.get(i);
						}
					}
				}
				
				if (chaseModeCar != null) {
					
					center = chaseModeCar.velocity.getHead();
					center.x += chaseModeCar.origin.x;
					center.y += chaseModeCar.origin.y;
					
					center.x *= 0.5;
					center.y *= 0.5;
					
					myPlane.setPan(center);
				}
				break;
			
			case OVERVIEW_CAMERA_MODE:
				
				for (int i = 0; i < trackedEntities.size(); i++) {
					center.x += trackedEntities.get(i).origin.x;
					center.y += trackedEntities.get(i).origin.y;
				}
			
				center.x /= trackedEntities.size();
				center.y /= trackedEntities.size();
			
				center.x += trackedEntities.get(0).origin.x;
				center.y += trackedEntities.get(0).origin.y;
			
				center.x *= 0.5;
				center.y *= 0.5;
			
				for (int i = 0; i < trackedEntities.size(); i++) {
					midpointVec = new Vector(center, trackedEntities.get(i).origin, new Color(255, 255, 255));
					if (midpointVec.magnitude > greatestDistance) {
						greatestDistance = midpointVec.magnitude;
					}
				}
			
				myPlane.setPan(center);
				double zoom = myPlane.getHeight()  / (greatestDistance * 2.0);
				if (zoom > 2.5) {
					zoom = 2.5;
				}
				myPlane.setZoom(new Point2D.Double(zoom, zoom));
				break;
			
			}
			

			
		}
		
	}
	
}
