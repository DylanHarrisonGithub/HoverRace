import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.*;

import javax.swing.*;

public class CartesianPlane extends JPanel {

	private Point2D.Double screenRes;
	private Point2D.Double screenResHalf;
	private Point2D.Double screenResRCP;
	public Point2D.Double graphRes;
	private Point2D.Double graphResRCP;
	private Point2D.Double screenToGraphRatio;
	private Point2D.Double graphToScreenRatio;
	private Point2D.Double zoom;
	private Point2D.Double pan;
	private Point2D.Double mousePos;
	private boolean isDefaultMouseInteractive;
	private Color axisColor;
	private Color gridColor;
	public int textCursor;
	
	public CartesianPlane(boolean Interactive) {
		
		screenRes = new Point2D.Double();
		screenResHalf = new Point2D.Double();
		screenResRCP = new Point2D.Double();
		graphRes = new Point2D.Double();
		graphResRCP = new Point2D.Double();
		screenToGraphRatio = new Point2D.Double();
		graphToScreenRatio = new Point2D.Double();
		zoom = new Point2D.Double(1.0, 1.0);
		pan = new Point2D.Double(0.0, 0.0);
		mousePos = new Point2D.Double();
		isDefaultMouseInteractive = Interactive;
		addMouseListener(defaultMouseListener);
		addMouseMotionListener(defaultMouseMotionListener);
		addMouseWheelListener(defaultMouseWheelListener);
		
		this.axisColor = new Color(255, 164, 200);
		this.gridColor = new Color(200, 200, 220);
		this.setBackground(new Color(0,0,0));
		
	    addComponentListener(new ComponentListener() 
	    {
	    	public void componentShown(ComponentEvent e) {
	    	}
	    	public void componentResized(ComponentEvent e) {
	    		update();
	    	}
			@Override
			public void componentHidden(ComponentEvent arg0) {
			}
			@Override
			public void componentMoved(ComponentEvent arg0) {
			}
	    });
		
		
		//update();
	}
	
	public void update() {
	    
	    this.screenRes.x = (double) getWidth(); 
	    this.screenRes.y = (double) getHeight(); 
	    this.screenResHalf.x = this.screenRes.x * 0.5;
	    this.screenResHalf.y = this.screenRes.y * 0.5;
	    this.screenResRCP.x = 1.0 / this.screenRes.x;
	    this.screenResRCP.y = 1.0 / this.screenRes.y;
	    
	    //System.out.println(screenRes.x);
	    //System.out.println(screenRes.y);
	    updateGraphRes();
	    updateRatios();
  
	}
		
	private void updateGraphRes() {
		
		this.graphRes.x = this.zoom.x * this.screenRes.x;
		this.graphRes.y = this.zoom.y * this.screenRes.y;
		this.graphResRCP.x = 1.0 / this.graphRes.x;
		this.graphResRCP.y = 1.0 / this.graphRes.y;
		
	}
	
	private void updateRatios() {
					    
		this.graphToScreenRatio.x = this.graphRes.x * this.screenResRCP.x;
		this.graphToScreenRatio.y = this.graphRes.y * this.screenResRCP.y;
		
		this.screenToGraphRatio.x = this.screenRes.x * this.graphResRCP.x;
		this.screenToGraphRatio.y = this.screenRes.y * this.graphResRCP.y;
		
	}
	
	
	public Point2D.Double screenToGraphCoords(Point2D.Double ScreenPoint) {
		
		Point2D.Double tempReturn = new Point2D.Double();
		
		tempReturn.x = (ScreenPoint.x - this.screenResHalf.x) * this.screenToGraphRatio.x + this.pan.x;
		tempReturn.y = (ScreenPoint.y - this.screenResHalf.y) * -this.screenToGraphRatio.y + this.pan.y;
		
		return tempReturn;
	
	}

	public Point2D.Double graphToScreenCoords(Point2D.Double GraphPoint) {
	
		Point2D.Double tempReturn = new Point2D.Double();
		
		tempReturn.x = (GraphPoint.x - this.pan.x) * this.graphToScreenRatio.x + this.screenResHalf.x;
		tempReturn.y = (GraphPoint.y - this.pan.y) * -this.graphToScreenRatio.y + this.screenResHalf.y;
		
		return tempReturn;
		
	}
	
	public void drawAxis(Graphics g) {
		
		Color savedColor = new Color(g.getColor().getRGB());
		Point2D.Double axis = graphToScreenCoords(new Point2D.Double(0.0, 0.0));
		
		g.setColor(this.axisColor);
		
		if ( (axis.y > 0.0) && (axis.y < this.screenRes.y) ) {
			
			g.drawLine(0, (int) axis.y,(int) this.screenRes.x,(int) axis.y);
			
		}
		
		if ( (axis.x > 0.0) && (axis.x < this.screenRes.x) ) {
			
			g.drawLine((int) axis.x, 0, (int) axis.x, (int) this.screenRes.y );
		
		}
		
		g.setColor(savedColor);
	}
	
	public void drawGrid(Graphics g) {
		
		String expString;
		int xZoomExp;
		int yZoomExp;
		double xStep;
		double yStep;
		Point2D.Double p1 = new Point2D.Double();
		Point2D.Double p2 = new Point2D.Double();
		Point2D.Double Min = new Point2D.Double(0, 0);
		Point2D.Double Max = new Point2D.Double(this.screenRes.x, this.screenRes.y);
		
		Min = screenToGraphCoords(Min);
		Max = screenToGraphCoords(Max);
		
		expString = String.format("%e", this.zoom.x);
		xZoomExp = Integer.parseInt(expString.substring(expString.length()-3));
		expString = String.format("%e", this.zoom.y);
		yZoomExp = Integer.parseInt(expString.substring(expString.length()-3));
		
		xStep = Math.pow(10, -xZoomExp+1);
		yStep = Math.pow(10, -yZoomExp+1);

			
		p1.x = ((float) ((int) (Min.x / xStep))) * xStep;

		p1.y = Min.y;
		p2.y = Max.y;
		while (p1.x < Max.x){ 
			
			p2.x = p1.x;
			plotGraphLine(g, p1, p2, this.gridColor);
			p1.x += xStep;
					
		}
			

		p1.y = (float) ((int) (Min.y / yStep)) * yStep;

		p1.x = Min.x;
		p2.x = Max.x;		
		while (p1.y > Max.y) {
			
			p2.y = p1.y;
			plotGraphLine(g, p1, p2, this.gridColor);
			p1.y -= yStep;
			
		}

	}
	
	public void plotGraphPoint(Graphics g, Point2D.Double GraphPoint, Color c) {
		
		Color savedColor = new Color(g.getColor().getRGB());
		Point2D.Double screenPoint;
		
		screenPoint = graphToScreenCoords(GraphPoint);
		
		g.setColor(c);
		g.drawLine((int) screenPoint.x, (int) screenPoint.y, (int) screenPoint.x, (int) screenPoint.y);		
		g.setColor(savedColor);
		
	}
	
	public void plotGraphLine(Graphics g, Point2D.Double graphPoint1, Point2D.Double graphPoint2, Color c) {
		
		Color savedColor = new Color(g.getColor().getRGB());
		Point2D.Double screenPoint1;
		Point2D.Double screenPoint2;
		
		screenPoint1 = graphToScreenCoords(graphPoint1);
		screenPoint2 = graphToScreenCoords(graphPoint2);
		
		g.setColor(c);
		g.drawLine((int) screenPoint1.x, (int) screenPoint1.y, (int) screenPoint2.x, (int) screenPoint2.y);		
		g.setColor(savedColor);
		
	}
	
	public void plotGraphCircle(Graphics g, Point2D.Double origin, double radius, Color c) {
		
		Color savedColor = new Color(g.getColor().getRGB());
		g.setColor(c);
		
		Point2D.Double radiusRatio = new Point2D.Double(graphToScreenRatio.x * radius, graphToScreenRatio.y * radius);
		Point2D.Double screenOrigin = graphToScreenCoords(origin);
		screenOrigin.x -= radiusRatio.x;
		screenOrigin.y -= radiusRatio.y;

		g.fillOval((int) screenOrigin.x,(int) screenOrigin.y,(int) (2 *radiusRatio.x),(int) (2 *radiusRatio.y));
		g.setColor(savedColor);
	}
	
	public void plotGraphCircleEmpty(Graphics g, Point2D.Double origin, double radius, Color c) {
		
		Color savedColor = new Color(g.getColor().getRGB());
		g.setColor(c);
		
		Point2D.Double radiusRatio = new Point2D.Double(graphToScreenRatio.x * radius, graphToScreenRatio.y * radius);
		Point2D.Double screenOrigin = graphToScreenCoords(origin);
		screenOrigin.x -= radiusRatio.x;
		screenOrigin.y -= radiusRatio.y;

		g.drawOval((int) screenOrigin.x,(int) screenOrigin.y,(int) (2 *radiusRatio.x),(int) (2 *radiusRatio.y));
		g.setColor(savedColor);
	}
	
    MouseListener defaultMouseListener = new MouseListener() {
        public void mouseClicked(MouseEvent e) {
        }
    	
    	public void mouseEntered(MouseEvent e) {
    		//System.out.println("Mouse Entered");
    	}
    	
    	public void mouseExited(MouseEvent e) {
    		//System.out.println("Mouse Exited");
    	}
        public void mousePressed(MouseEvent e) {
        	
        	Point2D.Double m = new Point2D.Double();
        	
        	m.x = e.getX();
        	m.y = e.getY();
        	
        	m = screenToGraphCoords(m);
        	setPan(m);
        	repaint();
        	
        }
        
        
        public void mouseReleased(MouseEvent e) {
        	
        }

    };
    
    MouseMotionListener defaultMouseMotionListener = new MouseMotionListener() {
    	public void mouseDragged(MouseEvent e) {
    		
    	}
        public void mouseMoved(MouseEvent e) {
        		        	
        	mousePos.x = e.getX();
        	mousePos.y = e.getY();
        	
        	mousePos = screenToGraphCoords(mousePos);

        }
    };
    
    MouseWheelListener defaultMouseWheelListener = new MouseWheelListener() {
		public void mouseWheelMoved(MouseWheelEvent e) {
			
			Point2D.Double tempZoomPnt = new Point2D.Double();
			
			if (e.getWheelRotation() > 0) {
				
				tempZoomPnt.x = zoom.x * 0.9;
				tempZoomPnt.y = zoom.y * 0.9;
				setZoom(tempZoomPnt);
				//System.out.println("" + zoom.x + " " + zoom.y);
				
			} else {
				tempZoomPnt.x = zoom.x * 1.1;
				tempZoomPnt.y = zoom.y * 1.1;
				setZoom(tempZoomPnt);
				//System.out.println("" + zoom.x + " " + zoom.y);
				
			}
			repaint();
			//setDraw(true);
		}
    };
    
	public void setZoom(Point2D.Double z) {
		
		if (z.x != 0) {
			this.zoom.x = z.x;
		}
		
		if (z.y != 0) {
			this.zoom.y = z.y;
		}
		updateGraphRes();
		updateRatios();
		
		Point m = new Point();
		m = this.getMousePosition();
		if (m != null) {
	    	mousePos.x = m.x;
	    	mousePos.y = m.y;

	    	mousePos = screenToGraphCoords(mousePos);
		}
	}
	
	public void setPan(Point2D.Double p) {
		
		this.pan.x = p.x;
		this.pan.y = p.y;
		
		Point m = new Point();
		m = this.getMousePosition();
		if (m != null) {
	    	mousePos.x = m.x;
	    	mousePos.y = m.y;

	    	mousePos = screenToGraphCoords(mousePos);
		}

		
	}
	
	public void displayStats(Graphics g) {
		
		g.drawString("zoom:  (" + zoom.x + ", " + zoom.y + ")", 10, textCursor);
		g.drawString("pan:   (" + pan.x + ", " + pan.y + ")", 10, textCursor + 10);
		g.drawString("mouse: (" + mousePos.x + ", " + mousePos.y + ")", 10, textCursor + 20);
		
		textCursor += 30;
		
	}
	
	public Point2D.Double getMousePos() {
		
		return new Point2D.Double(mousePos.x, mousePos.y);
		
	}
	
	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		textCursor = 20;
		//drawGrid(g);
		//drawAxis(g);

		displayStats(g);
		
	}
	
}
