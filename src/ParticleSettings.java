import java.awt.Color;

//This class holds all of the behavior settings on the particles


public class ParticleSettings {
	private static boolean showCursor = true;
    private static boolean attraction = true;
    private static boolean connectParticles = false;
    private static boolean loopIfOffScreen = false;
    private static boolean tripMode = false;
    private static boolean particleTrails = false;
    private static boolean clearNextFrame = false;
    private static boolean demoMode = false;
    
    //Circle angle in radians for demo mode
    private static double circleInc = 0;
    
    private static int distanceModifier = 0;
    
    //Number of particles for start of program
    private static int numP = 0;
    
    //Max amount of distance mod, positive and negative
    private static int maxDistMod = 3000;
    
    //Delay of millisecond that the color changes
    private static int colorScrollDelay = 15;
    
    //Color for particles, this changes in the color thread
    private static Color color = Color.RED;
    
    public static boolean isCursorShowing() { return showCursor; }
    public static boolean isAttraction() { return attraction; };
    public static boolean connectingParticles() { return connectParticles; }
    public static boolean loopIfOffScreen() { return loopIfOffScreen; }
    public static boolean isTripMode() { return tripMode; }
    public static boolean particleTrails() { return particleTrails; }
    public static boolean clearNextFrame() { return clearNextFrame; }
    public static boolean isDemoMode() { return demoMode; }
    
    public static void setCursorShowing(boolean b) { showCursor = b; }
    public static void setAttraction(boolean b) { attraction = b; }
    public static void setConnectParticles(boolean b) { connectParticles = b; }
    public static void setLoopIfOffScreen(boolean b) { loopIfOffScreen = b; }
    public static void setTripMode(boolean b) { tripMode = b; }
    public static void setParticleTrails(boolean b) { particleTrails = b; }
    public static void clearNextFrame(boolean b) { clearNextFrame = b; }
    public static void setDemoMode(boolean b) { demoMode = b; }
    
    public static double getCircleInc() { return circleInc; }
    public static int getDistanceModifier() { return distanceModifier; }
    public static int getNumP() { return numP; }
    public static int getMaxDistMod() { return maxDistMod; }
    public static int getColorScrollDelay() { return colorScrollDelay; }
    public static Color getColor() { return color; }
    
    public static void incDistMod(int i) { distanceModifier += i; }
    
    public static void incrementCircle(double i) { 
    	circleInc += i;
    	circleInc %= 360;
    }
    
    public static void setDistanceModifier(int d) {
    	distanceModifier = d;
    }
    
    public static void setNumP(int p) { numP = p; }
    public static void setMaxDistMod(int i) { maxDistMod = i; }
    public static void setColorScrollDelay(int i) { colorScrollDelay = i; }
    public static void setColor(Color c) { color = c; }
    
}