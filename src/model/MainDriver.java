package model;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.sikuli.script.*;

import gui.Overlay;
import sound.Sound;

/**
 * The main driver of the class that uses Sikuli to add data to various collections.
 * Version 1.0.1
 * @author May
 */
public class MainDriver {
	
	private static final int DEFAULT_WIDTH = 1920;
	private static final int DEFAULT_HEIGHT = 1080;
	
	public static boolean active = false;
	private static final long DEFAULT_TIME = -5000000;
	private static long pauseTime = DEFAULT_TIME;
	private static long startTime = DEFAULT_TIME;
	private static Dimension screenSize = new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);

	/**
	 * The TrackPoint enum, which describes several constants for image or text recognition.
	 * Constructors without images instead look for text within a certain region.
	 * All x and y coordinates are based on a full screen, 1920x1080 monitor.
	 * It is scaled down or up appropriately in the getRegion local method.
	 * The coordinates are the values of the upper left / bottom right corners around the region to be viewed for a certain TrackPoint.
	 */
	public enum TrackPoint {
		
		//debuffs
		SMITE("Smiting Aura", "Increases damage taken by target", "smitingaura.png", 640, 130, 1330, 240),
		SHIELDTOSS("Shield Toss", "Decreases defense of target", "shieldtoss.png", 640, 130, 1330, 240, 0.999),
		MOD("Mark of Death", "Increases damage taken by target", "markofdeath.png", 640, 130, 1330, 240, 0.9995),
		CELESTIAL_LIGHT("Celestial Light", "Inflicts damage over time and increases SP regen by 50%", "celestiallight.png", 640, 130, 1330, 240, 0.999),
		STATIC_FLASH("Static Flash", "Decreases defense of target", "staticflash.png", 640, 130, 1330, 240, 0.9995),
		RAGING_TEMPEST("Raging Tempest", "Decreases target's evasion and accuracy", "ragingtempest.png", 640, 130, 1330, 240, 0.999),
		SHADOW_CHASER("Shadow Chaser", "Recovers an additional 1 spirit every 0.1 seconds", "shadowchaser.png", 640, 130, 1330, 240, 0.999),
		POISON_EDGE("Poison Edge", "Inflicts damage over time, and increases damage dealt with Ruthless Guile", "poisonedge.png", 640, 130, 1330, 240, 0.999),
		POISON_VIAL("Poison Vial", "Inflicts damage over time, and increases damage dealt with Ruthless Guile", "poisonvial.png", 640, 130, 1330, 240, 0.999),

		//buffs
		IRON_DEFENSE("Iron Defense", "Increases SP regen, and decreases damage taken and dealt", "irondefense.png", 550, 740, 890, 890),
		GUARDIAN("Celestial Guardian", "Increases magic attack", "guardian.png", 550, 740, 890, 890, 0.995),
		BLESSINGS("Celestial Blessings", "Increases damage and resistance", "blessings.png", 550, 740, 890, 890, 0.9),
		SHARPEYES("Sharp Eyes", "Increases critical rate and accuracy", "sharpeyes.png", 550, 740, 890, 890, 0.999),
		FOCUSSEAL("Focus Seal", "Increases physical/magic attack", "focusseal.png", 550, 740, 890, 890, 0.999),
		WARHORN("Warhorn", "Increases physical/magic attack", "warhorn.png", 550, 740, 890, 890, 0.999),
		HONINGRUNES("Honing Runes", "Increases critical damage", "honingrunes.png", 550, 740, 890, 890, 0.999),
		SNIPE("Snipe", "Increases spirit regen when no enemies are nearby", "snipe.png", 550, 740, 890, 890, 0.99),
		DARKAURA("Dark Aura", "Increases spirit regen, stacking up to 10 times on hit", "darkaura.png", 550, 740, 890, 890, 0.99),
		HOLY_SYMBOL("Holy Symbol", "Increases attack speed, accuracy, and damage", "holysymbol.png", 550, 740, 890, 890, 0.999),
		VARR_WINGS("Varrekant's Wings", "Increases Piercing by 10%", "varrwings.png", "varrwings3.png", 550, 740, 890, 890, 0.99),
		WEAPON_PROC("Weapon Proc", "Weapon proc effect - varies with equipment", "weaponbuff.png", 550, 740, 890, 890, 0.9999),
		
		//misc
		SPIRIT("SP Efficiency: ", "% of the time SP was under 100%", "spirit.png", 0.94),
		HP("HP", "", 1099, 76, 1099 + 198, 76 + 29),
		BOSS("HP", "", "boss.png", 643, 77, 685, 102),
		RAID_DPS("Raid DPS", "Average raid damage per second"),
		TIME("Time", "Total ellasped time of the encounter", "clock.png"),
		HOLY_SYMBOL_DAMAGE("Holy Symbol Damage", "Estimated contribution of Holy Symbol", "holysymbol.png"),
		HOLY_SYMBOL_DAMAGE_RAW("Holy Symbol Damage (Raw)", "Total damage dealt during Holy Symbol", "holysymbolraw.png"),
		SMITE_AMP("Damage Amplified: ", "0"),
		BLESSINGS_AMP("Damage Amplified: ", "0"),
		SHIELDTOSS_AMP("Damage Amplified: ", "0"), //are these redundant or what
		STATIC_FLASH_AMP("Damage Amplified: ", "0"),
		MOD_AMP("Damage Amplified: ", "0"),
		INFERNOG_BOMB("Infernog Blue Bomb", "Drops a puddle when the timer ends", "infernogbomb.png", 1000, 750, 1300, 900, 0.99),
		BOSS_HEAL("Boss Healing", "Number of times the boss healed", "bossheal.png", 630, 0, 730, 55, 0.99),
		SHIELD("Shield Uptime", "Reduces damage taken by 50%", "shield.png", 630, 0, 730, 55, 0.99),
		SHIELD2("2x Shield Uptime", "Reduces damage taken by 80%", "doubleshield.png", 630, 0, 730, 55, 0.9),
		DMG_MITIGATED("Damage Mitigated: ", "0");
		private String name, intro, image, secondaryImage = null;
		private double threshold;
		private int[] region;
		private TrackPoint(String name, String intro, int x1, int y1, int x2, int y2) {
			this.name = name;
			this.intro = intro;
			this.image = null;
			this.region = new int[]{x1, y1, x2, y2};
			this.threshold = 0.7;
		}
		private TrackPoint(String name, String intro) {
			this.name = name;
			this.intro = intro;
			this.image = null;
			this.region = null;
			this.threshold = 0.7;
		}
		private TrackPoint(String name, String intro, String image) {
			this.name = name;
			this.intro = intro;
			this.image = image;
			this.region = null;
			this.threshold = 0.7;
		}
		private TrackPoint(String name, String intro, String image, double threshold) {
			this.name = name;
			this.intro = intro;
			this.image = image;
			this.region = null;
			this.threshold = threshold;
		}
		private TrackPoint(String name, String intro, String image, int x1, int y1, int x2, int y2) {
			this.name = name;
			this.intro = intro;
			this.image = image;
			this.region = new int[]{x1, y1, x2, y2};
			this.threshold = 0.7;
		}
		private TrackPoint(String name, String intro, String image, String secondaryImage, int x1, int y1, int x2, int y2, double threshold) {
			this.name = name;
			this.intro = intro;
			this.image = image;
			this.secondaryImage = image;
			this.region = new int[]{x1, y1, x2, y2};
			this.threshold = threshold;
		}
		private TrackPoint(String name, String intro, String image, int x1, int y1, int x2, int y2, double threshold) {
			this.name = name;
			this.intro = intro;
			this.image = image;
			this.region = new int[]{x1, y1, x2, y2};
			this.threshold = threshold;
		}
		private TrackPoint(String name, String image, int x1, int y1, int x2, int y2, double threshold) {
			this.name = name;
			this.image = image;
			this.region = new int[]{x1, y1, x2, y2};
			this.threshold = threshold;
		}
		public String getName() {
			return name;
		}
		public String getIntro() {
			return intro;
		}
		
		/**
		 * Returns the image for Sikuli recognition.
		 * @return An image - usually a very small sliver - for Sikuli's image recognition.
		 */
		public String getImage() {
			if (image == null)
				return null;
			StringBuilder sb = new StringBuilder();
			sb.append("images/sikuli/");
			sb.append(image);
			return sb.toString();
		}
		public String getSecondaryImage() {
			if (secondaryImage == null)
				return null;
			StringBuilder sb = new StringBuilder();
			sb.append("images/sikuli/");
			sb.append(secondaryImage);
			return sb.toString();
		}
		
		/**
		 * Returns the image for the table UI.
		 * @return An image for the table UI.
		 */
		public String getIcon() {
			StringBuilder sb = new StringBuilder();
			sb.append("images/tableicons/");
			sb.append(image);
			return sb.toString();
		}
		public boolean usesScreen() {
			return region == null;
		}
		public int[] getRegion() {
			int[] toReturn = new int[4];
			if (region == null)
				return null;
			for (int i = 0; i < region.length; i++) {
				toReturn[i] = (int)(((double)region[i] / (i % 2 == 0 ? DEFAULT_WIDTH : DEFAULT_HEIGHT)) 
					* (i % 2 == 0 ? screenSize.getWidth() : screenSize.getHeight()));
			}
			return toReturn;
		}
		public double getThreshold() {
			return threshold;
		}
		@Override
		public String toString() {
			return name;
		}
	}

	private static Overlay overlay = new Overlay();
    
    public static final Color MAIN_COLOR = new Color(255, 209, 220);
	
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
            	setLookAndFeel();
            	overlay.start();
            }
        });
    	run();
    }
    
    /**
     * Set the look and feel for the GUI program.
     */
    private static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (final UnsupportedLookAndFeelException e) {
            System.out.println("UnsupportedLookAndFeelException");
        } catch (final ClassNotFoundException e) {
            System.out.println("ClassNotFoundException");
        } catch (final InstantiationException e) {
            System.out.println("InstantiationException");
        } catch (final IllegalAccessException e) {
            System.out.println("IllegalAccessException");
        }
    }
    
    public static Map<TrackPoint, DataCollection> data;
    
    public static void run() {
        long lastLoopTime = System.nanoTime();
        final int TARGET_FPS = 2; //check twice per second
        final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;
        long lastFpsTime = 0;
        initializeData();
        while(true){
            long now = System.nanoTime();
            long updateLength = now - lastLoopTime;
            lastLoopTime = now;
            double delta = updateLength / ((double)OPTIMAL_TIME);

            lastFpsTime += updateLength;
            if(lastFpsTime >= 1000000000){
                lastFpsTime = 0;
            }
            tick();
            try {
            	Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
    }
    
    /**
     * Adds TrackPoints to the data and initializes screen size.
     * Certain data is excluded from the GUI in the Overlay class.
     */
    public static void initializeData() {
    	screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    	data = new TreeMap<TrackPoint, DataCollection>();
        data.put(TrackPoint.HP, new DataCollection());
        data.put(TrackPoint.RAID_DPS, new DeltaCollection(data.get(TrackPoint.HP), 1)); //this counts total damage; divided by seconds in chart
        data.put(TrackPoint.DMG_MITIGATED, new DeltaCollection(data.get(TrackPoint.HP), 1)); //mitigated is conditional - see tick method
        data.put(TrackPoint.SHIELD2, new HitMissCollection());
        data.put(TrackPoint.SHIELD, new HitMissCollection());
        data.put(TrackPoint.BOSS_HEAL, new CountCollection(2));
        data.put(TrackPoint.MOD_AMP, new DeltaCollection(data.get(TrackPoint.HP), 0.1)); //assumes max level
        data.put(TrackPoint.SHIELDTOSS_AMP, new DeltaCollection(data.get(TrackPoint.HP), 0.064)); //assumes max level
        data.put(TrackPoint.STATIC_FLASH_AMP, new DeltaCollection(data.get(TrackPoint.HP), 0.1)); //assumes max level
        data.put(TrackPoint.SMITE_AMP, new DeltaCollection(data.get(TrackPoint.HP), 0.064)); //assumes max level
        data.put(TrackPoint.BLESSINGS_AMP, new DeltaCollection(data.get(TrackPoint.HP), 0.064)); //assumes max level. may be inaccurate
        data.put(TrackPoint.POISON_EDGE, new HitMissCollection());
        data.put(TrackPoint.POISON_VIAL, new HitMissCollection());
        data.put(TrackPoint.RAGING_TEMPEST, new HitMissCollection());
        data.put(TrackPoint.STATIC_FLASH, new HitMissCollection());
        data.put(TrackPoint.MOD, new HitMissCollection());
        data.put(TrackPoint.SHIELDTOSS, new HitMissCollection());
        data.put(TrackPoint.IRON_DEFENSE, new HitMissCollection());
        data.put(TrackPoint.DARKAURA, new HitMissCollection());
        data.put(TrackPoint.SNIPE, new HitMissCollection());
        data.put(TrackPoint.SMITE, new HitMissCollection());
        data.put(TrackPoint.WARHORN, new HitMissCollection());
        data.put(TrackPoint.HONINGRUNES, new HitMissCollection());
        data.put(TrackPoint.SHARPEYES, new HitMissCollection());
        data.put(TrackPoint.FOCUSSEAL, new HitMissCollection());
        data.put(TrackPoint.BLESSINGS, new HitMissCollection());
        data.put(TrackPoint.GUARDIAN, new HitMissCollection());
        data.put(TrackPoint.SHADOW_CHASER, new HitMissCollection());
        data.put(TrackPoint.CELESTIAL_LIGHT, new HitMissCollection());
        data.put(TrackPoint.SPIRIT, new HitMissCollection());
        data.put(TrackPoint.HOLY_SYMBOL, new HitMissCollection());
        data.put(TrackPoint.WEAPON_PROC,  new HitMissCollection());
        data.put(TrackPoint.VARR_WINGS, new HitMissCollection(Sound.PROC, 10));
        data.put(TrackPoint.INFERNOG_BOMB, new HitMissCollection(Sound.STATUS_WARNING, 11));
        data.put(TrackPoint.HOLY_SYMBOL_DAMAGE, new HolySymbolCollection((HitMissCollection)data.get(TrackPoint.HOLY_SYMBOL), 
            	(DeltaCollection)data.get(TrackPoint.RAID_DPS)));
        data.put(TrackPoint.HOLY_SYMBOL_DAMAGE_RAW, new DeltaCollection(data.get(TrackPoint.HP), 1)); //assumes max level
    }
    
    /**
     * Resets the combat data.
     */
    public static void reset() {
    	for (TrackPoint tp : data.keySet()) {
    		DataCollection dc = data.get(tp);
    		dc.reset();
    	}
    	startTime = DEFAULT_TIME;
    	active = false;
    	Sound.RESET.play();
    }
    
    /**
     * Pauses the collection of combat data.
     */
    public static void pause() {
    	if (active) {
    		pauseTime = System.currentTimeMillis();
	    	active = false;
	    	Sound.PAUSE.play();
    	}
    }
    /**
     * Starts the collection of combat data.
     */
    public static void start() {
    	if (!active) {
        	active = true;
        	if (pauseTime != DEFAULT_TIME) {
        		long differential = pauseTime - startTime;
        		startTime = System.currentTimeMillis() - differential;
        		pauseTime = DEFAULT_TIME;
        	} else {
        		startTime = System.currentTimeMillis();
        	}
    	}
    }
    /**
     * Returns the ellasped time in seconds.
     * @return The ellasped time in seconds.
     */
    public static int getEllaspedTime() {
    	return (int)(Math.abs((active ? System.currentTimeMillis() : pauseTime) - startTime) / 1000);
    }
    
    /**
     * Returns a time as a formatted string.
     * @param time The time in seconds
     * @return A formatted time string, e.g. 0:46.
     */
    public static String timeToString(int time) {
    	int minutes = time / 60;
    	int seconds = time % 60;
    	StringBuilder sb = new StringBuilder();
    	sb.append(minutes);
    	sb.append(":");
    	sb.append(seconds >= 10 ? seconds : "0" + seconds);
    	return sb.toString();
    }
    
    private static int currentTick = 0;
    
    /**
     * Equalizes DataCollections by adding values equal to the first record to the start if they are shorter than others.
     */
    public static void equalize() {
    	int maxLength = -1;
    	for (TrackPoint tp : data.keySet()) {
    		DataCollection dc = data.get(tp);
    		if (dc.getData().size() > maxLength)
    			maxLength = dc.getData().size();
    	}
    	for (TrackPoint tp : data.keySet()) {
    		DataCollection dc = data.get(tp);
    		if (tp.getName().contains("Holy Symbol Damage"))
    			continue;
    		if (dc.getData().size() < maxLength) {
    			dc.equalize(maxLength);
    		}
    	}
    	
    }
    public static void tick() {
        Screen s = new Screen();
        int shields = 0;
        currentTick++;
        if (currentTick % 10 == 0 && active) {
        	//equalize();
        	((HolySymbolCollection)data.get(TrackPoint.HOLY_SYMBOL_DAMAGE)).calculate();
        }
    	for (TrackPoint tp : data.keySet()) {
    		DataCollection dc = data.get(tp);
    		String image = tp.getImage();
    		Match m;
    		if (image != null && (dc instanceof HitMissCollection || dc instanceof CountCollection)) {
	    		boolean hit;
	    		if (tp.usesScreen()) {
	    			m = s.exists(image, 0.01);
	    		} else {
	    			int[] region = tp.getRegion();
	    	        Region r = new Region(region[0], region[1], region[2] - region[0], region[3] - region[1]);
	    			m = r.exists(image, 0.01);
		    		if (m == null && tp.getSecondaryImage() != null) {
		    			m = r.exists(tp.getSecondaryImage(), 0.01);
		    		}
	    		}
	    	
				hit = m != null && m.getScore() >= tp.getThreshold();
				//if (m != null && tp.getName().contains("ings"))
				//	System.out.println(tp.getName()+": "+m.getScore());
				if (tp.getName().contains("Smiting Aura") && hit)
					((DeltaCollection)data.get(TrackPoint.SMITE_AMP)).handleHit(true);
				if (tp.getName().contains("Static Flash") && hit)
					((DeltaCollection)data.get(TrackPoint.STATIC_FLASH_AMP)).handleHit(true);
				if (tp.getName().contains("Shield Toss") && hit)
					((DeltaCollection)data.get(TrackPoint.SHIELDTOSS_AMP)).handleHit(true);
				if (tp.getName().contains("Mark of Death") && hit)
					((DeltaCollection)data.get(TrackPoint.MOD_AMP)).handleHit(true);
				if (tp.getName().contains("Blessings") && hit)
					((DeltaCollection)data.get(TrackPoint.BLESSINGS_AMP)).handleHit(true);
				//if (tp.getName().contains("Holy Symbol") && hit)
				//	((DeltaCollection)data.get(TrackPoint.HOLY_SYMBOL_DAMAGE)).handleHit(true);
				if (tp.getName().contains("Holy Symbol") && hit)
					((DeltaCollection)data.get(TrackPoint.HOLY_SYMBOL_DAMAGE_RAW)).handleHit(true);
				if (tp.getName().contains("Shield Uptime") && hit)
					shields = Math.max(shields,  1);
				if (tp.getName().contains("2x Shield Uptime") && hit)
					shields = Math.max(shields,  2);
				if (dc instanceof HitMissCollection) {
					if (tp.getName().contains("SP Efficiency"))
						hit = !hit;
					/*if (tp.getName().contains("Dark Aura") && hit) { //SORRY ZERKERS I TRIED
						Location l = m.getBottomLeft();
		    			int[] region = tp.getRegion();
		    	        Region r = new Region(l.getX() + 5, l.getY() - 15, 15, 15);
		    	        System.out.println("Reading between: "+r.toString());
		    	        String[] text = r.text().split("\n");
		    	        int value = -1;
		    	        for (String string : text) {
		    	        	try {
		    	        		System.out.println("Read value: "+string);
		    	        		value = Integer.parseInt(string);
		    	        		stacks = value;
		    	        		System.out.println("SRead value: "+value);
		    	        	} catch (Exception e) {
		    	        		
		    	        	}
		    	        }
						int stacks = 1;
						((HitMissCollection) dc).handleHit(stacks);
						
					} else*/
						((HitMissCollection) dc).handleHit(hit);
				}
				if (dc instanceof CountCollection) {
					((CountCollection) dc).handleHit(hit);
					
				}
    		} else if (tp.getRegion() != null) { //look for text
    			int[] region = tp.getRegion();
    	        Region r = new Region(region[0], region[1], region[2] - region[0], region[3] - region[1]);
    	        String[] text = r.text().split("\n");
    	        //String text = ocr.read(new Rectangle(region[0], region[1], region[2], region[3]));
    	        int value = -1;
    	        for (String string : text) {
    	        	try {
    	        		value = Integer.parseInt(string);
    	        	} catch (Exception e) {
    	        		
    	        	}
    	        }
    	        dc.addData(value);
    	        if (value != -1 && tp.getName().contains("HP") && !active) {
    	        	int[] regionBoss = TrackPoint.BOSS.getRegion();
        	        Region r2 = new Region(regionBoss[0], regionBoss[1], regionBoss[2] - regionBoss[0], regionBoss[3] - regionBoss[1]);
	    			m = r2.exists(TrackPoint.BOSS.getImage(), 0.01);
	    			if (m == null) {
	    				System.out.println("Found HP: "+value+", but unable to find [Boss] tag.");
	    			}
	    			if (m != null)
	    				start();
    	        }
    	        if (tp.getName().contains("HP") && value == 0) {
    	        	pause();
    	        }
    		}
    	}
    	((DeltaCollection)data.get(TrackPoint.RAID_DPS)).handleHit(true);
    	((DeltaCollection)data.get(TrackPoint.DMG_MITIGATED)).handleHit(shields > 0, shields == 1 ? 1 : 4);
    	/*for (TrackPoint tp : data.keySet()) {
    		DataCollection dc = data.get(tp);
    		if (dc instanceof DeltaCollection) {
				if (tp.getName().contains("Raid DPS")) {
					((DeltaCollection) dc).handleHit(true);
				}
				if (tp.getName().contains("Damage Amp")) {
					((DeltaCollection) dc).handleHit(smiteHit);
				}
				if (tp.getName().contains("Damage Mit")) {
					//one shield is 50% mitigation (e.g. 1:1 with current damage delta); two is 80% (so 4x the dmg dealt is mitigated)
					((DeltaCollection) dc).handleHit(shields > 0, shields == 1 ? 1 : 4);
				}
    		}
    	}*/
		overlay.refresh();
    }
}