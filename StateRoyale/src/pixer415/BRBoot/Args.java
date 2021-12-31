package pixer415.BRBoot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URLConnection;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

// Args handler. 
public class Args {
	public static int endArg = 0;
	public static Scanner input = new Scanner(System.in);
	public static final String OVERWRITE_WARNING = "This file already exists. Would you like to overwrite it?";
	public static final String YN_PROMPT = "\n(Y/N): ";
    public static String help = "";
    public static void handle(String[] args) throws Exception {
    	// Handles end argument
    	if (args.length > 1) {
	    	System.out.println("Reading args.....");
	    	if (args[args.length - 1].equalsIgnoreCase("-OO")) {
	    		endArg = 1;
	    		args = Arrays.copyOf(args, args.length-1);
	    	} else if (args[args.length - 1].equalsIgnoreCase("-NO")) {
	    		endArg = 2;
	    		args = Arrays.copyOf(args, args.length-1);
	    	} else if (isValidResArg(args[args.length - 1])) {
	    		String[] sp = args[args.length-1].split("x");
	    		Camera.changeRes(Integer.parseInt(sp[0].substring(1)), Integer.parseInt(sp[1]));
	    		// Copy of the end argument handler. So both the resolution argument and the overwrite-related arguments can be used.
	    		args = Arrays.copyOf(args, args.length-1);
	    		if (args[args.length - 1].equalsIgnoreCase("-OO")) {
	        		endArg = 1;
	        		args = Arrays.copyOf(args, args.length-1);
	        	} else if (args[args.length - 1].equalsIgnoreCase("-NO")) {
	        		endArg = 2;
	        		args = Arrays.copyOf(args, args.length-1);
	        	}
	    	} 
    	} else {
    		if (args.length == 0 || args[args.length - 1].equals("-help")) {
    			System.out.println("\n" + help);
        		System.exit(0);
    		} else {
    			System.err.println("error: unknown end arg error - exiting");
        		System.exit(0);
    		}
    	}
    	// Handles botched commands that are less than 3 args long.
    	if (args.length < 3) {
    		System.err.println("error: not enough args - exiting");
    		System.exit(0);
    	// Handles commands that are 3 args long.
    	} else if (args.length == 3) {
            File f = assrrt(args[0]);
            String name = Main.saveLoader(f.toString());
            if (args[1].equalsIgnoreCase("-ls")) {
            	// Prints the list of remaining regions to a text file. Useful for a potential Discord bot's polling function.
            	args[2] = overWriteOrNo(args[2], ".txt", false);
            	StringBuilder sb = new StringBuilder();
        		for (Region r : Main.regions) {
        			sb.append(r.getName() + "\",\"");
        		}
        		BufferedWriter writer = new BufferedWriter(new FileWriter(args[2]));
        	    writer.write(sb.toString());
        	    writer.close();
        	    System.out.println("List of regions printed.");
            } else if (args[1].equalsIgnoreCase("-rm")) {
            	// Removes all remaining regions at random.
            	args[2] = overWriteOrNo(args[2], "video", false);
            	System.out.println("Preparing ffmpeg export.");
            	Main.printVideo(args[2]);
            	System.out.println("Rendering initial \"overview.\"\n");
        		Main.borderScan();
        		Main.renderLabels(-1);
        		Main.borderPrintOut(null, 40, false, name + " BATTLE ROYALE!");
            	int n = -1;
            	Main.regionsNum = Main.regions.size();
            	do {
    				n = Main.regions.size();
    				if (n == 1) {
    					continue;
    				}
    				Region region = Main.regions.get(Main.rando.nextInt(n));
    				System.out.print("(" + (n-1) + ") ");
    			    Main.eliminate(region, n, -1);
    				Main.boundingBox = null;//
    			} while (n > 1);
            } else {
            	System.err.println("Error: second arg invalid - exiting");
            	System.exit(0);
            }
        // Handles the "other" commands.
    	} else {
    		File f = assrrt(args[0]);
    		args[1] = overWriteOrNo(args[1], ".zip", args[0].equals(args[1]));
    		args[2] = overWriteOrNo(args[2], "video", false);
    		String name = Main.saveLoader(f.toString());
    		System.out.println("Preparing ffmpeg export.");
        	Main.printVideo(args[2]);
        	System.out.println("Rendering initial \"overview.\"\n");
    		Main.borderScan();
    		Main.renderLabels(-1);
    		Main.borderPrintOut(null, 40, false, name + " BATTLE ROYALE!");
        	if (args[3].equals("-rm!") || args[3].equals("-rm")) {
        		if (isNumeric(args[4])) {
        			// Removes (args[4]) number of regions, which are randomly picked.
        			int l = Integer.parseInt(args[4]);
        			if (Main.regions.size() < l) {
        				l = Main.regions.size() - 1;
        			}
        		    for (int i = 0; i < l; i++) {
        		    	Region tmp = Main.regions.get(Main.rando.nextInt(Main.regions.size()));
        		    	if (tmp == null || Main.regions.size() == 1) {
        		    		i = l;
        		    		continue;
        		    	}
        		    	int n = 1;
        		    	if (i == 0) {
        		    		n = -1;
        		    	} else if (i == (l - 1) || Main.regions.size() == 2) {
        		    		n = 2;
        		    	}
        		    	System.out.print("(" + (l-i) + ") ");
        		    	Main.eliminate(tmp, n, -1);
        		    }
        		} else {
        			// Removes the specified regions. When -rm! is used, the regions are removed in the specified order. Otherwise, the regions are shuffled.
        			List<Region> rgns = new ArrayList<Region>();
        			for (int i = 4; i < args.length; i++) {
        				Region tmp = Main.findRegion(args[i]);
        				if (tmp == null) {
        					System.err.println("Error: region not found - exiting");
        	            	System.exit(0);
        				} else if (!rgns.contains(tmp)) {
        					rgns.add(tmp);
        				}
        			}
        			if (!args[3].endsWith("!")) {
        				Collections.shuffle(rgns);
        			}
        			int n = rgns.size() + 1;
        			Main.regionsNum = n;
        			for (Region r : rgns) {
        				System.out.print("(" + (n-1) + ") ");
        				Main.eliminate(r, n, -1);
        				n--;
        			}
        		}
        	} else if (args[3].equals("-pl")) {
        		// Same as -rm!, but with the "pedestal" animation. Should be used with the Discord bot's pull function.
    			for (int i = 4; i < args.length; i++) {
    				Region tmp = Main.findRegion(args[i]);
    				if (tmp == null) {
    					System.err.println("Error: region not found - exiting");
    	            	System.exit(0);
    				} else {
    					int n = 1;
    					if (i == 4) {
    						n = -1;
    					} else if (i == args.length - 1) {
    						n = 2;
    					}
    					System.out.print("(" + (args.length - i) + ") ");
    					Main.eliminate(tmp, n, i - 4);
    				}
    			}
        	}
        	Main.saveGame(args[1], name);
    	}
    	
    }
    
    // Simple method to check "if the file exists"-related conditions.
    private static String overWriteOrNo(String arg, String ext, boolean same) {
    	boolean cond = isPathValid(arg, ext);
    	if (ext.equals("video")) {
    		cond = isPathValid(arg, "disable") && isVideoFile(arg);
    	}
    	if (cond) {
    		boolean ov = true;
    		File fr = new File(arg);
    		if (fr.exists()) {
    		   switch (endArg) {
                case 0:
            	    System.out.print("(" + arg + ") " + OVERWRITE_WARNING + YN_PROMPT);
            	    ov = yNInput();
            	    break;
                case 1:
                	ov = true;
                	break;
                default:
                	ov = false;
               }
    	    }
    		if (!ov) {
        		int sm = 1;
        		String ogArg = arg;
        		arg = ogArg.substring(0, ogArg.lastIndexOf(".")) + "_" + sm + ogArg.substring(ogArg.lastIndexOf("."));
        		while (new File(arg).exists()) {
        			sm++;
        			arg = ogArg.substring(0, ogArg.lastIndexOf(".")) + "_" + sm + ogArg.substring(ogArg.lastIndexOf("."));
        		}
    		} else if (fr.exists() && !same) {
    			fr.delete();
    		}
    		return arg;
    	} else {
    		System.err.println("error: " + ext + " file path invalid - exiting");
    		System.exit(0);
    		return "";
    	}
    }
    
    // The Y/N boolean interface.
    private static boolean yNInput() {
    	String is = input.nextLine();
    	while (!is.equalsIgnoreCase("Y") && !is.equalsIgnoreCase("N")) {
    		System.out.print("\n(I said Y/N): ");
    		is = input.nextLine();
    	}
    	if (is.equalsIgnoreCase("Y")) {
    		return true;
    	} else {
    		return false;
    	}
    }
    
    // checks to see if file 1 exists.
    private static File assrrt(String arg) {
    	File f = new File(arg);
        if (!f.exists() || !arg.endsWith(".zip")) {
        	System.err.println("error: start file not found - exiting");
        	System.exit(0);
    		return null;
        } else {
        	return f;
        }
    }
    
    // checks path validity of other files (grabbed from https://www.javainuse.com/java/java-file-is-valid);
    public static boolean isPathValid(String path, String extension) {
        try {
            Paths.get(path);
        } catch (InvalidPathException ex) {
            return false;
        }
        if (!path.endsWith(extension) && !extension.equals("disable")) {
        	return false;
        }
        return true;
    }
    
    // checks to see if video path is valid
    public static boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
    }
    
    // checks to see if resolution arg is valid
    private static boolean isValidResArg(String endingArg) {
    	String[] sp = endingArg.split("x");
    	return (sp.length == 2 && endingArg.startsWith("-") && isNumeric(sp[0].substring(1)) && isNumeric(sp[1]));
    }
    
    // isNumeric method shamelessly grabbed from https://www.baeldung.com/java-check-string-number and modified.
    private static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            @SuppressWarnings("unused")
			int d = Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
