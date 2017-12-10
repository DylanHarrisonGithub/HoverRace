import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

public class Vector {

	public Point2D.Double origin;
	public double theta;
	public double magnitude;
	public Color c;
	public static final double twoPi = Math.PI * 2.0;
	public static final double halfPi = Math.PI * 0.5;
	public static final double quarterPi = Math.PI * 0.25;
	public static final double threeQuartersPi = Math.PI * 0.75;
	public static final double PiRCP = 1.0 / Math.PI;
	public static final double twoPiRCP = 1.0 / twoPi;
	public static final double halfPiRCP = 1.0 / halfPi;
	public static final double quarterPiRCP = 1.0 / quarterPi;
	public static final double threeQuartersPiRCP = 1.0 / threeQuartersPi;
	public static final double radiansToDegreesRatio = 360.0 / twoPi;
	public static final double degreesToRadiansRatio = twoPi / 360.0;
	
	public Vector() {
		
		origin = new Point2D.Double();
		c = new Color(255, 0, 0);
		origin.x = 0.0;
		origin.y = 0.0;
		theta = 0.0;
		magnitude = 0.0;
		
	}
	
	public Vector(double xo, double yo, double theta, double magnitude, Color c) {
		
		origin = new Point2D.Double();
		origin.x = xo;
		origin.y = yo;
		this.c = c;
		this.theta = theta;
		this.magnitude = magnitude;
		enforceAngleRules();
	}
	
	public Vector(Point2D.Double o, Point2D.Double h, Color c) {
		
		double deltax, deltay;
		this.c = new Color(c.getRGB());
		deltax = h.x - o.x;
		deltay = h.y - o.y;

		origin = new Point2D.Double(o.x, o.y);
		magnitude = Math.sqrt( deltax * deltax + deltay * deltay);
		theta = Math.atan2(deltay, deltax);
		enforceAngleRules();
		
	}
	
	public Vector copy() {
		
		Vector returnVec = new Vector(origin.x, origin.y, theta, magnitude, new Color(c.getRGB()));
		
		return returnVec;
		
	}
	
	public double getComponentX() {
		
		return magnitude * Math.cos(theta);
		
	}
	
	public double getComponentY() {
		
		return magnitude * Math.sin(theta);
		
	}
	
	public static double getComponentX(Vector v) {
		
		return v.magnitude * Math.cos(v.theta);
		
	}
	
	public static double getComponentY(Vector v) {
		
		return v.magnitude * Math.sin(v.theta);
		
	}
	
	public static Point2D.Double SinCos(double theta) {
		
		//since x86 fSinCos is not available
		// this may yield a small speed improvement
		// over sin(t) & cos(t) computed seperately
		Point2D.Double ret = new Point2D.Double();
		
		ret.x = Math.sin(theta);
		ret.y = Math.sqrt(1 - ret.x*ret.x);
		
		return ret;
		
	}
	
	public Point2D.Double getHead() {
		
		Point2D.Double head = new Point2D.Double();
		
		head.x = magnitude * Math.cos(theta) + origin.x;
		head.y = magnitude * Math.sin(theta) + origin.y;
		return head;
		
	}
	
	public void setHead(Point2D.Double h) {
		
		double dx = h.x - origin.x;
		double dy = h.y - origin.y;
		
		theta = Math.atan2(dy, dx);
		magnitude = Math.sqrt( dx * dx + dy * dy);
		
	}
	
	public double getYintercept() {
		
		double m = Math.tan(theta);
		
		return -m * origin.x + origin.y;
		
	}
	
	public Point2D.Double getIntersectionPoint(Vector v) {
		
		Point2D.Double intersection = new Point2D.Double();
		double thisYintercept = getYintercept();
		double vYintercept = v.getYintercept();
		double thisSlope = Math.tan(theta);
		double vSlope = Math.tan(v.theta);
		
		intersection.x = (vYintercept - thisYintercept) / (thisSlope - vSlope);
		intersection.y = thisSlope * intersection.x + thisYintercept;
		
		return intersection;
		
	}
	
	public double getParametricIntersection2(Vector v) {
		
		double tempReturn = -1;
		Point2D.Double thisHead = getHead();
		//Point2D.Double vHead = v.getHead();
		double thisYintercept = getYintercept();
		double vYintercept = v.getYintercept();
		double thisSlope = Math.tan(theta);
		double vSlope = Math.tan(v.theta);
		
		if (theta != v.theta) {
		
			if ( (theta == Vector.halfPi) || (theta == -Vector.halfPi) ) {

				tempReturn = (vSlope * this.origin.x + vYintercept - origin.y) / (thisHead.y - origin.y);
				
			} else {
		
				if ( (theta == 0) || (theta == Math.PI) ) {
					
					tempReturn = (origin.y - vYintercept - origin.x * vSlope) / (vSlope * (thisHead.x - origin.x));
					
				} else {

					tempReturn = (thisSlope * (vYintercept - origin.y) + vSlope * (origin.y - thisYintercept) ) / ( (thisSlope - vSlope) * (thisHead.y - origin.y) );

					
				}
				
			}
		
		}
		
		return tempReturn;
		
	}
	
	public double getParametricIntersection3(Vector v) {
		
		Point2D.Double myHead = getHead();
		Point2D.Double vHead = v.getHead();
		double myDeltaX = myHead.x - origin.x;
		double myDeltaY = myHead.y - origin.y;
		double vDeltaX = vHead.x - v.origin.x;
		double vDeltaY = vHead.y - v.origin.y;
		double divisor = (vDeltaY * myDeltaX - myDeltaY * vDeltaX); 
		
		
		if (divisor == 0) {
			return -1.0;
		} else {
			return (vDeltaY * (v.origin.x - origin.x) + vDeltaX * (origin.y - v.origin.y)  ) / divisor; 
		}
		
	}
	
	public double getParametricIntersection(Vector v) {
		
		double divisor = magnitude * Math.sin(v.theta - theta);
		
		
		if (divisor == 0) {
			return -1.0;
		} else {
			// consider sqrt(1 - sin^2(v.theta)) = cos(v.theta)
			return (Math.cos(v.theta) * (origin.y - v.origin.y) + Math.sin(v.theta) * (v.origin.x - origin.x) ) / divisor;  
		}
		
	}
	
	public Point2D.Double getParametricPoint2(double t) {
		
		Point2D.Double thisHead = getHead();
		Point2D.Double p = new Point2D.Double(origin.x + t * (thisHead.x - origin.x), origin.y + t * (thisHead.y - origin.y));
		
		return p;
		
	}
	
	public Point2D.Double getParametricPoint(double t) {
		
		double tempMag = magnitude * t;
		
		return new Point2D.Double(origin.x + tempMag * Math.cos(theta), origin.y + tempMag * Math.sin(theta)); 
		
	}
	
	public Vector getPerpendicularVec(Point2D.Double o, Color c) {
		
		Vector perpVec = new Vector(o.x, o.y, theta + halfPi, 1.0, c);
		
		perpVec.magnitude = perpVec.getParametricIntersection(this);
		
		if (perpVec.magnitude < 0) {
			perpVec.magnitude *= -1;
			perpVec.rotate(Math.PI);
		}
		
		return perpVec;
		
	}
	
	public void AddVec(Vector v) {
		
		double x, y;
		
		x = getComponentX(v) + getComponentX();
		y = getComponentY(v) + getComponentY();
		
		theta = Math.atan2(y, x);
		enforceAngleRules();
		magnitude = Math.sqrt(x*x + y*y);
		
	}
	
	public void appendTo(Vector v) {
		
		origin.x = v.getComponentX() + v.origin.x;
		origin.y = v.getComponentY() + v.origin.y;
		
	}
	
	public void rotate(double deltaTheta) {
		
		theta += deltaTheta;
		
		enforceAngleRules();
		
	}
	
	public void enforceAngleRules() {
	
		if (magnitude < 0.0) {
			theta += Math.PI;
			magnitude = Math.abs(magnitude);
		}
		
		if (theta > twoPi) {
			while (theta > twoPi) {
				theta -= twoPi;
			}
			if (theta > Math.PI) {
				theta = theta - twoPi;
			}
		} 
		
		if (theta <= -twoPi) {
			while (theta < -twoPi) {
				theta += twoPi;
			}
			if (theta < -Math.PI) {
				theta = theta + twoPi;
			}
		}
		
	}
	
	public double getAbsoluteAngle() {
		
		double tempReturn = theta;
		
		if (Math.abs(tempReturn) > halfPi) {
			if (tempReturn > halfPi) {
				tempReturn -= Math.PI;
			} else if (tempReturn < - halfPi) {
				tempReturn += Math.PI;
			}
		}
		
		return tempReturn;
		
	}
	
	public static double enforceAngleRules(double t) {
		
		if (t > twoPi) {
			while (t > twoPi) {
				t -= twoPi;
			}
			
			if (t > Math.PI) {
				t = t - twoPi;
			}
		} 
		
		if (t <= -twoPi) {
			while (t < -twoPi) {
				t += twoPi;
			}
			
			if (t < -Math.PI) {
				t = t + twoPi;
			}
		}
		
		return t;
		
	}
	
	
	public static Vector addVecs(Vector v1, Vector v2) {
		
		Vector returnVec = new Vector();
		double x, y;
		
		returnVec.origin.x = v1.origin.x;
		returnVec.origin.y = v1.origin.y;
	
		x = getComponentX(v1) + getComponentX(v2);
		y = getComponentY(v1) + getComponentY(v2);
		
		returnVec.theta = Math.atan2(y, x);
		returnVec.enforceAngleRules();
		returnVec.magnitude = Math.sqrt(x*x + y*y);
		
		return returnVec;
		
	}
	
	public double getAngleBetween2(Vector v) {
		
		double dotProduct;
		
		dotProduct = getComponentX() * v.getComponentX() + getComponentY() * v.getComponentY();
		
		return Math.acos(dotProduct / (magnitude * v.magnitude));
		
	}
	
	public double getAngleBetween(Vector v) {
		
		return Math.abs(this.theta - v.theta);
	}
	
	public void draw(Graphics g) {

		double x1, x2, y1, y2;
	    double tenthMag = magnitude * 0.1;
	    
	    x2 = Math.cos(theta) * magnitude + origin.x;
	    y2 = Math.sin(theta) * magnitude + origin.y;
	    
	    g.drawLine((int) x2,(int) y2,(int) origin.x,(int) origin.y);
	    
	    x1 = Math.cos(theta + 2.3561944901923449288469825374596) * tenthMag + x2;
	    y1 = Math.sin(theta + 2.3561944901923449288469825374596) * tenthMag + y2;
	    
	    g.drawLine((int) x1,(int) y1,(int) x2,(int) y2);
	    
	    x1 = Math.cos(theta - 2.3561944901923449288469825374596) * tenthMag + x2;
	    y1 = Math.sin(theta - 2.3561944901923449288469825374596) * tenthMag + y2;
	    
	    g.drawLine((int) x1,(int) y1,(int) x2,(int) y2);
	    
	}
	
	public void plot(Graphics g, CartesianPlane p) {
		
	    double tenthMag = magnitude * 0.1;
		Point2D.Double head = new Point2D.Double(getComponentX() + origin.x, getComponentY() + origin.y);
		Point2D.Double arrowTail1 = new Point2D.Double(
				Math.cos(theta + 2.3561944901923449288469825374596) * tenthMag + head.x,
				Math.sin(theta + 2.3561944901923449288469825374596) * tenthMag + head.y);
		Point2D.Double arrowTail2 = new Point2D.Double(
				Math.cos(theta - 2.3561944901923449288469825374596) * tenthMag + head.x,
				Math.sin(theta - 2.3561944901923449288469825374596) * tenthMag + head.y);

		p.plotGraphLine(g, origin, head, c);
		p.plotGraphLine(g, head, arrowTail1, c);
		p.plotGraphLine(g, head, arrowTail2, c);	
		
	}
	
}
