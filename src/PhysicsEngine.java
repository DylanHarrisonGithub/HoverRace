import java.util.ArrayList;
import java.awt.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PhysicsEngine {
	
	public ArrayList<PhysicsEntity> entities;
	public ArrayList<Vector> additionalVectorRenderQueue;
	public ArrayList<String> engineLog;
	private int maxLogLength = 15;
	private PhysicsEntity collider;
	private PhysicsEntity collidee;
	private PhysicsEntity lastCollider;
	private PhysicsEntity lastCollidee;
	private StopWatch deltaTimer;
	public StopWatch clock;
	private Timer updateTimer;
	private int defaultUpdateTime = 17;
	private double nextCollisionTime;
	public int numEntities = 0;
	public double timeFactor = 1.0;
	public boolean isPaused = false;
	public double collisionFrameTime;
	
	public PhysicsEngine() {
		
		entities = new ArrayList<PhysicsEntity>();
		additionalVectorRenderQueue = new ArrayList<Vector>();
		engineLog = new ArrayList<String>();
		deltaTimer = new StopWatch();
		clock = new StopWatch();
		updateTimer = new Timer(defaultUpdateTime, run);
		
	}
	
	public void go() {
		
		deltaTimer.reset();
		deltaTimer.start();
		//clock.reset();
		clock.resume();
		updateTimer.start();
		isPaused = false;
		
	}
	
	public void pause() {
		
		deltaTimer.stop();
		updateTimer.stop();	
		clock.stop();
		isPaused = true;
		
	}
	
	public void setUpdateTime(int updateTimeMS) {
		
		defaultUpdateTime = updateTimeMS;
		updateTimer.removeActionListener(run);
		updateTimer.stop();
		updateTimer = new Timer(defaultUpdateTime, run);
		
	}

	ActionListener run = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			deltaTimer.stop();
			lastCollider = null;
			lastCollidee = null;
			for (int i = 0; i < entities.size(); i++) {
				entities.get(i).computeVelocity(deltaTimer.getStopTimeInSeconds() * timeFactor);
			}
			moveEntites(deltaTimer.getStopTimeInSeconds() * timeFactor);
			deltaTimer.start();
			
		}	
		
	};
	
	public void addEntity(PhysicsEntity e) {
	

		if (e.isReactive){
			entities.add(0, e);
		} else {
			entities.add(e);
		}
		
		e.serialID = numEntities;
		numEntities++;
		
	}
	
	public void drawEntities(Graphics g, CartesianPlane p) {
		
		for (int i = 0; i < entities.size(); i++) {
			
			entities.get(i).draw(g, p);
			
		}
		
		for (int i = 0; i < additionalVectorRenderQueue.size(); i++) {
			
			additionalVectorRenderQueue.get(i).plot(g, p);
			
		}
		
		//additionalVectorRenderQueue.clear();
		
	}
	
	public void displayEntityLog(Graphics g, CartesianPlane p) {
		
		for (int i = 0; i < entities.size(); i++) {
			
			entities.get(i).generateLog();
			p.textCursor += 5;
			
			for (int j = 0; j < entities.get(i).status.size(); j++) {

				g.drawString(entities.get(i).status.get(j), 10, p.textCursor);
				p.textCursor += 10;
				
			}
			for (int j = 0; j < entities.get(i).baseLogLength; j++) {			
				entities.get(i).status.remove(0);
			}
			
		}
		
	}
	
	public void log(String msg) {
		
		engineLog.add(msg);
		
		if (engineLog.size() > maxLogLength) {
			engineLog.remove(0);
		}
		
	}
	
	public void displayEngineLog(Graphics g, CartesianPlane p) {
		
		g.drawString("Engine Log: @" + clock.getElapsedTimeHMSString(), 10, p.textCursor);
		p.textCursor += 10;
		for (int i = 0; i < engineLog.size(); i++) {
			g.drawString(engineLog.get(i), 10, p.textCursor);
			p.textCursor += 10;
		}
		g.drawString(" ----------- ", 10, p.textCursor);
		p.textCursor += 10;
		
	}
	
	public void moveEntites(double deltaTime) {
		
		collider = null;
		collidee = null;
		nextCollisionTime = -1.0;
		
		determineNextCollision(deltaTime);
		
		if (nextCollisionTime >= 0.0) {
			
			for (int i = 0; i < entities.size(); i++) {			
				entities.get(i).move(nextCollisionTime);	
			}

			//System.out.println(nextCollisionTime);
			//log(collider.entityType);
			log(collider.entityType + " ID: " + collider.serialID + " collides with " + collidee.entityType + " ID: " + collidee.serialID);
			collider.collideWith(collidee);
			lastCollider = collider;
			lastCollidee = collidee;

			moveEntites(deltaTime - nextCollisionTime);
			
		} else {
			
			for (int i = 0; i < entities.size(); i++) {			
				entities.get(i).move(deltaTime);	
			}
			
		}	
		
	}
	
	public void determineNextCollision(double deltaTime) {
		
		double collisionTime;
		collisionFrameTime = deltaTime;
		
		for (int i = 0; i < (entities.size() - 1); i++) {
			
			for (int j = i + 1; j < entities.size(); j++) {
				
				collisionTime = entities.get(i).getCollisionTime(entities.get(j));
				
				if ((collisionTime > 0.0) && (collisionTime <= deltaTime)) {
					
					if (nextCollisionTime > 0.0) {
						
						if (nextCollisionTime > collisionTime) {
							
							if ( !((entities.get(i) == lastCollider) && (entities.get(j) == lastCollidee)) ) {
							
								collider = entities.get(i);
								collidee = entities.get(j);
								nextCollisionTime = collisionTime;
								
							}
							
						}
						
					} else {
					
						if ( !((entities.get(i) == lastCollider) && (entities.get(j) == lastCollidee)) ) {

							collider = entities.get(i);
							collidee = entities.get(j);
							nextCollisionTime = collisionTime;
						
						}
						
					}
					
				}
				
			}
			
		}
		
	}

}
