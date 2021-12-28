package pixer415.BRBoot;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//Class to define the attributes of a region.
public class Region {
	
	private String name = "";
	private String code = "000";
	private int[] center = new int[2];
	public List<Integer> x = new ArrayList<Integer>();
	public List<Integer> y = new ArrayList<Integer>();
	private Color color = new Color(0, 0, 0);
	private int id = -1;
	private BufferedImage label = null;
	public int minX = Main.res[0];
	public int minY = Main.res[1];
	public int maxX = 0;
	public int maxY = 0;
	public Region() {
		// NOTHING!
	}
	public Region(String name, String code) {
		this.name = name;
		// Attempts to create the best abbreviation for a region if it does not currently exist.
		if (code == null) {
			String[] space = name.split(" ");
			if (space.length != 1) {
				StringBuilder c = new StringBuilder();
				for (String s : space) {
					if (Character.isUpperCase(s.charAt(0))) {
						c.append(s.charAt(0));
						c.append(".");
					}
				}
				this.code = c.toString();
			} else {
				if (space[0].length() > 3) {
				    this.code = Character.toString(space[0].charAt(0)).toUpperCase() +
				    			Character.toString(bestChar(space[0].substring(1, 3).toUpperCase())) +
				    		    Character.toString(bestChar(space[0].substring(space[0].length() - 2, space[0].length())));
				    if (isVowel(space[0].charAt(3))) {
				    	this.code = space[0].substring(0, 3).toUpperCase();
				    }
				} else {
					this.code = space[0].toUpperCase();
				}
			}
		} else {
			this.code = code;
		}
		this.code = stripAccents(this.code);
	}
	public Region(String pixer) {
		String[] spl = pixer.split("\",\"");
		this.name = spl[0];
		this.code = spl[1];
		String[] c = spl[2].split("-");
		this.color = new Color(Integer.parseInt(c[0]), Integer.parseInt(c[1]), Integer.parseInt(c[2]));
		this.id = Integer.parseInt(spl[3]);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	private static char bestChar(String sub) {
		sub = sub.toUpperCase();
	    if (!isVowel(sub.charAt(0))) {
	    	return sub.charAt(0);
	    } else if (isVowel(sub.charAt(0)) && isVowel(sub.charAt(1))) {
			return (char)Math.min((int)sub.charAt(0), (int)sub.charAt(1));
		} else {
			return (char)Math.max((int)sub.charAt(0), (int)sub.charAt(1));
		}
	}
	public static boolean isVowel(char c) {
		  return "AEIOUaeiou".indexOf(c) != -1;
	}
	public int[] getCenter() {
		return center;
	}
	public void setCenter() {
		int Xsum = this.x.stream().mapToInt(Integer::intValue).sum();
		int Ysum = this.y.stream().mapToInt(Integer::intValue).sum();
		int cx = (int)Math.round((double)Xsum / (double)this.x.size());
		int cy = (int)Math.round((double)Ysum / (double)this.y.size());
		this.center[0] = cx;
		this.center[1] = cy;
        List<Integer> sortedlist = new ArrayList<>(this.x);
        Collections.sort(sortedlist);
        this.minX = sortedlist.get(0);
        this.maxX = sortedlist.get(sortedlist.size() - 1);
        sortedlist = new ArrayList<>(this.y);
        Collections.sort(sortedlist);
        this.minY = sortedlist.get(0);
        this.maxY = sortedlist.get(sortedlist.size() - 1);
	}
 	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public List<Integer> getX() {
		return x;
	}
	public void setX(List<Integer> x) {
		this.x = x;
	}
	public List<Integer> getY() {
		return y;
	}
	public void setY(List<Integer> y) {
		this.y = y;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void makeLabel() {
		this.label = new BufferedImage(30, 7, BufferedImage.TYPE_INT_RGB);
		Graphics2D d = this.label.createGraphics();
		d.setColor(this.color);
		d.fillRect(1, 1, 5, 5);
		int p = 6;
        for (int i = 0; i < this.code.length(); i++) {
        	if (code.charAt(i) == '.') {
        		d.drawImage(Main.font.getSubimage(182, 0, 3, 7), p, 0, null);
        		p+=2;
        	} else {
        		d.drawImage(Main.font.getSubimage(((int)code.charAt(i) - 65) * 7, 0, 7, 7), p, 0, null);
        		p+=6;
        	}
        }
        d.dispose();
	}
	public BufferedImage getLabel() {
		return label;
	}
	public void setLabel(BufferedImage label) {
		this.label = label;
	}
	private String stripAccents(String s) 
	{
	    s = Normalizer.normalize(s, Normalizer.Form.NFD);
	    s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
	    return s;
	}
	public String toString() {
		return String.join("\",\"", this.name, this.code, String.join("-", Integer.toString(this.color.getRed()), Integer.toString(this.color.getGreen()), Integer.toString(this.color.getBlue())), Integer.toString(id));
	}
	public void addPoint(int x, int y) {
		try {
			this.x.add(x);
			this.y.add(y);
		} catch (ArrayIndexOutOfBoundsException aioobe) {
			
		}
	}
}
