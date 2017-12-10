import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Track {

	PhysicsEngine myEngine;
	ArrayList<WallEntity> trackWalls;
	ArrayList<GateNode> gateNodes;
	ArrayList<Vector> startPositions;
	Point2D.Double origin;
	int nextStartPosition;
	
	
	public Track(PhysicsEngine e) {
		myEngine = e;
		trackWalls = new ArrayList<WallEntity>();
		gateNodes = new ArrayList<GateNode>();
		startPositions = new ArrayList<Vector>();
		origin = new Point2D.Double(0,0);
		nextStartPosition = 0;
	}
	
	public void resize(double resizeXFactor, double resizeYFactor) {
		
		Point2D.Double head;
		Point2D.Double tail;
		
		for (int i = 0; i < trackWalls.size(); i++) {
			
			head = trackWalls.get(i).wall.getHead();
			tail = new Point2D.Double(trackWalls.get(i).wall.origin.x, trackWalls.get(i).wall.origin.y);
			
			head.x -= origin.x;
			head.y -= origin.y;
			tail.x -= origin.x;
			tail.y -= origin.y;
			
			head.x *= resizeXFactor;
			head.y *= resizeYFactor;
			tail.x *= resizeXFactor;
			tail.y *= resizeYFactor;
			
			head.x += origin.x;
			head.y += origin.y;
			
			trackWalls.get(i).wall.origin.x = tail.x + origin.x;
			trackWalls.get(i).wall.origin.y = tail.y + origin.y;
			
			trackWalls.get(i).wall.setHead(head);
			
		}
		
		for (int i = 0; i < gateNodes.size(); i++) {
			
			head = gateNodes.get(i).wall.getHead();
			tail = new Point2D.Double(gateNodes.get(i).wall.origin.x, gateNodes.get(i).wall.origin.y);
			
			head.x -= origin.x;
			head.y -= origin.y;
			tail.x -= origin.x;
			tail.y -= origin.y;
			
			head.x *= resizeXFactor;
			head.y *= resizeYFactor;
			tail.x *= resizeXFactor;
			tail.y *= resizeYFactor;
			
			head.x += origin.x;
			head.y += origin.y;
			
			gateNodes.get(i).wall.origin.x = tail.x + origin.x;
			gateNodes.get(i).wall.origin.y = tail.y + origin.y;
			
			gateNodes.get(i).wall.setHead(head);
			
			gateNodes.get(i).establishMidPoint();
		}
		
	}
	
	public void transpose(double xDistance, double yDistance) {
		
		for (int i = 0; i < trackWalls.size(); i++) {
			trackWalls.get(i).wall.origin.x += xDistance;
			trackWalls.get(i).wall.origin.y += yDistance;
		}
		
		for (int i = 0; i < gateNodes.size(); i++) {
			gateNodes.get(i).wall.origin.x += xDistance;
			gateNodes.get(i).wall.origin.y += yDistance;
			gateNodes.get(i).establishMidPoint();
		}
		
		origin.x += xDistance;
		origin.y += yDistance;
		
	}
	
	
	public void addToEngine(PhysicsEngine e) {
		
		for (int i = 0; i < trackWalls.size(); i++) {
			e.addEntity(trackWalls.get(i));
		}
		
		for (int i = 0; i < gateNodes.size(); i++) {
			e.addEntity(gateNodes.get(i));
		}		
	}
	
	public void removeFromEngine(PhysicsEngine e) {
	
		for (int i = 0; i < trackWalls.size(); i++) {
			e.entities.remove(trackWalls.get(i));
		}
		
		for (int i = 0; i < gateNodes.size(); i++) {
			e.entities.remove(gateNodes.get(i));
		}
	}
	
	public void generateRing(PhysicsEngine e, int resolution, double width) {
		
		
		double angStep = Vector.twoPi / (double) resolution;
		
		for (int i = 0; i < resolution; i++) {
			trackWalls.add(new WallEntity(e, 
					Math.cos(angStep * (double) i), 
					Math.sin(angStep * (double) i), 
					Math.cos(angStep * (double) (i+1)), 
					Math.sin(angStep * (double) (i+1)),
					new Color(0,0,255)	));
			
			trackWalls.add(new WallEntity(e, 
					(width+1) * Math.cos(angStep * (double) i), 
					(width+1) * Math.sin(angStep * (double) i), 
					(width+1) * Math.cos(angStep * (double) (i+1)), 
					(width +1) * Math.sin(angStep * (double) (i+1)),
					new Color(0,0,255)	));
			
			gateNodes.add(new GateNode(e,
					trackWalls.get(trackWalls.size() -1).wall.origin,
					trackWalls.get(trackWalls.size() -2).wall.origin));
			
		}
			
	}
	
	public void createStartLine(Point2D.Double o, Point2D.Double h) {
		
		if (gateNodes.size() == 0) {
			gateNodes.add(new GateNode(myEngine, o, h));
			gateNodes.get(0).wall.c = new Color(255, 0, 0);
		}
		
	}
	
	public void generateStartPositions(int positionsPerRow, int numRows) {
		
		GateNode startLine;
		double rowStep;
		double parametricRowStep;
		double rowStartOffset;
		double parametricRowStartOffset;
		
		if (gateNodes.size() != 0) {
			
			startLine = gateNodes.get(0);
			rowStep = startLine.wall.magnitude / positionsPerRow;
			parametricRowStep = rowStep / startLine.wall.magnitude;
			rowStartOffset = rowStep * 0.5;
			parametricRowStartOffset = rowStartOffset / startLine.wall.magnitude;
			
			
			Vector railGuide = new Vector(startLine.wall.origin.x, 
					startLine.wall.origin.y, 
					startLine.wall.theta - Vector.halfPi, 
					rowStep*numRows, 
					new Color(0, 255, 255));
			
			myEngine.additionalVectorRenderQueue.add(railGuide);

			for (int r = 0; r < numRows; r++) {
				
				Point2D.Double rowGuideOrigin = railGuide.getParametricPoint(parametricRowStartOffset + (parametricRowStep * ((double) r)));
				Vector rowGuide = new Vector(rowGuideOrigin.x,
						rowGuideOrigin.y,
						startLine.wall.theta,
						startLine.wall.magnitude,
						new Color(0,255,255));
				
				myEngine.additionalVectorRenderQueue.add(rowGuide);
				
				for (int p = 0; p < positionsPerRow; p++) {
					
					Point2D.Double startPointOrigin = rowGuide.getParametricPoint(parametricRowStartOffset + (parametricRowStep * (double) p));
					Vector startVec = new Vector(startPointOrigin.x,
							startPointOrigin.y,
							startLine.wall.theta + Vector.halfPi, 
							100.0,
							new Color(0,255,0));
					
					startPositions.add(startVec);
					myEngine.additionalVectorRenderQueue.add(startVec);
					
				}
				
			}
					
		}
		
	}
	
	public Vector getNextStartPosition() {
		
		if (startPositions.size() > nextStartPosition) {
			nextStartPosition++;
			return startPositions.get(nextStartPosition-1);		
		} else {
			return new Vector();
		}
		
	}
	
	public void appendStraightaway(double length) {
		
		if (gateNodes.size() > 0) {
			
			GateNode g = gateNodes.get(gateNodes.size() - 1);
			Point2D.Double gHead = g.wall.getHead();
			WallEntity innerWall = new WallEntity(myEngine);
			WallEntity outerWall = new WallEntity(myEngine);
			
			innerWall.wall = new Vector(g.wall.origin.x, g.wall.origin.y, g.wall.theta + Vector.halfPi, length, new Color(0,0,255));
			outerWall.wall = new Vector(gHead.x, gHead.y, g.wall.theta + Vector.halfPi, length, new Color(0,0,255));
			
			gateNodes.add(new GateNode(myEngine, innerWall.wall.getHead(), outerWall.wall.getHead()));
			trackWalls.add(innerWall);
			trackWalls.add(outerWall);
			
		}
		
	}
	
	public void appendClockWiseTurn(double deltaTheta, double turnRadius, int resolution) {
		
		if (gateNodes.size() > 0) {
		
			Point2D.Double turnOrigin = gateNodes.get(gateNodes.size() -1).wall.getParametricPoint(1 + (turnRadius / gateNodes.get(gateNodes.size() -1).wall.magnitude));
			double thetaStep = -deltaTheta / resolution;
			double beginAng = gateNodes.get(gateNodes.size() -1).wall.theta + Math.PI;
			double width = gateNodes.get(gateNodes.size() -1).wall.magnitude;
		
			for (int i = 0; i < resolution; i++) {
			
				trackWalls.add(new WallEntity(myEngine, 
						turnOrigin.x + turnRadius * Math.cos(beginAng + thetaStep * (double) i), 
						turnOrigin.y + turnRadius * Math.sin(beginAng + thetaStep * (double) i), 
						turnOrigin.x + turnRadius * Math.cos(beginAng + thetaStep * (double) (i+1)), 
						turnOrigin.y + turnRadius * Math.sin(beginAng + thetaStep * (double) (i+1)),
						new Color(0,0,255)	));
			
				trackWalls.add(new WallEntity(myEngine, 
						turnOrigin.x + (width+turnRadius) * Math.cos(beginAng + thetaStep * (double) i), 
						turnOrigin.y + (width+turnRadius) * Math.sin(beginAng + thetaStep * (double) i), 
						turnOrigin.x + (width+turnRadius) * Math.cos(beginAng + thetaStep * (double) (i+1)), 
						turnOrigin.y + (width +turnRadius) * Math.sin(beginAng + thetaStep * (double) (i+1)),
						new Color(0,0,255)	));
			
				gateNodes.add(new GateNode(myEngine,
						trackWalls.get(trackWalls.size() -1).wall.getHead(),
						trackWalls.get(trackWalls.size() -2).wall.getHead()));
			
			}
		
		}
			
	}
	
	public void appendCounterClockWiseTurn(double deltaTheta, double turnRadius, int resolution) {
		
		if (gateNodes.size() > 0) {
		
			Point2D.Double turnOrigin = gateNodes.get(gateNodes.size() -1).wall.getParametricPoint(-(turnRadius / gateNodes.get(gateNodes.size() -1).wall.magnitude));
			double thetaStep = deltaTheta / resolution;
			double beginAng = gateNodes.get(gateNodes.size() -1).wall.theta;
			double width = gateNodes.get(gateNodes.size() -1).wall.magnitude;
		
			for (int i = 0; i < resolution; i++) {
			
				trackWalls.add(new WallEntity(myEngine, 
						turnOrigin.x + turnRadius * Math.cos(beginAng + thetaStep * (double) i), 
						turnOrigin.y + turnRadius * Math.sin(beginAng + thetaStep * (double) i), 
						turnOrigin.x + turnRadius * Math.cos(beginAng + thetaStep * (double) (i+1)), 
						turnOrigin.y + turnRadius * Math.sin(beginAng + thetaStep * (double) (i+1)),
						new Color(0,0,255)	));
			
				trackWalls.add(new WallEntity(myEngine, 
						turnOrigin.x + (width+turnRadius) * Math.cos(beginAng + thetaStep * (double) i), 
						turnOrigin.y + (width+turnRadius) * Math.sin(beginAng + thetaStep * (double) i), 
						turnOrigin.x + (width+turnRadius) * Math.cos(beginAng + thetaStep * (double) (i+1)), 
						turnOrigin.y + (width +turnRadius) * Math.sin(beginAng + thetaStep * (double) (i+1)),
						new Color(0,0,255)	));
			
				gateNodes.add(new GateNode(myEngine,
						trackWalls.get(trackWalls.size() -2).wall.getHead(),
						trackWalls.get(trackWalls.size() -1).wall.getHead()));
			
			}
		
		}
		
	}
	
	public void appendFinishingStretch() {
		//make sure track has a pretty clean shot at the start line before use
		
		if (gateNodes.size() > 1) {
			Point2D.Double startHead = gateNodes.get(0).wall.getHead();
			Point2D.Double lastGateHead = gateNodes.get(gateNodes.size() -1).wall.getHead();
			
			trackWalls.add(new WallEntity(myEngine, 
					lastGateHead.x,
					lastGateHead.y,
					startHead.x,
					startHead.y,
					new Color(0,0,255)));
			
			trackWalls.add(new WallEntity(myEngine, 
					gateNodes.get(gateNodes.size() -1).wall.origin.x,
					gateNodes.get(gateNodes.size() -1).wall.origin.y,
					gateNodes.get(0).wall.origin.x,
					gateNodes.get(0).wall.origin.y,
					new Color(0,0,255)));
		}
		
		
	}

	public void generateTestTrack() {
		
		createStartLine(new Point2D.Double(1000, 0), new Point2D.Double(2000,50));
		appendStraightaway(2500);
		appendClockWiseTurn(Vector.threeQuartersPi, 50, 5);
		appendStraightaway(3000);
		appendClockWiseTurn(Math.PI, 50, 6);
		appendCounterClockWiseTurn(Vector.threeQuartersPi, 50, 5);
		appendStraightaway(3000);
		appendCounterClockWiseTurn(Vector.quarterPi, 50, 4);
		appendClockWiseTurn(Vector.twoPi - Vector.quarterPi, 3000, 16);
		appendCounterClockWiseTurn(Vector.halfPi, 500, 4);
		appendFinishingStretch();
		generateStartPositions(4, 3);
		addToEngine(myEngine);
		
	}
	
	public void generateTestTrack2() {
		
		createStartLine(new Point2D.Double(-500.0, 0.0), new Point2D.Double(0.0, 500.0));
		generateStartPositions(3, 4);
		appendStraightaway(500);
		appendClockWiseTurn(Vector.quarterPi, 50.0, 4);
		appendCounterClockWiseTurn(Vector.halfPi, 50, 4);
		appendClockWiseTurn(Math.PI, 100, 8);
		appendStraightaway(2000);
		appendClockWiseTurn(Math.PI, 50, 4);
		appendCounterClockWiseTurn(Vector.threeQuartersPi, 50, 4);
		appendStraightaway(1000);
		appendClockWiseTurn(Vector.halfPi, 100, 4);
		appendStraightaway(3000);
		appendClockWiseTurn(Vector.halfPi+Math.PI, 1000, 16);
		appendCounterClockWiseTurn(Vector.halfPi, 50, 4);
		appendStraightaway(400);
		appendCounterClockWiseTurn(Vector.halfPi, 50, 4);
		appendFinishingStretch();
		addToEngine(myEngine);
	}
	
}
