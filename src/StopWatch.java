
public class StopWatch {

	public double startTime;
	public double stopTime;
	public double timeSum;
	public double numTimings;
	public static double secondsPerHourRCP = 1 / 3600;
	public static double secondsPerMinuteRCP = 1 / 60;
	
	public StopWatch() {
		
		timeSum = 0.0;
		numTimings = 0.0;
		
	}
	
	public void reset() {
		
		timeSum = 0.0;
		numTimings = 0.0;
		
	}
	
	public void start() {
		
		startTime = System.nanoTime();
		
	}
	
	public double stop() {
		
		stopTime = System.nanoTime() - startTime;
		timeSum += stopTime;
		numTimings += 1.0;
		
		return stopTime;
		
	}
	
	public void resume() {
		
		startTime = System.nanoTime() - stopTime;
		
	}
	
	public double getElapsedTime() {
		
		return (System.nanoTime() - startTime);
		
	}
	
	public double getElapsedTimeInSeconds() {
		
		return (System.nanoTime() - startTime) * 0.000000001;
		
	}
	
	public String getElapsedTimeHMSString() {
		
		double now = System.nanoTime() - startTime;
		int totalSecondsElapsed = (int) (now * 0.000000001);
		int hours =  totalSecondsElapsed / 3600;
		int minutes = (totalSecondsElapsed % 3600) / 60;
		int seconds = totalSecondsElapsed - hours * 3600 - minutes * 60;
		
		return (String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
		
	}
	
	public double getStopTime() {
		
		return stopTime;
		
	}
	
	public double getStopTimeInSeconds() {
		
		return stopTime * 0.000000001;
		
	}
	
	public double getAverageTime() {
		
		if (numTimings != 0) {
			
			return timeSum / numTimings;
			
		} else {
			
			return 0.0;
			
		}
		
	}
	
	public double getAverageTimeInSeconds() {
		
		if (numTimings != 0) {
			
			return (timeSum / numTimings) * 0.000000001;
			
		} else {
			
			return 0.0;
			
		}
		
	}
	
	public double getNumTimings() {
		
		return numTimings;
		
	}
}
