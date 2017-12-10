import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class HoverGame extends JFrame {

	PhysicsEngine engine = new PhysicsEngine();

	CartesianPlane gameWorld = new CartesianPlane(true) {
		
		@Override
		public void paintComponent (Graphics g) {
			super.paintComponent(g);
			engine.drawEntities(g, gameWorld);
			//engine.displayEngineLog(g, gameWorld);
			//engine.additionalVectorRenderQueue.clear();
		}
	};
	
	Timer drawTimer;
	Track myTrack = new Track(engine);
	ArrayList<HoverCar> hoverCars = new ArrayList<HoverCar>();
	static int numHoverCars = 8;
	Camera myCamera = new Camera(gameWorld);
	
	
	public HoverGame() {
		
		add(gameWorld);
		setSize(500,500);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		//myTrack.generateTestTrack();
		myTrack.generateTestTrack();
		for (int i = 0; i < numHoverCars; i++) {
			hoverCars.add(new HoverCar(engine));
			engine.addEntity(hoverCars.get(i));
			hoverCars.get(i).setTrack(myTrack);
			hoverCars.get(i).setAtTrackStartPosition();
			myCamera.trackedEntities.add(hoverCars.get(i));
		}
		
		hoverCars.get(0).isComputerControlled = false;

		addKeyListener(myKeyListener);	
		//engine.timeFactor = 0.025;
		
		drawTimer = new Timer(17, onDraw);
		drawTimer.start();

		engine.go();


	}
	
	ActionListener onDraw = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {

			myCamera.operate();
			Point2D.Double mousePos = gameWorld.getMousePos();
			hoverCars.get(0).orientation = Math.atan2(mousePos.y - hoverCars.get(0).origin.y, mousePos.x - hoverCars.get(0).origin.x);
			repaint();	

		}		
	};
	
	KeyListener myKeyListener = new KeyListener() {

		@Override
		public void keyPressed(KeyEvent arg0) {
			
			char c = arg0.getKeyChar();			
			
			switch (c) {
			
			case 'a':
				Point2D.Double mousePos = gameWorld.getMousePos();
				double ang = Math.atan2(mousePos.y - hoverCars.get(0).origin.y, mousePos.x - hoverCars.get(0).origin.x);
				hoverCars.get(0).acceleration = new Vector(0, 0, ang, hoverCars.get(0).accelerationRate + 1000.0, Color.RED);
				break;
				
			case 'b':
				hoverCars.get(0).velocity.magnitude *= 0.25;
				hoverCars.get(0).acceleration = new Vector(0, 0, hoverCars.get(0).velocity.theta, 100.0, new Color(0,255,0));
				hoverCars.get(0).acceleration.rotate(Math.PI);
				break;
				
			case 'o':
				engine.addEntity(new BallEntity(engine));
				break;
				
			case 's':

				HeatSeekingBullet b = new HeatSeekingBullet(engine);
				b.radius = 40.0;
				b.origin.x = hoverCars.get(0).origin.x + (hoverCars.get(0).radius + 2) * Math.cos(hoverCars.get(0).orientation);
				b.origin.y = hoverCars.get(0).origin.y + (hoverCars.get(0).radius + 2) * Math.sin(hoverCars.get(0).orientation);
				b.velocity.theta = hoverCars.get(0).orientation;
				b.velocity.magnitude = 1000.0;
				engine.addEntity(b);
				break;
				
			case '1':
				myCamera.setMode(Camera.FREE_CAMERA_MODE);
				break;
			case '2':
				myCamera.setMode(Camera.CHASE_CAMERA_MODE);
				break;
			case '3':
				myCamera.setMode(Camera.OVERVIEW_CAMERA_MODE);
				break;
							
			}			
		}

		@Override
		public void keyReleased(KeyEvent arg0) {
			// TODO Auto-generated method stub
			
			char c = arg0.getKeyChar();			
			
			switch (c) {
			
			case 'r':
				myTrack.resize(1.1, 1.1);
				break;
				
			case 'p':
				if (engine.isPaused) {
					engine.go();
				} else {
					engine.pause();

				}
				break;
			}
			
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	public static void main(String[] args) {

		new HoverGame();
		
	}

}
