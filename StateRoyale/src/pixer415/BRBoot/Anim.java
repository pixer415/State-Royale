package pixer415.BRBoot;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.LookupOp;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

// Handles creating the "slot machine" and "pedestal" animations.
public class Anim {
	public static BufferedImage list = null;
	public static BufferedImage pullScene = null;
	public static BufferedImage psblank = null;
	public static BufferedImage pullScene2 = null;
	public static BufferedImage watermark = null;
	public static final int FRAME_NUM = 15;
	public static final int WIN_LINGER = 15;
	public static final int ANIMATE_BY = 9;
	
	public static Font f = null;
    public static void pull(List<Region> regions, Region r, String caption, int place) throws IOException {
    	List<BufferedImage> frames = new ArrayList<BufferedImage>();
    	if (place < 0) {
    		// Creates list of remaining regions
    		list = new BufferedImage(60, (15 * regions.size()) + 1, BufferedImage.TYPE_INT_RGB);
    		Graphics2D lg = list.createGraphics();
    		for (int i = 0; i < regions.size(); i++) {
				lg.drawImage(regions.get(i).getLabel().getScaledInstance(60, 14, Image.SCALE_DEFAULT), 0, (15 * i) + 1, null);
    		}
    		lg.dispose();
    		// resets the "poll" scene
    		pullScene = psblank;
    		int id = Main.rando.nextInt(list.getHeight() - 14);
    		lg = pullScene.createGraphics();
    		// creates the actual animation
    		for (int i = 0; i < FRAME_NUM; i++) {
				lg.drawImage(list.getSubimage(0, id, 60, 14), null, 10, 13);
				id+=ANIMATE_BY;
				if (id > (list.getHeight() - 14)) {
					id = 0;
				}
				BufferedImage doggie = addCaption(Main.toBufferedImage(pullScene.getScaledInstance(Camera.outRes.width, Camera.outRes.height, Image.SCALE_DEFAULT)), caption); // Adds the caption on top
				ImageIO.write(doggie, "JPEG", Main.ffmpegInput);
			}
			lg.dispose();
			// Creates the "end" scene
			lg = pullScene2.createGraphics();
			lg.drawImage(list.getSubimage(0, (regions.indexOf(r) * 15) + 1, 60, 14), null, 10, 13);
			// Overlays semi-transparent black bar 
			lg.setColor(new Color(0, 0, 0, 192));
			lg.fillRect(0, 19, 80, 22);
            // Scales scene to output resolution
			pullScene = Main.toBufferedImage(pullScene2.getScaledInstance(Camera.outRes.width, Camera.outRes.height, Image.SCALE_DEFAULT));
			lg.dispose();
			// Adds text displaying region's name
			Graphics2D l2 = pullScene.createGraphics();
			BufferedImage txt = softScale(textToImage(r.getName(), f, Color.WHITE), 
					                     (int)Math.round((double)pullScene.getWidth() * 0.80), 
					                     (int)Math.round((double)pullScene.getHeight() * 0.30));
			l2.drawImage(txt, null, (pullScene.getWidth() / 2) - (txt.getWidth() / 2), (pullScene.getHeight() / 2) - (txt.getHeight() / 2));
			l2.dispose();
			// Adds the caption
			pullScene = addCaption(pullScene, caption);
			printMultiple(pullScene, WIN_LINGER);
    	} else {
    		// Made a separate module for readability; this is a common theme.
    		poal(frames, r, caption, place);
        }
    }
    
    // Text to image module grabbed from https://stackoverflow.com/questions/18800717/convert-text-content-to-image/18800845
    public static BufferedImage textToImage(String text, Font font, Color color) {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int width = fm.stringWidth(text);
        int height = fm.getHeight();
        width = width + 1;
        height = height + 1;
        g2d.dispose();
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setFont(font);
        fm = g2d.getFontMetrics();
        g2d.setColor(color);
        g2d.drawString(text, 0, fm.getAscent());
        g2d.dispose();
        return img;
    }
    
    // soft scaling solution, grabbed from https://stackoverflow.com/questions/10245220/java-image-resize-maintain-aspect-ratio
    public static BufferedImage softScale(BufferedImage img, double x, double y) { 
		int original_width = img.getWidth();
	    int original_height = img.getHeight();
	    int bound_width = (int) (x);
	    int bound_height = (int) (y);
	    int new_width = original_width;
	    int new_height = original_height;

	    // first check if we need to scale width
	    if (original_width > bound_width) {
	        //scale width to fit
	        new_width = bound_width;
	        //scale height to maintain aspect ratio
	        new_height = (new_width * original_height) / original_width;
	    }

	    // then check if we need to scale even with the new height
	    if (new_height > bound_height) {
	        //scale height to fit instead
	        new_height = bound_height;
	        //scale width to maintain aspect ratio
	        new_width = (new_height * original_width) / original_height;
	    }
	    return Main.toBufferedImage(img.getScaledInstance(new_width, new_height, Image.SCALE_SMOOTH));
    }
    
    // Caption adder. And also the watermark adder.
    public static BufferedImage addCaption(BufferedImage frame, String text) {
    	BufferedImage bar = new BufferedImage(Camera.outRes.width, (int)Math.round((double)Camera.outRes.height * 1.33333333), BufferedImage.TYPE_INT_RGB);
    	BufferedImage txt = softScale(textToImage(text, Anim.f, Color.WHITE), (int)Math.round((double)bar.getWidth() * 0.80), (int)Math.round((double)bar.getHeight() * 0.14));
    	BufferedImage wm = Main.setOpacity(softScale(watermark, (int)Math.round((double)bar.getWidth() * 0.80), (int)Math.round((double)bar.getHeight() * 0.125)), 0.5);
    	Graphics2D g = bar.createGraphics();
    	g.drawImage(txt, null, (Camera.outRes.width / 2) - (txt.getWidth() / 2), (Camera.outRes.height / 6) - (txt.getHeight() / 2));
    	g.drawImage(frame, null, 0, bar.getHeight() - frame.getHeight());
    	g.drawImage(wm, null, bar.getWidth() - wm.getWidth(), bar.getHeight() - wm.getHeight());
    	g.dispose();
    	return bar;
    }
    
    // Convenient way to export multiple instances of the same frame to the FFmpeg pipe.
    public static void printMultiple(BufferedImage print, int num2) throws IOException {
    	for (int i = 0; i < num2; i++) {
    		ImageIO.write(print, "JPEG", Main.ffmpegInput);
    	}
    }
    
    // Handles the "pedestal" animation in case the removed regions are decided by poll.
    public static void poal(List<BufferedImage> frames, Region r, String caption, int place) throws IOException {
    	// Handles medal color
    	BufferedImage bar = ImageIO.read(Main.class.getResource("/bar.png"));
    	bar = toARGB(bar);
    	Color entry = null;
    	switch (place) {
    	    case 0:
    	    	entry = new Color(255, 215, 0);
    	    	break;
    	    case 1:
    	    	entry = new Color(192, 192, 192);
    	    	break;
    	    case 2: 
    	    	entry = new Color(205, 127, 52);
    	    	break;
    	    default:
    	    	entry = Color.WHITE;
    	}
    	BufferedImageOp lookup = new LookupOp(new ColorMapper(Color.GREEN, entry), null);
    	bar = lookup.filter(bar, null);
    	// Adds region label to top of pedestal
    	bar.createGraphics().drawImage(r.getLabel().getScaledInstance(60, 14, Image.SCALE_DEFAULT), 0, 0, null);
    	// Resets the scene
    	pullScene = new BufferedImage(80, 60, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = pullScene.createGraphics();
        g.setColor(new Color(128, 128, 128));
        g.fillRect(0, 0, 80, 60);
        // Animates the pedestal
        for (int i = 0; i < FRAME_NUM; i++) {
            g.drawImage(bar, null, 10, 60 - (int)Math.round((double)bar.getHeight() * ((double)i / (double)(FRAME_NUM - 1))));
            ImageIO.write(addCaption(Main.toBufferedImage(pullScene.getScaledInstance(Camera.outRes.width, Camera.outRes.height, Image.SCALE_DEFAULT)), caption), "JPEG", Main.ffmpegInput);
        }
        // Adds transparent black bar
        g.setColor(new Color(0, 0, 0, 192));
		g.fillRect(0, 19, 80, 22);
        g.dispose();
        // Scales to output res and adds text displaying region's name
        pullScene = Main.toBufferedImage(pullScene.getScaledInstance(Camera.outRes.width, Camera.outRes.height, Image.SCALE_DEFAULT));
		Graphics2D l2 = pullScene.createGraphics();
		BufferedImage txt = softScale(textToImage(r.getName(), f, Color.WHITE), 
				                     (int)Math.round((double)pullScene.getWidth() * 0.80), 
				                     (int)Math.round((double)pullScene.getHeight() * 0.30));
		l2.drawImage(txt, null, (pullScene.getWidth() / 2) - (txt.getWidth() / 2), (pullScene.getHeight() / 2) - (txt.getHeight() / 2));
		l2.dispose();
		// Adds the caption
		pullScene = addCaption(pullScene, caption);
		printMultiple(pullScene, WIN_LINGER);		
    }
    
    // Prevents indexed image error (https://stackoverflow.com/a/19594979)
    private static BufferedImage toARGB(Image i) {
	    BufferedImage rgb = new BufferedImage(i.getWidth(null), i.getHeight(null), BufferedImage.TYPE_INT_ARGB);
	    rgb.createGraphics().drawImage(i, 0, 0, null);
	    return rgb;
	}
}
