package pixer415.BRBoot;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import de.jilocasin.nearestneighbour.kdtree.KdPoint;
import de.jilocasin.nearestneighbour.kdtree.KdTree;
import de.jilocasin.nearestneighbour.nnsolver.NNSolver;

public class Main {
	public static int[] mem2res = new int[2]; // Used for creating modules with mem2.txt.
	public static int[] res = new int[2];
	public static List<Region> regions = new ArrayList<Region>();
	public static Pixel[][] mp = null;
	public static BufferedImage devMap = null;
	public static boolean[][] otherLand; 
	public static Random rando = new Random();
	public static BufferedImage font = null;
	public static Rectangle boundingBox;
	public static int regionsNum = -1;
	public static BufferedImage lbl = null;
	public static List<Point> z = null;
	public static OutputStream ffmpegInput = null;
	private static BufferedImage geo;
	private static BufferedImage elv;
	public static void main(String[] args) throws Exception {
		// Pulls required files from the JAR's resources.
		try {
			font = ImageIO.read(Main.class.getResource("/alphabet.png"));
			Anim.pullScene = ImageIO.read(Main.class.getResource("/pullscene.png"));
			Anim.pullScene2 = ImageIO.read(Main.class.getResource("/pullscene2.png"));
			Anim.watermark = ImageIO.read(Main.class.getResource("/watermark.png"));
			GraphicsEnvironment ge = 
			    GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, Main.class.getResource("/Pixeled.ttf").openStream())); // The Pixeled font by DaFont user OmegaPC777 is used.
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Sets the blank pullScene and font variables in the Anim class 
		Anim.psblank = Anim.pullScene;
		Anim.f = new Font("Pixeled", Font.PLAIN, (int)Math.round((double)Camera.outRes.height / 3.0));
		
		// And here, the args are handled. Sorry, Apache Commons but I didn't want to deal with you.
		Args.handle(args);
	    
		// Code to create a new mode from mem2.txt.
//		List<String> reed = Files.readAllLines(Paths.get("mem2.txt"), Charset.forName("UTF-8"));
//		String[] st = reed.get(reed.size() - 1).split("\",\"")[0].split("x");
//		mem2res[0] = Integer.parseInt(st[0]);
//		mem2res[1] = Integer.parseInt(st[1]);
//		modeSetup(6, reed, "Oceania");
//		reed = null;
//		System.gc();
	}
	
	// Loads save file .zip from the hard disk.
	public static String saveLoader(String name) throws Exception {
			ZipFile zipFile = new ZipFile(name);
			BufferedImage pol = null;
			String[] reed = null;
			for (ZipEntry e : Collections.list(zipFile.entries())) {
			    if (e.getName().endsWith("pol.png")) {
			    	pol = ImageIO.read(zipFile.getInputStream(e));
			    } else if (e.getName().endsWith("elv.png")) {
			    	elv = ImageIO.read(zipFile.getInputStream(e));
			    } else if (e.getName().endsWith("geo.png")) {
			    	geo = ImageIO.read(zipFile.getInputStream(e)); // elv and geo are fields, to make saving maps easier
			    } else if (e.getName().endsWith("rgn.txt")) {
			    	reed = new String(zipFile.getInputStream(e).readAllBytes(), StandardCharsets.UTF_8).split("\n");
			    }
			}
			// creates the regions from rgn.txt
			for (int i = 1; i < reed.length; i++) {
		        Region r = new Region(reed[i]);
		        r.makeLabel();
				regions.add(r);
			}
			// Creates the main pixel array
			res[0] = pol.getWidth();
			res[1] = pol.getHeight();
			mp = new Pixel[pol.getWidth()][pol.getHeight()];
			otherLand = new boolean[pol.getWidth()][pol.getHeight()];
			for (int y = 0; y < pol.getHeight(); y++) {
				for (int x = 0; x < pol.getWidth(); x++) {
					if (pol.getRGB(x, y) != Color.BLACK.getRGB()) {
						if (pol.getRGB(x, y) == Color.GREEN.getRGB()) {
							otherLand[x][y] = true; // Used for null pixels. Ocean pixels do not exist in mem2 to save hard drive space.
						} else {
							Pixel p = new Pixel(x, y, geo.getRGB(x, y), elv.getRGB(x, y));
							Region rr = regions.get(((pol.getRGB(x, y) >> 16) & 0xFF) - 1);
							p.setRegion(rr.getId()); // In pixels, regions are identified by a region ID in order to ensure changes are made to *the* region and not made to a "forked" one.
							mp[x][y] = p;
							regions.get(((pol.getRGB(x, y) >> 16) & 0xFF) - 1).addPoint(x, y); // The addPoint method exists to center the labels on the map and to set the boundingBox variable.
						}
					}
				}
			}
			zipFile.close();
			return reed[0].split("-")[1];
	}
	
	// removes a region. The true "primary" method of the program.
	public static void eliminate(Region r, int n, int pl) throws Exception {
		System.out.println("Removing region: " + r.getName());
		String caption;
		String endCaption;
		if (n == regionsNum) {
			caption = "The first place out is....";
		} else {
		    switch (n) {
		        case -1:
		        	caption = "The first place out is....";
		        	break;
		        case 2:
		        	caption = "And the last place out is....";
		        	break;
		        default:
		        	caption = "And the next place out is....";
		    }
		}
		boundingBox = new Rectangle(r.minX, r.minY, r.maxX - r.minX, r.maxY - r.minY); // boundingBox is used to only read the part of "mp" that is needed. 
		
		// The System.out.printlns below explain the functions of the code
		
		System.out.println("Rendering \"picking\" animation.");
		Anim.pull(regions, r, caption, pl);
		
		System.out.println("Rendering \"place\" shot.");
		borderPrintOut(boundingBox, 10, true, r.getName());
		r.setColor(new Color(230, 230, 230));	
		renderLabels(r.getId());
		
		System.out.println("Rendering \"grey\" shot.");
		borderPrintOut(boundingBox, 16, true, r.getName());
		
		System.out.println("Partitioning area.");
	    landBorderKill(r);
	    
	    System.out.println("Partitioning \"ocean\" pixels.");
		if (nullBorderKill(r)) {
			System.out.println("Partitioning remaining area.");
			landBorderKill(r);
		}
		
		System.out.println("Rendering \"removed\" shot.");
		borderPrintOut(boundingBox, 16, true, r.getName());
		
		// Re-renders the labels and removes the region from the list.
		renderLabels(r.getId());
		regions.remove(r);
		
		// Renders out final "overview" shot.
		System.out.println("Rendering \"overview\" shot.");
        if (regions.size() == 1) {
			endCaption = "Congratulations to " + regions.get(0).getName() + " for winning.";
			borderPrintOut(boundingBox, 24, false, endCaption);
			System.out.print("[WINNER: " + regions.get(0).getName() + "] ");
		} else {
			endCaption = Integer.toString(regions.size()) + " places remain.";
			borderPrintOut(boundingBox, 24, false, endCaption);
			if (n == 2) {
	        	borderPrintOut(boundingBox, 8, false, "Which place do you want to get out?");
	        }
		}
        
		System.out.println("Removal complete.\n");
	}
	
	// Saves the modified map file as a .zip.
	public static void saveGame(String name, String ogName) throws Exception {
		// Creates the new pol.png.
		BufferedImage pol = new BufferedImage(res[0], res[1], BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < res[0]; x++) {
			for (int y = 0; y < res[1]; y++) {
				Pixel p = mp[x][y];
				if (p != null) {
					pol.setRGB(x, y, new Color(regions.indexOf(findRegion(p.getRegion())) + 1, 0, 0).getRGB());
				} else if (otherLand[x][y] == true) {
					pol.setRGB(x, y, Color.GREEN.getRGB());
				}
			}
		}
		// Creates the new rgn.txt.
		StringBuilder sb = new StringBuilder();
		sb.append("NaN-" + ogName + "\n");
        int newCount = 0;
		for (Region rr : regions) {
			sb.append(rr.toString().substring(0, rr.toString().lastIndexOf("\",\"")) + "\",\"" + newCount + "\n");
			newCount++;
		}
		// Writes the files to the new .zip.
		FileOutputStream fos = new FileOutputStream(name, false);
        ZipOutputStream zos = new ZipOutputStream(fos);
        zos.putNextEntry(new ZipEntry("geo.png"));
        ImageIO.write(geo, "PNG", zos);
        zos.closeEntry();
        zos.putNextEntry(new ZipEntry("elv.png"));
        ImageIO.write(elv, "PNG", zos);
        zos.closeEntry();
        zos.putNextEntry(new ZipEntry("pol.png"));
        ImageIO.write(pol, "PNG", zos);
        zos.closeEntry();
        zos.putNextEntry(new ZipEntry("rgn.txt"));
        byte[] bytes = sb.toString().getBytes();
        zos.write(bytes, 0, bytes.length);
        zos.closeEntry();
        zos.close();
	}
	
	// Sets up the FFmpeg pipe.  This may be problematic in a Discord bot setting.
	public static void printVideo(String out) throws Exception {
		File ffmpeg_output_msg = new File("ffmpeg_output_msg.txt"); // redirects output
		//String ffmpeg = "ffmpeg";
		ProcessBuilder pb = new ProcessBuilder(
		        "ffmpeg","-r","20","-f","image2pipe",
		        "-s",Integer.toString(Camera.outRes.width) + "x" + Integer.toString((int)Math.round((double)Camera.outRes.height * 1.33333333)),"-i","pipe:0","-vcodec",
		        "libx264","-crf","25","-pix_fmt","yuv420p",
		        out);
		pb.redirectErrorStream(true);
		pb.redirectOutput(ffmpeg_output_msg);
		pb.redirectInput(ProcessBuilder.Redirect.PIPE);
		Process p = pb.start();
		ffmpegInput = p.getOutputStream();
	}
	
	// Handles the partitioning of pixels that border another region.
	public static void landBorderKill(Region r) throws Exception {
		int num = -1;
		boolean first = true;
		z = null;
		do {
			List<Integer> dList = fine(mp, r, 0); // Grabs list of land pixels.
			num = dList.size();
			if (num == 4) {
				if (first) {
            	    return; // Prevents an unnecessary process for "island" regions.
				} else {
					continue;
				}
            }
			first = false;
			for (int i = 2; i < num / 2; i++) {
				pixelKill(dList.get(i * 2), dList.get((i * 2) + 1), r);
			}
			z = z.stream().distinct().collect(Collectors.toList());
			borderScanP(z);
		} while (num > 4);
	}
	
	// Made a separate module to make things easier.
	public static void pixelKill(int x, int y, Region r) {
		int[] xx = {x-1, x, x+1, x};
		int[] yy = {y, y-1, y, y+1};
		double chanceH = 0.0;
		int xd = 0;
		int yd = 0;
		for (int i = 0; i < 4; i++) {
		  try {
		  if (mp[xx[i]][yy[i]] != null) {
			 // The elevation map and satellite image are considered based on similarity. Intended to add a degree of realism to the partitioned region borders.
		     double ediff = 1.00 - Math.abs(mp[x][y].getElvSim() - mp[xx[i]][yy[i]].getElvSim());
		     double gdiff = 1.00 - Math.abs(mp[x][y].getGeoSim() - mp[xx[i]][yy[i]].getGeoSim());
		     double chance = ediff * gdiff;
		     // The pixel with the highest "chance" is used, in case of multiple bordering regions.
			 if (mp[xx[i]][yy[i]].getRegion() != r.getId() && chance > chanceH) {
			     chanceH = chance;
			     xd = xx[i];
			     yd = yy[i];
			 }
		  }
		  } catch (ArrayIndexOutOfBoundsException aioobe) {
			  // in case of edge pixel
		  }
		}
		// The "chance" is enforced using a random value. If the number divided by 100 happens to be greater than chanceH, then the pixel is saved for a later iteration. 
		if (((double)rando.nextInt(101) / 100.0) < chanceH) {
			mp[x][y].setRegion(mp[xd][yd].getRegion());
			Region rrt = findRegion(mp[xd][yd].getRegion());
		    rrt.addPoint(x, y);
		}
	}
     
	// Handles "rogue" pixels that border the ocean. Think islands. landBorderKill may be called again to deal with the resulting "land" border pixels from this process.
    public static boolean nullBorderKill(Region r) throws Exception { // boolean to control whether landBorderKill is called again.
		int num2 = -1;
		z = null;
		// A list of pixels is found.
		List<Integer> dList = fine(mp, r, 1);
		num2 = dList.size() / 2;
		if (num2 == 2) {
			return false;
		}
		// Prepares a Multi-Executor: a solution for multi-threading.
		int cc = 0;
		int atATime = Math.max(Runtime.getRuntime().availableProcessors() / 2, 1);
		if (num2 > 0) {
			ExecutorService executor;
	    if (num2 < atATime) {
		    executor = Executors.newFixedThreadPool(num2);
	    } else {
	    	executor = Executors.newFixedThreadPool(atATime);
	    }
	    // A KdTree solution from Github user Jilocasin is used. This may fail so a backup solution is implemented. (see nullKill) 
	    List<KdPoint<Integer>> borders = new ArrayList<>();
		List<Pixel> ls = Arrays.stream(mp).flatMap(Arrays::stream)
		.filter(pxl -> (pxl != null && pxl.getRegion() != r.getId() && pxl.getRegion() != -1 && (pxl.isBorderWithNull() || pxl.isBorderWithOL())))
		.collect(Collectors.toList());
		ls.stream().forEach(x -> borders.add(new KdPoint<>(x.getX(), x.getY())));
		KdTree<Integer> tree = new KdTree<>(borders);
		// A MultiExecutor solution is used to increase performance. From https://stackoverflow.com/questions/46426120/how-to-start-a-thread-for-an-operation-at-each-iteration-of-for-loop
		for (int i = 2; i < dList.size() / 2; i++) {
			Runnable worker = new MultiExecutor(dList, i, tree, ls, r);
			executor.execute(worker);
			cc++;
			if (cc == atATime || i == num2 - 1) {
				cc = 0;
				executor.shutdown();
				while (!executor.isTerminated()) {
		        }
				executor = Executors.newFixedThreadPool(atATime);
			}
		}
		}
		// z is made unique.
		z = z.stream().distinct().collect(Collectors.toList());
		borderScanP(z);
		return true;
    }
	
    // Again, the specific pixel process is made a separate module. An alternative solution is included in case of a failure with the KdTree implementation from Jilo.
	public static void nullKill(int x, int y, KdTree<Integer> tree, List<Pixel> ls, Region r) {
		try {
			NNSolver<Integer> solver = new NNSolver<>(tree);
			KdPoint<Integer> searchPoint = (new KdPoint<>(x, y));
	        KdPoint<Integer> nearestPoint = solver.findNearestPoint(searchPoint);
	        Pixel p = mp[nearestPoint.getAxisValue(0)][nearestPoint.getAxisValue(1)];
	        mp[x][y].setRegion(p.getRegion());
			Region rr = findRegion(p.getRegion());
			rr.addPoint(x, y);
		} catch (Exception ert) {
			// Backup solution in case the KdTree implementation fails.
			Pixel p = ls.stream().sorted(Comparator.comparingDouble(user -> distance(mp[x][y].getX(), mp[x][y].getY(), user.getX(), user.getY()))).collect(Collectors.toList()).get(0);
			mp[x][y].setRegion(p.getRegion());
			Region rr = findRegion(p.getRegion());
			rr.addPoint(x, y);
			System.err.print("[backup used]");
		}
	}
	
	// Fits boundingBox within the confines of the map.
	public static void boxResize(Rectangle boundingBox) {
		boundingBox.setSize(Math.min(boundingBox.width, res[0]), Math.min(res[1], boundingBox.height));
		boundingBox.setLocation(Math.max(Math.min(boundingBox.x, res[0] - 1 - boundingBox.width), 0), 
				                Math.max(Math.min(boundingBox.y, res[1] - 1 - boundingBox.height), 0));	
	}
	
	// Locates border pixels in a given area. Found to be faster than stream-related methods, hence the module's name.
	public static List<Integer> fine(Pixel[][] mp, Region r, int option) {
		List<Point> newz = new ArrayList<Point>();
		List<Integer> ppp = new ArrayList<Integer>();
		ppp.add(r.minX);
		ppp.add(r.minY);
		ppp.add(r.maxX);
		ppp.add(r.maxY); // bounds are added for easy resizing of boundingBox. Ends up unused but left in due to compatibility errors I'm too lazy to fix.
		// depends on if z is null. If so, boundingBox is used
		if (z != null && z.size() != 0) {
			for (int i = 0; i < z.size(); i++) {
			     Point p = z.get(i);
			     verifyPixel(p.x, p.y, ppp, r, option, newz);
			}
		} else {
			int[] limbs = limbsSetup(boundingBox);
			for (int x = limbs[0]; x < limbs[2]; x++) {
				for (int y = limbs[1]; y < limbs[3]; y++) {
					verifyPixel(x, y, ppp, r, option, newz);
				}
			}
		}
		z = newz;
		return ppp;
	}
	
	// Adds a pixel's x/y values to the List<Integer>. Used due to difficulties with using a List<Point>.
	public static void verifyPixel(int x, int y, List<Integer> ppp, Region r, int option, List<Point> newz) {
		Pixel cmp = mp[x][y];
		if (mp[x][y] != null) {
			if (r.getId() == cmp.getRegion() && cmp.isBorder() && option == 0) {
				ppp.add(x);
				ppp.add(y);
				addToZed(x, y, newz);
			} else if (r.getId() == cmp.getRegion() && cmp.isBorderWithNull() && option == 1) {
				ppp.add(x);
				ppp.add(y);
				addToZed(x, y, newz);
			}
		}
	}
	
	// Adds a pixel and the eight pixels surrounding it to "z." Used to make the region removal process significantly faster. 
	public static void addToZed(int x, int y, List<Point> z) {
		for (int yy = -1; yy < 2; yy++) {
			for (int xx = -1; xx < 2; xx++) {
				Point p = new Point(Math.min(Math.max(x + xx, 0), Main.res[0] - 1),
						            Math.min(Math.max(y + yy, 0), Main.res[1] - 1));
				z.add(p);
			}
		}
	}
	
	// Distance formula solution.
	public static double distance(int x1, int y1, int x2, int y2) {
		double p1 = Math.pow((x2-x1), 2);
		double p2 = Math.pow((y2-y1), 2);
		return Math.sqrt(p1 + p2);
	}

	//Creates "starting" mode file from mem2.txt. mem2 is a list of pixels and metadata for the entire world.
	public static void modeSetup(int cond, List<String> reed, String modeName) throws Exception {
		int[] bounds;
		// List of available modes are here. Both the lat/long bounds and "type" names are covered.
		double[][] ll = {{82.0, 6.4, -180.0, -50.0}, 
				         {72.0, 17.0, -180.0, -64.0}, 
				         {13.0, -57.1, -84.0, -32.3}, 
				         {72.5, 34.5, -25.5, 59.2}, 
				         {82.1, -12.0, 25.5, 176.0}, 
				         {38.0, -35.5, -27.7, 63.4}, 
				         {29.0, -53.0, -180.0, 180.0}, 
				         {90.0, -90.0, -180.0, 180.0}};
		String[] name = {"NORTH AMERICAN", "US STATE", "SOUTH AMERICAN", "EUROPEAN", "ASIAN", "AFRICAN", "OCEANIC", "WORLDWIDE"};
		bounds = convertLatLong(ll[cond]);
		res[0] = bounds[3] - bounds[2] + 1;
		res[1] = bounds[1] - bounds[0] + 1;
		BufferedImage pol = new BufferedImage(res[0], res[1], BufferedImage.TYPE_INT_RGB);
		BufferedImage geo = new BufferedImage(res[0], res[1], BufferedImage.TYPE_INT_RGB);
		BufferedImage elv = new BufferedImage(res[0], res[1], BufferedImage.TYPE_INT_RGB);
		lbl = new BufferedImage(res[0], res[1], BufferedImage.TYPE_INT_ARGB);
		System.out.println(reed.size());
		for (int lin = 0; lin < reed.size(); lin++) {
			// Parses this line of mem2 and creates a pixel.
			String line = reed.get(lin);
			String[] opts = line.split("\",\"");
			Pixel pix = new Pixel(line);
			// Checks to see if this pixel is "valid" for this mode.
			boolean isInB = isInBounds(pix.getX(), pix.getY(), bounds);
			boolean[] sw = condition(cond, opts);
			if (isInB && !opts[3].equals("3")) {
				// Determines whether to consider a country or a subdivision a region. The region name and code are obtained here.
				if (sw[0]) {
				String rN;
				String code;
				if (sw[1]) {
					rN = opts[7];
					code = null;
				} else {
					rN = opts[5];
					code = opts[6];
				}
				// Creates the region if it doesn't already exist
				Region r = findRegion(rN);
				if (r == null) {
					r = new Region(rN, code);
					System.out.println(r.getCode());
					r.setColor(new Color(25 + rando.nextInt(195), 25 + rando.nextInt(195), 25 + rando.nextInt(195)));
					r.setId(regions.size());
					r.makeLabel();
					regions.add(r);
				}
				// Creates png files
				pix.setRegion(r.getId());
				pol.setRGB(pix.getX() - bounds[2], pix.getY() - bounds[0], new Color(r.getId() + 1, 0, 0).getRGB());
				geo.setRGB(pix.getX() - bounds[2], pix.getY() - bounds[0], pix.getGeo().getRGB());
				elv.setRGB(pix.getX() - bounds[2], pix.getY() - bounds[0], pix.getElv().getRGB());
				} else {
					pol.setRGB(pix.getX() - bounds[2], pix.getY() - bounds[0], Color.GREEN.getRGB());
				}
			}
		}
		// Creates the text for rgn.txt
		StringBuilder sb = new StringBuilder();
		sb.append("NaN-" + name[cond] + "\n");
        int newCount = 0;
		for (Region rr : regions) {
			sb.append(rr.toString().substring(0, rr.toString().lastIndexOf("\",\"")) + "\",\"" + newCount + "\n");
			newCount++;
		}
		// Writes to the .zip
		FileOutputStream fos = new FileOutputStream(modeName + ".zip", false);
        ZipOutputStream zos = new ZipOutputStream(fos);
        zos.putNextEntry(new ZipEntry("geo.png"));
        ImageIO.write(geo, "PNG", zos);
        zos.closeEntry();
        zos.putNextEntry(new ZipEntry("elv.png"));
        ImageIO.write(elv, "PNG", zos);
        zos.closeEntry();
        zos.putNextEntry(new ZipEntry("pol.png"));
        ImageIO.write(pol, "PNG", zos);
        zos.closeEntry();
        zos.putNextEntry(new ZipEntry("rgn.txt"));
        byte[] bytes = sb.toString().getBytes();
        zos.write(bytes, 0, bytes.length);
        zos.closeEntry();
        zos.close();
	}
	
	// Condition that has to be met for a line in mem2 to be made into a non-null pixel.
	public static boolean[] condition(int cond, String[] opts) {
		boolean[] rzz = new boolean[2];
		switch (cond) {
		  case 0:
			  rzz[0] = opts[4].contains("NA");
			  rzz[1] = opts[6].equals("USA") || opts[6].equals("CAN") || opts[6].equals("MEX");
			  break;
		  case 1:
			  rzz[0] = opts[6].equals("USA");
			  rzz[1] = true;
			  break;
		  case 2:
			  rzz[0] = opts[4].contains("SA");
			  rzz[1] = false;
			  break;
		  case 3:
			  rzz[0] = opts[4].contains("EU");
			  rzz[1] = opts[6].equals("GBR");
			  break;
		  case 4:
			  rzz[0] = opts[4].contains("AS");
			  rzz[1] = false;
			  break;
		  case 5: 
			  rzz[0] = opts[4].contains("AF");
			  rzz[1] = false;
			  break;
		  case 6:
			  rzz[0] = opts[4].contains("OC");
			  rzz[1] = opts[6].equals("AUS");
			  break;
		  case 7:
			  rzz[0] = true;
			  rzz[1] = false;
			  break;
		  default:
			  System.err.println("error");
		}
		return rzz;
	}
	
	// BufferedImage converter from https://stackoverflow.com/a/13605411
	public static BufferedImage toBufferedImage(Image img)
	{
	    if (img instanceof BufferedImage)
	    {
	        return (BufferedImage) img;
	    }

	    // Create a buffered image with transparency
	    BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

	    // Draw the image on to the buffered image
	    Graphics2D bGr = bimage.createGraphics();
	    bGr.drawImage(img, 0, 0, null);
	    bGr.dispose();

	    // Return the buffered image
	    return bimage;
	}
	
	// Checks to see if x/y is within a set of int bounds.
	public static boolean isInBounds(int x, int y, int[] bounds) {
		if(bounds[2] <= x && x <= bounds[3] && bounds[0] <= y && y <= bounds[1]) {
		    return true;
		} else {
			return false;
		}
	}
	
	// Converts latitude/longitude to the equivalent x/y position in mem2.
	public static int[] convertLatLong(double[] bounds) {
		int[] converted = new int[bounds.length];
		for (int i = 0; i < bounds.length; i++) {
			if (i > 1) {
				bounds[i] = bounds[i] + 180.0;
				bounds[i] = bounds[i] / 360.0;
				converted[i] = (int)(bounds[i] * (double)mem2res[0]);
			} else {
				bounds[i] = bounds[i] + 90.0;
				bounds[i] =  1.0 - (bounds[i] / 180.0);
				converted[i] = (int)(bounds[i] * (double)mem2res[1]);
			}
		}
		return converted;
	}
	
	// Prints out map frames to FFmpeg.
	public static void borderPrintOut(Rectangle subBlock, int frameNum, boolean zoomIn, String caption) throws IOException {
		int[] limbs = limbsSetup(subBlock);
		if (devMap == null) {
			devMap = new BufferedImage(res[0], res[1], BufferedImage.TYPE_INT_RGB);
		}
		// Prints pixels using bounds.
		for (int y = limbs[1]; y < limbs[3]; y++) {
			for (int x = limbs[0]; x < limbs[2]; x++) {
				printPixel(x, y);
			}
		}
		// The map and label overlay are combined. 
		BufferedImage print = new BufferedImage(res[0], res[1], BufferedImage.TYPE_INT_RGB);
		Graphics2D g = print.createGraphics();
		g.drawImage(devMap, null, 0, 0);
		g.drawImage(lbl, null, 0, 0); 
		g.dispose();
		// The camera system is applied and the caption is added.
		print = Anim.addCaption(Camera.use(print, limbs, zoomIn), caption);
		Anim.printMultiple(print, frameNum);
	}
	
	// Handles individual pixels for borderPrintOut.
	public static void printPixel(int x, int y) {
		if (mp[x][y] != null) {
			//System.out.println(mp[x][y].getRegion());
			Color bc = null;
			try {
			    bc = findRegion(mp[x][y].getRegion()).getColor();
			} catch (Exception e) {
				// Used in case a region is not found. The closest pixel is found using the KdTree solution.
				Region rrr = findRegion(mp[x][y].getRegion());
				List<KdPoint<Integer>> borders = new ArrayList<>(); 
				List<Pixel> ls = Arrays.stream(mp).flatMap(Arrays::stream)
				.filter(user -> (user != null && user.getRegion() != mp[x][y].getRegion() && (user.isBorderWithNull() || user.isBorderWithOL()))).collect(Collectors.toList());
				ls.stream().forEach(xld -> borders.add(new KdPoint<>(xld.getX(), xld.getY())));
				KdTree<Integer> tree = new KdTree<>(borders);
				nullKill(x, y, tree, ls, rrr);
				try {
					borderScan(mp[x][y]);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.err.println("ERROR FOUND. USING BANDAID.");
			}
			if (mp[x][y].isBorderWithNull() || mp[x][y].isBorder() || mp[x][y].isBorderWithOL()) {
				bc = Color.BLACK;
			}
			devMap.setRGB(x, y, bc.getRGB());
		} else {
			if (otherLand[x][y] != true) {
		        devMap.setRGB(x, y, new Color(153, 217, 234).getRGB());
			} else {
				devMap.setRGB(x, y, Color.WHITE.getRGB());
			}
		}
	}
	
	// The following six methods scan the map to refresh what pixels are counted as borders. Multiple different ways to define the area to be scanned are covered.
	public static void borderScan() throws Exception { // entire map
		scanBlock(0, res[0], 0, res[1]);
	}
	
	public static void borderScanP(List<Point> z) { // z
		for (Point p : z) {
			scanPixel(p.x, p.y);
		}
	}
	
	public static void borderScan(Pixel pixel) { // specific pixel and surrounding area
		scanBlock(Math.max(pixel.getX(), 0), Math.min(pixel.getY() + 2, res[0]), Math.max(pixel.getX() - 1, 0), Math.min(pixel.getY() + 2, res[1]));
	}
	
	public static void scanBlock(int l0, int l2, int l1, int l3) { // within a set of bounds
		for (int y = l1; y < l3; y++) {
			for (int x = l0; x < l2; x++) {
				scanPixel(x, y);
			}
		}
	}
	
	public static void scanPixel(int x, int y) { // the actual job is done here
		if (mp[x][y] != null) {
			  mp[x][y].setBorder(false);
			  mp[x][y].setBorderWithNull(false);
			  mp[x][y].setBorderWithOL(false);
			  int[] xx = {x-1, x, x+1, x};
			  int[] yy = {y, y-1, y, y+1};
			  for (int i = 0; i < 4; i++) {
				try {
					if (mp[xx[i]][yy[i]] == null) {
						if (otherLand[xx[i]][yy[i]]) {
							mp[x][y].setBorderWithOL(true);
						} else {
							mp[x][y].setBorderWithNull(true);
						}
					} else {
						if (mp[xx[i]][yy[i]].getRegion() != mp[x][y].getRegion()) {
							mp[x][y].setBorder(true);
						}
					}
				} catch (ArrayIndexOutOfBoundsException aiooobe) {
					// Used in the case of an "edge" pixel
				}
			}
		}
	}
	
	// Two modules to find a region by name or by ID.
	public static Region findRegion(String search) {
		Region tmp = null;
		for (int i = 0; i < regions.size(); i++) {
			if (regions.get(i).getName().equalsIgnoreCase(search)) {
				tmp = regions.get(i);
			}
		}
	    return tmp;
	}
	
	public static Region findRegion(int id) {
		Region tmp = null;
		for (int i = 0; i < regions.size(); i++) {
			if (regions.get(i).getId() == id) {
				tmp = regions.get(i);
			}
		}
	    return tmp;
	}
	
	// Converts boundingBox coordinates to simple subsection integer bounds. 2 pixels are added on each side for wiggle room.
	public static int[] limbsSetup(Rectangle subBlock) {
		int[] limbs = {0, 0, res[0], res[1]};
		if (subBlock != null) {
			limbs[0] = Math.max(subBlock.x - 2, 0);
			limbs[1] = Math.max(subBlock.y - 2, 0);
			limbs[2] = Math.min(subBlock.x + subBlock.width + 2, res[0]);
			limbs[3] = Math.min(subBlock.y + subBlock.height + 2, res[1]);
		}
		return limbs;
	}
	
	// sets opacity of a BufferedImage. Made with help from https://stackoverflow.com/questions/11552092/changing-image-opacity
	public static BufferedImage setOpacity(BufferedImage b, double o) {
		 float alpha = (float) o;
		 Image tmp = b;
		 BufferedImage dimg = new BufferedImage(b.getWidth(), b.getHeight(), BufferedImage.TYPE_INT_ARGB);
		 Graphics2D g = dimg.createGraphics();
		 AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
		 g.setComposite(ac);
	     g.drawImage(tmp, 0, 0, null);
	     g.dispose();
	   	return dimg;
	}
	
	// Renders the label overlay for the map shots in the video. 
	public static void renderLabels(int bl) {
		lbl = new BufferedImage(res[0], res[1], BufferedImage.TYPE_INT_ARGB);
		Graphics2D lb = lbl.createGraphics();
        List<Rectangle> rex = new ArrayList<Rectangle>(); // Used to check for intersections.
		for (Region r : regions) {
			if (r.getId() != bl) {
				// Center of each region is set here.
				r.setCenter(); 
				// Scales the label based on region size. Integer scaling is used to prevent a "wonky" appearance in the final video.
				double scale = (int)Math.round((((double)r.x.size() / 10000.0))) + 1.0;
				if (scale > 7.0) {
					scale = 7.0;
				}
				int h = (int)(7.0 * scale);
				int w = (int)(30.0 * scale);
				int x = (int)(14.0 * scale);
				int y = (int)(3.0 * scale);
				BufferedImage sl = toBufferedImage(r.getLabel().getScaledInstance(w, h, Image.SCALE_DEFAULT));
				// Makes labels transparent based on intersections.
				Rectangle rrr = new Rectangle(r.getCenter()[0] - x, r.getCenter()[1] - y, w, h);
				int pow = 0;
				for (Rectangle rec : rex) {
					if (rrr.intersects(rec)) {
						pow++;
					}
				}
				sl = setOpacity(sl, Math.pow(0.60, pow));
				rex.add(rrr);
				// Draws the label on the overlay.
				lb.drawImage(sl, null, r.getCenter()[0] - x, r.getCenter()[1] - y);
			}
		}
		lb.dispose();
	}
	
	// Hippity hoppity, T1C's solution from https://github.com/ThatOneCalculator/Among-Us-Dumpy-Gif-Maker/blob/main/src/main/java/dev/t1c/dumpy/sus.java is now my property
	public static boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().startsWith("windows");
	}
}
