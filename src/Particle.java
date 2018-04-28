//This class is used to hold information about a particle and how they behave

public class Particle {
    private static int size = 1;
    
    double x = 0, y = 0, oldY = 0, oldX = 0;
    double velocityX = 0, velocityY = 0;
    
    //Index within the array, we use this to call the set method in the array list
    int index = 0;
    
    public Particle(double x, double y, double velocityX, double velocityY, int index) {
        this.x = x;
        this.oldX = x;
        this.oldY = y;
        this.y = y;
        this.index = index;
    }
    
    public static int getSize() { return size; }
    public static void setSize(int s) { size = s; }
    
    //Move it to a point
    public void attract(double x, double y) {
        double dx = x - this.x;
        double dy = y - this.y;
        
        double dist = (double)Math.sqrt((dx*dx) + (dy*dy));
        
        //Divide by 0 error fix
        if (dist == 0) dist = 1;
        if (dx == 0) dx = 1;
        if (dy == 0) dy = 1;
        
        //If the distance modifier is negative, then divide, if it is positive then multiply
        //if (Main.distanceModifier == 0) {
        if (ParticleSettings.getDistanceModifier() == 0) {
            this.x += dx / dist;
            this.y += dy / dist;
        }
        //else if (Main.distanceModifier < 0) {
        else if (ParticleSettings.getDistanceModifier() < 0) {
            //this.x += (dx / Math.abs(Main.distanceModifier)) / dist;
            //this.y += (dy / Math.abs(Main.distanceModifier)) / dist;
        	this.x += (dx / Math.abs(ParticleSettings.getDistanceModifier())) / dist;
            this.y += (dy / Math.abs(ParticleSettings.getDistanceModifier())) / dist;
        } else {
            this.x += (dx * ParticleSettings.getDistanceModifier()) / dist;
            this.y += (dy * ParticleSettings.getDistanceModifier()) / dist;
        }
    }
    
    //Calculate velocity
    public void integrate() {
        velocityX = this.x - this.oldX;
        velocityY = this.y - this.oldY;
        this.oldX = this.x;
        this.oldY = this.y;
        
        this.x += velocityX;
        this.y += velocityY;
        
        //Check if loop if off screen is true
        //Check if particle went out of bounds, if they did, spawn at other side
        if (ParticleSettings.loopIfOffScreen()) {
        	double newX = x;
        	double newY = y;
        	
        	if (x > Main.windowWidth)
        		newX %= Main.windowWidth;
        	else if (x < 0)
        		newX = Main.windowWidth;

        	if (y > Main.windowHeight)
        		newY %= Main.windowHeight;
        	else if (y < 0)
        		newY = Main.windowHeight;
        	
        	if (newX != x || newY != y)
        		Main.particles.set(this.index, new Particle(newX, newY, velocityX, velocityY, this.index));
        }
    }
}