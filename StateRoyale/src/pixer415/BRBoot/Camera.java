package pixer415.BRBoot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

// Handles "camera control" for the map.
public class Camera {
	public static Dimension videoRes = new Dimension(720, 720);
	public static Dimension outRes = new Dimension(videoRes.width, (videoRes.height * 3) / 4);
	public static double aspectRatio = outRes.getWidth() / outRes.getHeight();
	public static int minSize = 140;
	public static Rectangle originalArea = new Rectangle(0, 0, Main.res[0], Main.res[1]);
	public static Color bg = new Color(128, 128, 128);
	public static double thickness = 5.0;
	// The main module.
    public static BufferedImage use(BufferedImage map, int[] limbs, boolean zoomIn) {
    	BufferedImage send = null;
    	if (zoomIn) {
    		Rectangle area = new Rectangle(limbs[0], limbs[1], limbs[2] - limbs[0], limbs[3] - limbs[1]);
    		Point centroid = new Point(area.x + (area.width / 2), area.y + (area.height / 2));
    		area.setLocation(centroid.x - (int)Math.round(area.getWidth() / 2.0), centroid.y - (int)Math.round(area.getHeight() / 2.0));
   		    area.grow(Math.max(0, (minSize - area.width) / 2), 
   		    		  Math.max(0, (minSize - area.height) / 2));
   		    send = letterbox(area, map);
    		return Main.toBufferedImage(send.getScaledInstance(outRes.width, outRes.height, Image.SCALE_DEFAULT));
    	} else {
    		Rectangle area = new Rectangle(0, 0, Main.res[0], Main.res[1]);
    		send = letterbox(area, map);
    		return Main.toBufferedImage(send.getScaledInstance(outRes.width, outRes.height, Image.SCALE_SMOOTH));
    	}
    }
    
    // Handles fitting the map within the confines of the camera area.
    private static BufferedImage letterbox(Rectangle area, BufferedImage map) {
    	BufferedImage send = null;
    	if (area.getWidth() / area.getHeight() > aspectRatio) {
		    	area.grow(0, ((int)(area.getWidth() / aspectRatio - area.getHeight()) / 2));
		    } else {
			    area.grow(((int)(area.getHeight() * aspectRatio - area.getWidth())) / 2, 0);
		    }
		if (!originalArea.contains(area)) {
			send = new BufferedImage(area.width, area.height, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = send.createGraphics();
			g.setColor(bg);
			g.fillRect(0, 0, area.width, area.height);
			g.setColor(Color.BLACK);
			g.setStroke(new BasicStroke((float) thickness));
			g.drawRect((area.x * -1) - 3, (area.y * -1) - 3, Main.res[0] + 5, Main.res[1] + 5);
			g.drawImage(map, null, area.x * -1, area.y * -1);
			g.dispose();
		} else {
			send = map.getSubimage(area.x, area.y, area.width, area.height);
		}
		return send;
    }
    
    // Changes the output resolution to a non-default value.
    public static void changeRes(int x, int y) {
    	videoRes = new Dimension(x, y);
    	outRes = new Dimension(videoRes.width, (videoRes.height * 3) / 4);
        aspectRatio = outRes.getWidth() / outRes.getHeight();
    }
}
