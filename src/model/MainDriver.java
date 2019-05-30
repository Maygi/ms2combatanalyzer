package model;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.sikuli.script.*;

import gui.OverlayFrame;
import gui.GraphFrame;
import sound.Sound;
import util.VersionCheck;

/**
 * The main driver of the class that uses Sikuli to add data to various collections.
 * Version 1.31
 * @author May
 */
public class MainDriver {
	
	public static final String VERSION = "1.31";
	
	private static final int DEFAULT_WIDTH = 1920;
	private static final int DEFAULT_HEIGHT = 1080;
	
	/**
	 * The delay, in milliseconds, before the tracker will automatically restart (when entering combat)
	 * after a pause.
	 */
	private static final long RESET_THRESHOLD = 10000;
	
	public static boolean started = false;
	public static boolean active = false;
	public static boolean mute = false;
	private static final long DEFAULT_TIME = -5000000;
	private static long pauseTime = DEFAULT_TIME;
	private static long startTime = DEFAULT_TIME;
	private static Dimension screenSize = new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	private static Resolution resolution;

	/**
	 * The TrackPoint enum, which describes several constants for image or text recognition.
	 * Constructors without images instead look for text within a certain region.
	 * All x and y coordinates are based on a full screen, 1920x1080 monitor.
	 * It is scaled down or up appropriately in the getRegion local method.
	 * The coordinates are the values of the upper left / bottom right corners around the region to be viewed for a certain TrackPoint.
	 */
	public enum TrackPoint {
		
		//debuffs
		SMITE("Smiting Aura", "Increases damage taken by target", "smitingaura.png", Resolution.BOSS_DEBUFFS),
		SHIELDTOSS("Shield Toss", "Decreases defense of target", "shieldtoss.png", Resolution.BOSS_DEBUFFS, 0.999),
		MOD("Mark of Death", "Increases damage taken by target", "markofdeath.png", Resolution.BOSS_DEBUFFS, 0.9995),
		STATIC_FLASH("Static Flash", "Decreases defense of target", "staticflash.png", Resolution.BOSS_DEBUFFS, 0.9995),
		RAGING_TEMPEST("Raging Tempest", "Decreases target's evasion and accuracy", "ragingtempest.png", Resolution.BOSS_DEBUFFS, 0.999),

		//buffs
		BLESSINGS("Celestial Blessings", "Increases damage and resistance", "blessings.png", Resolution.BUFFS, 0.9),
		FOCUSSEAL("Focus Seal", "Increases physical/magic attack", "focusseal.png", Resolution.BUFFS, 0.9995),
		WARHORN("Warhorn", "Increases physical/magic attack", "warhorn.png", Resolution.BUFFS, 0.999),
		HONINGRUNES("Honing Runes", "Increases critical damage", "honingrunes.png", Resolution.BUFFS, 0.999),
		SHARPEYES("Sharp Eyes", "Increases critical rate and accuracy", "sharpeyes.png", Resolution.BUFFS, 0.999),
		HOLY_SYMBOL("Holy Symbol", "Increases attack speed, accuracy, and damage", "holysymbol.png", Resolution.BUFFS, 0.999),
		VARR_WINGS("Varrekant's Wings", "Increases Piercing by 10%", "varrwings.png", "varrwings3.png", Resolution.BUFFS, 0.99),
		WEAPON_PROC("Weapon Proc", "Weapon proc effect - varies with equipment", "weaponbuff.png", Resolution.BUFFS, 0.9999),
		
		//personal debuffs
		FLAME_WAVE("Flame Wave", "Inflicts damage over time", "flamewave.png", Resolution.BOSS_DEBUFFS, 0.999, ClassConstants.WIZARD),
		CELESTIAL_LIGHT("Celestial Light", "Inflicts damage over time and increases SP regen by 50%", "celestiallight.png", Resolution.BOSS_DEBUFFS, 0.999, ClassConstants.PRIEST),
		SHADOW_CHASER("Shadow Chaser", "Recovers an additional 1 spirit every 0.1 seconds", "shadowchaser.png", Resolution.BOSS_DEBUFFS, 0.999, ClassConstants.ASSASSIN),
		POISON_EDGE("Poison Edge", "Inflicts damage over time, and increases damage dealt with Ruthless Guile", "poisonedge.png", Resolution.BOSS_DEBUFFS, 0.999, ClassConstants.THIEF),
		POISON_VIAL("Poison Vial", "Inflicts damage over time, and increases damage dealt with Ruthless Guile", "poisonvial.png", Resolution.BOSS_DEBUFFS, 0.999, ClassConstants.THIEF),
		
		//personal buffs
		IRON_DEFENSE("Iron Defense", "Increases SP regen, and decreases damage taken and dealt", "irondefense.png", Resolution.BUFFS, ClassConstants.KNIGHT),
		SHIELD_MASTERY("Shield Mastery", "Increases damage after a successful block", "shieldmastery.png", Resolution.BUFFS, 0.999, ClassConstants.KNIGHT),
		GUARDIAN("Celestial Guardian", "Increases magic attack", "guardian.png", Resolution.BUFFS, 0.995, ClassConstants.PRIEST),
		SNIPE("Snipe", "Increases spirit regen when no enemies are nearby", "snipe.png", Resolution.BUFFS, 0.99, ClassConstants.ARCHER),
		BRONZE_EAGLE("Bronze Eagle", "Increases Dexterity", "bronzeeagle.png", Resolution.BUFFS, 0.9995, ClassConstants.ARCHER),
		EAGLES_MAJESTY("Eagle's Majesty", "Restores SP per second, and Bronze Eagle deals additional damage on hit", "eaglesmajesty.png", Resolution.BUFFS, 0.99, ClassConstants.ARCHER),
		FATAL_STRIKES("Fatal Strikes", "All attacks deal critical damage", "fatalstrikes.png", Resolution.BUFFS, 0.999, ClassConstants.ASSASSIN),
		DARKAURA("Dark Aura", "Increases spirit regen, stacking up to 10 times on hit", "darkaura.png", Resolution.BUFFS, 0.99, ClassConstants.BERSERKER),
		RETALIATION("Retaliation", "Increases physical and magic attack as well as evasion", "retaliation.png", Resolution.BUFFS, 0.999, ClassConstants.THIEF),
		MESOGUARD("Mesoguard Plus", "Increases physical and magic attack ", "mesoguard.png", Resolution.BUFFS, 0.999, ClassConstants.THIEF),
		HASTE("Haste", "Increases attack speed, movement speed, physical attack, and magic attack", "haste.png", Resolution.BUFFS, 0.999, ClassConstants.THIEF),
		
		// lapentiers
		ROOTED_STRENGTH("Rooted Strength", "Increases damage by 30% but reduces movement speed by 80%", "rootedstrength.png", Resolution.BUFFS, 0.999),
		
		// lapenshards
		PINK_BEANS_PRANK("Pink Bean's Prank", "Increases physical and magic attack", "pinkbeansprank.png", Resolution.BUFFS, 0.999),
		
		//wizard awakening
		FLAME_IMP("Flame Imp", "Embers will always crit. Chance to reset BBQ party on crit.", "playingwithfire.png", Resolution.BUFFS, 0.999, ClassConstants.WIZARD),
		MANA_CONTROL("Mana Control", "Increases movement speed by 35% and magic attack by 20%", "manacontrol.png", Resolution.BUFFS, 0.999, ClassConstants.WIZARD),
		MANA_CONTROL2("Mana Control - SP", "Increases movement speed by 35% and decreases SP costs by half", "manacontrol2.png", Resolution.BUFFS, 0.999, ClassConstants.WIZARD),
		PARTY_TIME("Party Time", "BBQ party can be cast instantly", "partytime.png", Resolution.BUFFS, 0.999, ClassConstants.WIZARD),
		PERFECT_STORM("Perfect Storm", "Increases electric and ice damage", "perfectstorm.png", Resolution.BUFFS, 0.999, ClassConstants.WIZARD),
		FROST("Frost", "Electric part of dualcast deals additional damage", "frost.png", Resolution.BOSS_DEBUFFS, 0.9995, ClassConstants.WIZARD),
		CHILL("Chill", "Target takes bonus damage from thunderbolt", "chill.png", Resolution.BOSS_DEBUFFS, 0.9995, ClassConstants.WIZARD),
		
		//sb awakening
		SOUL_DISSONANCE("Soul Dissonance", "Decreases evasion and critical evasion of target", "souldissonance.png", Resolution.BOSS_DEBUFFS, 0.999),
		SOUL_FLOCK("Soul Flock", "Decreases defense of target", "soulflock.png", Resolution.BOSS_DEBUFFS, 0.999),
		VISION_TORRENT("Vision Torrent", "Increases magic attack and enhances skills", "visiontorrent.png", Resolution.BUFFS, 0.999, ClassConstants.SOUL_BINDER),
		
		//priest awakening
		ARIELS_WINGS("Ariel's Wings", "Decreases defense of target", "arielswings.png", Resolution.BOSS_DEBUFFS, 0.999),
		PURIFYING_LIGHT("Purifying Light", "Decreases defense of target", "purifyinglight.png", Resolution.BOSS_DEBUFFS, 0.999),
		VITALITY("Vitality", "Increased physical and magic attack", "vitality.png", Resolution.BUFFS, 0.999),
		GREATER_HEAL("Greater Heal", "Restoring health over time. 50% of this uptime is a damage buff", "greaterheal.png", Resolution.BUFFS, 0.999, ClassConstants.PRIEST),
		HEAVENS_WRATH("Heaven's Wrath", "Increased stamina recovery, max health, movespeed, and access to Light Sword", "lightsword.png", Resolution.BUFFS, 0.999, ClassConstants.PRIEST),
		
		//archer awakening
		FLAME_ARROW_1("Flame Arrow I", "Multi-drive shot I is ready", "flamearrow1.png", Resolution.BUFFS, 0.8, ClassConstants.ARCHER),
		FLAME_ARROW_2("Flame Arrow II", "Multi-drive shot II is ready", "flamearrow2.png", Resolution.BUFFS, 0.8, ClassConstants.ARCHER),
		WIND_DRAW("Full Wind Draw", "Archer's secrets is ready", "rangersfocus.png", Resolution.BUFFS, 0.999, ClassConstants.ARCHER),
		RANGERS_FOCUS("Ranger's Focus", "Increases physical attack and enables Flame Arrow IV", "rangersfocus.png", Resolution.BUFFS, 0.999, ClassConstants.ARCHER),
		ARCHERS_SECRETS("Archer's Secrets", "Increases piercing and accuracy, and enables enhanced skills", "rangersfocus.png", Resolution.BUFFS, 0.999, ClassConstants.ARCHER),
		GREATER_SHARP_EYES("Greater Sharp Eyes", "Increases physical attack of caster", "greatersharpeyes.png", Resolution.BUFFS, 0.999),
		
		//knight awakening
		CYCLONE_SHIELD("Cyclone Shield", "Decreases defense of target", "cycloneshield.png", Resolution.BOSS_DEBUFFS, 0.9995),
		DIVINE_RETRIBUTION("Divine Retribution", "Increases physical/magical attack, but decreases defense and disables shield skills", "divineretribution.png", Resolution.BUFFS, 0.9995, ClassConstants.KNIGHT),
		
		//assassin awakening
		SHADOW_STANCE("Shadow Stance", "Increases dark damage while the shield holds", "shadowstance.png", Resolution.BUFFS, 0.999, ClassConstants.ASSASSIN),
		
		//berserker awakening
		BLOOD_FURY("Blood Fury", "Increases physical attack and Bloodlust damage", "bloodfury.png", Resolution.BUFFS, 0.999, ClassConstants.BERSERKER),
		RUTHLESS("Ruthless", "Skull Splitter's third attack can be used instantly", "ruthless.png", Resolution.BUFFS, 0.999, ClassConstants.BERSERKER),
		RAGING_SOUL("Raging Soul", "Increased attack speed, physical attack, and dark damage", "ragingsoul.png", Resolution.BUFFS, 0.999, ClassConstants.BERSERKER),
		
		//striker
		OVERCOME("Overcome", "Increases attack speed, movement speed, physical attack, and magic attack", "bloodfury.png", Resolution.BUFFS, 0.999, ClassConstants.STRIKER),
		FIGHTING_SPIRIT("Fighting Spirit - Vengeance", "Increased damage", "vengeance.png", Resolution.BUFFS, 0.999, ClassConstants.STRIKER),
		PATTERN_BREAK("Pattern Break", "Increases accuracy", "patternbreak.png", Resolution.BUFFS, 0.999, ClassConstants.STRIKER),
		MERIDIAN_FLOW("Meridian Flow I", "Increases physical attack, attack speed, and restores SP over time", "meridianflow1.png", Resolution.BUFFS, 0.999, ClassConstants.STRIKER),
		MERIDIAN_FLOW2("Meridian Flow II", "Increases physical attack, attack speed, and restores SP over time", "meridianflow2.png", Resolution.BUFFS, 0.999, ClassConstants.STRIKER),
		MERIDIAN_FLOW3("Meridian Flow III", "Increases physical attack, attack speed, and restores SP over time", "meridianflow3.png", Resolution.BUFFS, 0.999, ClassConstants.STRIKER),

		//thief awakening
		RUSH("Battle Step - Rush", "Increases physical attack and movement speed", "rush.png", Resolution.BUFFS, 0.999, ClassConstants.THIEF),		
		
		//misc
		SPIRIT("SP Efficiency: ", "% of the time SP was under 100%", "spirit.png", Resolution.SPIRIT, 0.94),
		HP("HP", "", Resolution.BOSS_HEALTH),
		BOSS("HP", "", "boss.png", Resolution.BOSS_TAG),
		RAID_DPS("Raid DPS", "Average raid damage per second"),
		TIME("Time", "Total ellasped time of the encounter and estimated clear time", "clock.png"),
		HOLY_SYMBOL_DAMAGE("Holy Symbol Damage", "Estimated contribution of Holy Symbol", "holysymbol.png"),
		HOLY_SYMBOL_DAMAGE_RAW("Holy Symbol Damage (Raw)", "Total damage dealt during Holy Symbol", "holysymbolraw.png"),
		SMITE_AMP("Damage Amplified: ", "0"),
		BLESSINGS_AMP("Damage Amplified: ", "0"),
		SHIELDTOSS_AMP("Damage Amplified: ", "0"), //are these redundant or what
		STATIC_FLASH_AMP("Damage Amplified: ", "0"),
		CYCLONE_SHIELD_AMP("Damage Amplified: ", "0"),
		PURIFYING_LIGHT_AMP("Damage Amplified: ", "0"),
		SOUL_FLOCK_AMP("Damage Amplified: ", "0"),
		ARIELS_WINGS_AMP("Damage Amplified: ", "0"),
		MOD_AMP("Damage Amplified: ", "0"),
		DUNGEON_COMPLETE("Dungeon Complete", "Flag for dungeon completion", "complete.png", Resolution.DUNGEON_CLEAR),
		TOMBSTONE("Tombstoned", "You are stuck under a tombstone - all buffs are wiped", "dead.png", Resolution.TOMBSTONE, 0.99),
		//TOMBSTONE("Tombstoned", "You are stuck under a tombstone - all buffs are wiped", "tombstone.png", "tombstone2.png", Resolution.TOMBSTONE),
		INFERNOG_BOMB("Infernog Blue Bomb", "Drops a puddle when the timer ends", "infernogbomb.png", Resolution.DEBUFFS, 0.99),
		BOSS_HEAL("Boss Healing", "Number of times the boss healed", "bossheal.png", Resolution.BOSS_BUFFS, 0.95),
		SHIELD("Shield Uptime", "Reduces damage taken by 50%", "shield.png", Resolution.BOSS_BUFFS),
		SHIELD2("2x Shield Uptime", "Reduces damage taken by 80%", "doubleshield.png", Resolution.BOSS_BUFFS),
		DMG_MITIGATED("Damage Mitigated: ", "0");
		
		private String name, intro, image, secondaryImage = null;
		private double threshold;
		private int regionIndex;
		private int classIndex = -1;
		
		private TrackPoint(String name, String intro, int regionIndex) {
			this.regionIndex = regionIndex;
			this.name = name;
			this.intro = intro;
			this.image = null;
			this.threshold = 0.7;
		}
		private TrackPoint(String name, String intro) {
			this.regionIndex = -1;
			this.name = name;
			this.intro = intro;
			this.image = null;
			this.threshold = 0.7;
		}
		private TrackPoint(String name, String intro, String image) {
			this.regionIndex = -1;
			this.name = name;
			this.intro = intro;
			this.image = image;
			this.threshold = 0.7;
		}
		private TrackPoint(String name, String intro, String image, String secondaryImage) {
			this.regionIndex = -1;
			this.name = name;
			this.intro = intro;
			this.image = image;
			this.secondaryImage = secondaryImage;
			this.threshold = 0.7;
		}
		private TrackPoint(String name, String intro, String image, String secondaryImage, int regionIndex) {
			this.regionIndex = -1;
			this.name = name;
			this.intro = intro;
			this.image = image;
			this.secondaryImage = secondaryImage;
			this.threshold = 0.7;
			this.regionIndex = regionIndex;
		}
		private TrackPoint(String name, String intro, String image, double threshold) {
			this.regionIndex = -1;
			this.name = name;
			this.intro = intro;
			this.image = image;
			this.threshold = threshold;
		}
		private TrackPoint(String name, String intro, String image, int regionIndex) {
			this.regionIndex = regionIndex;
			this.name = name;
			this.intro = intro;
			this.image = image;
			this.threshold = 0.7;
		}
		private TrackPoint(String name, String intro, String image, String secondaryImage, int regionIndex, double threshold) {
			this.regionIndex = regionIndex;
			this.name = name;
			this.intro = intro;
			this.image = image;
			this.secondaryImage = image;
			this.threshold = threshold;
		}
		private TrackPoint(String name, String intro, String image, int resolutionIndex, double threshold) {
			this.regionIndex = resolutionIndex;
			this.name = name;
			this.intro = intro;
			this.image = image;
			this.threshold = threshold;
		}
		private TrackPoint(String name, String intro, String image, int resolutionIndex, double threshold, int classIndex) {
			this.regionIndex = resolutionIndex;
			this.name = name;
			this.intro = intro;
			this.image = image;
			this.threshold = threshold;
			this.classIndex = classIndex;
		}
		private TrackPoint(String name, String image, int resolutionIndex, double threshold) {
			this.regionIndex = resolutionIndex;
			this.name = name;
			this.image = image;
			this.threshold = threshold;
		}
		public String getName() {
			return name;
		}
		public String getIntro() {
			return intro;
		}
		public int getClassIndex() {
			return classIndex;
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
			return regionIndex == -1;
		}
		public int getRegionIndex() {
			return regionIndex;
		}
		public int[] getRegion() {
			if (regionIndex == -1) {
				return null;
			}
			Integer[] region = resolution.getRegion(regionIndex);
			int[] toReturn = new int[4];
			for (int i = 0; i < region.length; i++) {
				if (resolution.hasMatch())
					toReturn[i] = region[i];
				else
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

	private static OverlayFrame overlay = new OverlayFrame();
	private static GraphFrame report = new GraphFrame();
    
    public static final Color MAIN_COLOR = new Color(255, 209, 220);
	public static Properties props;
    
    /**
     * Loads various properties from the property file.
     */
    private static void loadProps() {
        try {
        	boolean isMute = Boolean.parseBoolean(MainDriver.props.getProperty("mute"));
        	mute = isMute;
        } catch (Exception e) {
        	System.err.println("Unable to load properties on MainDriver.");
        }
    }
	
    public static void main(String[] args) throws FileNotFoundException {
    	props = new Properties();
    	try {
    		props.load(new FileReader("config.properties"));
    		loadProps();
		} catch (FileNotFoundException e) {
			System.out.println("Preferences not found - creating.");
			FileOutputStream file;
			try {
				file = new FileOutputStream("config.properties");
				props.store(file, null);
			} catch (FileNotFoundException e1) {
				System.err.println("Error in loading properties.");
				e1.printStackTrace();
			} catch (IOException e1) {
				System.err.println("Error in loading properties.");
				e1.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
            	setLookAndFeel();
            	overlay.start();
            }
        });
    	run();
    }
    
    public static void saveProps() {
		FileOutputStream file;
		try {
			file = new FileOutputStream("config.properties");
			props.store(file, null);
		} catch (FileNotFoundException e) {
			System.err.println("Error in saving properties.");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Error in saving properties.");
			e.printStackTrace();
		}
    	
    }
    
    public static void saveWindowPosition(int x, int y) {
    	props.setProperty("x", Integer.toString(x));
    	props.setProperty("y", Integer.toString(y));
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
    public static Map<Integer, List<TrackPoint>> trackPointByRegion;
    public static PrintStream logOutput;
    public static String liveVersion = VERSION;
    
    public static void run() throws FileNotFoundException {
		logOutput = new PrintStream(new File("log.txt"));
        long lastLoopTime = System.nanoTime();
        final int TARGET_FPS = 2; //check twice per second
        final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;
        long lastFpsTime = 0;
        initializeData();
    	liveVersion = VersionCheck.getVersion();
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
    	resolution = new Resolution(screenSize);
    	data = new TreeMap<TrackPoint, DataCollection>();
        data.put(TrackPoint.TIME, new TimeCollection());
        data.put(TrackPoint.HP, new DataCollection());
        data.put(TrackPoint.RAID_DPS, new DeltaCollection(data.get(TrackPoint.HP), 1, DeltaCollection.OTHER)); //this counts total damage; divided by seconds in chart
        data.put(TrackPoint.DMG_MITIGATED, new DeltaCollection(data.get(TrackPoint.HP), 1, DeltaCollection.OTHER)); //mitigated is conditional - see tick method
        data.put(TrackPoint.SHIELD2, new HitMissCollection());
        data.put(TrackPoint.SHIELD, new HitMissCollection());
        data.put(TrackPoint.BOSS_HEAL, new CountCollection(2));
        data.put(TrackPoint.MOD_AMP, new DeltaCollection(data.get(TrackPoint.HP), 0.1, DeltaCollection.DAMAGE_AMP)); //assumes max level
        data.put(TrackPoint.SHIELDTOSS_AMP, new DeltaCollection(data.get(TrackPoint.HP), 0.064, DeltaCollection.DEFENSE_DEBUFF)); //assumes max level
        data.put(TrackPoint.STATIC_FLASH_AMP, new DeltaCollection(data.get(TrackPoint.HP), 0.1, DeltaCollection.DEFENSE_DEBUFF)); //assumes max level
        data.put(TrackPoint.SMITE_AMP, new DeltaCollection(data.get(TrackPoint.HP), 0.064, DeltaCollection.DAMAGE_AMP)); //assumes max level
        data.put(TrackPoint.BLESSINGS_AMP, new DeltaCollection(data.get(TrackPoint.HP), 0.064, DeltaCollection.DAMAGE_AMP)); //assumes max level. may be inaccurate
        data.put(TrackPoint.POISON_EDGE, new HitMissCollection());
        data.put(TrackPoint.POISON_VIAL, new HitMissCollection());
        data.put(TrackPoint.RAGING_TEMPEST, new HitMissCollection());
        data.put(TrackPoint.STATIC_FLASH, new HitMissCollection(TrackPoint.STATIC_FLASH_AMP));
        data.put(TrackPoint.MOD, new HitMissCollection(TrackPoint.MOD_AMP));
        data.put(TrackPoint.SHIELDTOSS, new HitMissCollection(TrackPoint.SHIELDTOSS_AMP));
        data.put(TrackPoint.FLAME_WAVE, new HitMissCollection());
        data.put(TrackPoint.IRON_DEFENSE, new HitMissCollection());
        data.put(TrackPoint.DARKAURA, new HitMissCollection());
        data.put(TrackPoint.SNIPE, new HitMissCollection());
        data.put(TrackPoint.EAGLES_MAJESTY, new HitMissCollection());
        data.put(TrackPoint.BRONZE_EAGLE, new HitMissCollection());
        data.put(TrackPoint.SMITE, new HitMissCollection());
        data.put(TrackPoint.WARHORN, new HitMissCollection());
        data.put(TrackPoint.HONINGRUNES, new HitMissCollection());
        data.put(TrackPoint.SHARPEYES, new HitMissCollection());
        data.put(TrackPoint.FATAL_STRIKES, new HitMissCollection());
        data.put(TrackPoint.FOCUSSEAL, new HitMissCollection());
        data.put(TrackPoint.BLESSINGS, new HitMissCollection());
        data.put(TrackPoint.GUARDIAN, new HitMissCollection());
        data.put(TrackPoint.SHADOW_CHASER, new HitMissCollection());
        data.put(TrackPoint.CELESTIAL_LIGHT, new HitMissCollection());
        data.put(TrackPoint.SHIELD_MASTERY, new HitMissCollection());
        data.put(TrackPoint.SPIRIT, new HitMissCollection());
        data.put(TrackPoint.HOLY_SYMBOL, new HitMissCollection());
        data.put(TrackPoint.WEAPON_PROC,  new HitMissCollection(Sound.PROC, 21));
        data.put(TrackPoint.DUNGEON_COMPLETE, new HitMissCollection());
        data.put(TrackPoint.TOMBSTONE, new HitMissCollection());
        data.put(TrackPoint.VARR_WINGS, new HitMissCollection(Sound.PROC, 10));
        data.put(TrackPoint.INFERNOG_BOMB, new HitMissCollection(Sound.STATUS_WARNING, 11));
        data.put(TrackPoint.HOLY_SYMBOL_DAMAGE, new DPSCollection((HitMissCollection)data.get(TrackPoint.HOLY_SYMBOL), 
            	(DeltaCollection)data.get(TrackPoint.RAID_DPS)));
        data.put(TrackPoint.HOLY_SYMBOL_DAMAGE_RAW, new DeltaCollection(data.get(TrackPoint.HP), 1, DeltaCollection.DAMAGE_AMP)); //assumes max level

        //archer awakening
        data.put(TrackPoint.FLAME_ARROW_1, new HitMissCollection(Sound.PROC2, 10));
        data.put(TrackPoint.FLAME_ARROW_2, new HitMissCollection(Sound.PROC2, 10));
        data.put(TrackPoint.RANGERS_FOCUS, new HitMissCollection());
        data.put(TrackPoint.WIND_DRAW, new HitMissCollection(Sound.PROC2, 10));
        data.put(TrackPoint.ARCHERS_SECRETS, new HitMissCollection());
        data.put(TrackPoint.GREATER_SHARP_EYES, new HitMissCollection());
        
        //assassin awakening
        /*data.put(TrackPoint.SHADOW_STANCE, new HitMissCollection());
        
        //berserker awakening
        data.put(TrackPoint.BLOOD_FURY, new HitMissCollection());
        data.put(TrackPoint.RUTHLESS, new HitMissCollection(Sound.PROC2, 2));
        data.put(TrackPoint.RAGING_SOUL, new HitMissCollection());*/
        
        //knight awakening
        data.put(TrackPoint.CYCLONE_SHIELD, new HitMissCollection(TrackPoint.CYCLONE_SHIELD_AMP));
        data.put(TrackPoint.CYCLONE_SHIELD_AMP, new DeltaCollection(data.get(TrackPoint.HP), 0.08, DeltaCollection.DEFENSE_DEBUFF)); //assumes max level
        data.put(TrackPoint.DIVINE_RETRIBUTION, new HitMissCollection());
        
        //priest awakening
        data.put(TrackPoint.ARIELS_WINGS, new HitMissCollection(TrackPoint.ARIELS_WINGS_AMP));
        data.put(TrackPoint.ARIELS_WINGS_AMP, new DeltaCollection(data.get(TrackPoint.HP), 0.05, DeltaCollection.DEFENSE_DEBUFF));
        data.put(TrackPoint.PURIFYING_LIGHT, new HitMissCollection(TrackPoint.PURIFYING_LIGHT_AMP));
        data.put(TrackPoint.PURIFYING_LIGHT_AMP, new DeltaCollection(data.get(TrackPoint.HP), 0.06, DeltaCollection.DEFENSE_DEBUFF)); //assumes max level
        data.put(TrackPoint.HEAVENS_WRATH, new HitMissCollection());
        data.put(TrackPoint.GREATER_HEAL, new HitMissCollection());
        data.put(TrackPoint.VITALITY, new HitMissCollection());
        
        //soul binder awakening
        data.put(TrackPoint.SOUL_DISSONANCE, new HitMissCollection());
        data.put(TrackPoint.SOUL_FLOCK, new HitMissCollection(TrackPoint.SOUL_FLOCK_AMP));
        data.put(TrackPoint.SOUL_FLOCK_AMP, new DeltaCollection(data.get(TrackPoint.HP), 0.15, DeltaCollection.DEFENSE_DEBUFF)); //assumes max level
        data.put(TrackPoint.VISION_TORRENT, new HitMissCollection());
        
        //striker
       /* data.put(TrackPoint.OVERCOME, new HitMissCollection());
        data.put(TrackPoint.FIGHTING_SPIRIT, new HitMissCollection());
        data.put(TrackPoint.PATTERN_BREAK, new HitMissCollection());
        data.put(TrackPoint.MERIDIAN_FLOW, new HitMissCollection());
        data.put(TrackPoint.MERIDIAN_FLOW2, new HitMissCollection());
        data.put(TrackPoint.MERIDIAN_FLOW3, new HitMissCollection());*/
        
        //thief
        data.put(TrackPoint.RETALIATION, new HitMissCollection());
        data.put(TrackPoint.MESOGUARD, new HitMissCollection());
        data.put(TrackPoint.HASTE, new HitMissCollection());
        
        //wizard awakening
        data.put(TrackPoint.FLAME_IMP, new HitMissCollection());
        data.put(TrackPoint.PARTY_TIME, new HitMissCollection(Sound.PROC2, 1));
        data.put(TrackPoint.MANA_CONTROL, new HitMissCollection());
        data.put(TrackPoint.MANA_CONTROL2, new HitMissCollection());
        data.put(TrackPoint.PERFECT_STORM, new HitMissCollection());
        data.put(TrackPoint.FROST, new HitMissCollection());
        data.put(TrackPoint.CHILL, new HitMissCollection());
        
        data.put(TrackPoint.ROOTED_STRENGTH, new HitMissCollection());
        data.put(TrackPoint.PINK_BEANS_PRANK, new HitMissCollection());
    	trackPointByRegion = new TreeMap<Integer, List<TrackPoint>>();
    	for (TrackPoint tp : data.keySet()) {
    		int region = tp.getRegionIndex();
    		if (trackPointByRegion.get(region) == null) {
    			List<TrackPoint> list = new ArrayList<TrackPoint>();
    			list.add(tp);
    			trackPointByRegion.put(region, list);
    		} else {
    			List<TrackPoint> list = trackPointByRegion.get(region);
    			list.add(tp);
    			trackPointByRegion.put(region, list);
    		}
    	}
    }
    
    /**
     * Toggles the mute state.
     */
    public static void toggleMute() {
    	mute = !mute;
    	Sound.SELECT.play();
    	props.setProperty("mute", Boolean.toString(mute));
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
    	pauseTime = DEFAULT_TIME;
    	active = false;
    	started = false;
    	Sound.RESET.play();
        overlay.resetTooltips();
		firstHPReading = null;
        lastHPReading = null;
        secondReading = null;
        activeClass = -1;
    }
    
    /**
     * Pauses the collection of combat data.
     */
    public static void pause() {
    	if (active) {
    		pauseTime = System.currentTimeMillis();
	    	active = false;
	    	Sound.PAUSE.play();
			firstHPReading = null;
	        lastHPReading = null;
	        secondReading = null;
    	}
    }
    
    /**
     * Pauses the collection of combat data fully.
     */
    public static void end() {
		pauseTime = DEFAULT_TIME;
    	active = false;
    	Sound.PAUSE.play();
    }
    /**
     * Starts the collection of combat data.
     */
    public static void start() {
    	if (!active) {
        	if (pauseTime != DEFAULT_TIME) {
        		long differential = pauseTime - startTime;
        		System.out.println("Differential: "+differential);
        		if (differential > RESET_THRESHOLD) {
        			differential = 0;
        			reset();
        		}
        		startTime = System.currentTimeMillis() - differential;
        		pauseTime = DEFAULT_TIME;
        	} else {
        		startTime = System.currentTimeMillis();
        	}
        	active = true;
        	started = true;
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
     * Returns the ellasped time in seconds after the given argument.
     * @param time The time to compare to.
     * @return An int representing the ellasped time in seconds after the argument.
     */
    public static int getEllaspedTime(long time) {
    	return (int)(Math.abs(time - startTime) / 1000);    	
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
    
    private static BigInteger firstHPReading = null;
    private static BigInteger lastHPReading = null;
    private static BigInteger secondReading = null;
    private static int activeClass = -1;
    
    /**
     * Whether or not the program should get ready to check for the clear screen.
     * @return
     */
    private static boolean checkForClear() {
    	if (firstHPReading != null && lastHPReading != null) {
    		return (lastHPReading.compareTo(firstHPReading.divide(new BigInteger("10"))) < 0);
    	}
    	return false;
    }
    
    public static boolean isClose(BigInteger one, BigInteger two) {
    	if (one.subtract(two).abs().compareTo(new BigInteger("5000000")) <= 0)
    		return true;
    	BigInteger bigger, smaller;
    	if (one.compareTo(two) > 0) {
    		bigger = one;
    		smaller = two;
    	} else {
    		bigger = two;
    		smaller = one;
    	}
    	if (smaller.multiply(new BigInteger("2")).compareTo(bigger) <= 0) //if it's over 2x difference, it's bad
    		return false;
    	return true;
    }
    
    public static void tick() {
        //double timeMultiplier = ((TimeCollection)MainDriver.data.get(TrackPoint.TIME)).getTimeMultiplier();
        //System.out.println("Multiplier: "+timeMultiplier);
        Screen s = new Screen();
        int shields = 0;
        currentTick++;
        if (currentTick % 10 == 0 && active) {
        	//equalize();
        	((DPSCollection)data.get(TrackPoint.HOLY_SYMBOL_DAMAGE)).calculateHSDamage();
        	((DPSCollection)data.get(TrackPoint.HOLY_SYMBOL_DAMAGE)).calculateAverageDamage();
        }
        Map<Integer, Region> regionMap = new TreeMap<Integer, Region>();
        for (Integer regionId : trackPointByRegion.keySet()) {
        	List<TrackPoint> list = trackPointByRegion.get(regionId);
			int[] region = list.get(0).getRegion();
			if (region != null) {
		        Region r = new Region(region[0], region[1], region[2] - region[0], region[3] - region[1]);
		        regionMap.put(regionId, r);
			}
        }
    	for (TrackPoint tp : data.keySet()) {
    		if (activeClass >= 0 && tp.getClassIndex() >= 0 && tp.getClassIndex() != activeClass) {
    			continue;
    		}
    		DataCollection dc = data.get(tp);
    		String image = tp.getImage();
    		Match m;

    		/*if (tp.getName().equalsIgnoreCase("Greater Sharp Eyes")) { //special case
    			if (activeClass != ClassConstants.ARCHER) {
    				dc = data.get(TrackPoint.SHARPEYES);
    			}
    		}*/
    		if (dc instanceof TimeCollection && active) {
    			((TimeCollection) dc).addData(System.currentTimeMillis());
    			continue;
    		}
    		if (image != null && (dc instanceof HitMissCollection || dc instanceof CountCollection)) {
	    		boolean hit;
	    		if (tp.usesScreen()) {
					if (tp.getName().contains("Dungeon Complete") && !checkForClear()) {
						continue;
					}
	    			m = s.exists(image, 0.01);
	    		} else {
	    			//int[] region = tp.getRegion();
	    	        Region r = regionMap.get(tp.getRegionIndex()); //new Region(region[0], region[1], region[2] - region[0], region[3] - region[1]);
	    			m = r.exists(image, 0.01);
		    		if (m == null && tp.getSecondaryImage() != null) {
		    			m = r.exists(tp.getSecondaryImage(), 0.01);
		    		}
	    		}
	    		/*if (tp.getName().contains("Mark of") && m != null) {
	    			System.out.println(tp.getName()+": "+m.getScore());
	    		}*/
				hit = m != null && m.getScore() >= tp.getThreshold();
				if (tp.getName().contains("Dungeon Complete") && hit) {
					pause();
					break;
				}
				if (hit && tp.getClassIndex() >= 0) {
					activeClass = tp.getClassIndex();
				}
				if (tp.getName().contains("Holy Symbol") && hit)
					((DeltaCollection)data.get(TrackPoint.HOLY_SYMBOL_DAMAGE_RAW)).handleHit(true);
				if (tp.getName().contains("Shield Uptime") && hit)
					shields = Math.max(shields,  1);
				if (tp.getName().contains("2x Shield Uptime") && hit)
					shields = Math.max(shields,  2);
				if (dc instanceof HitMissCollection) {
					if (((HitMissCollection)dc).getTrackPoint() != null) {
						((DeltaCollection)data.get(((HitMissCollection)dc).getTrackPoint())).handleHit(true);
					}
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
    			//System.out.println(tp.getName() +": "+r.toString());
    	        String[] text = r.text().split("\n");
    	        //String text = ocr.read(new Rectangle(region[0], region[1], region[2], region[3]));
    	        BigInteger value = null;
    	        for (String string : text) {
    	        	try {
    	        		value = new BigInteger(string);
    	        		if (!isClose(value, lastHPReading)) { //bad reading
    	        	        if (dc.getData().size() < 5) {
    	        	        	if (secondReading == null) { //could the next reading be more accurate, perhaps?
	    	        	        	secondReading = value;
	    	        	        	value = null;
    	        	        	} else {
    	        	        		if (isClose(value, secondReading)) { //second reading is better
    	        	        			lastHPReading = secondReading;
    	        	        		} else { //second reading is not better
    	        	        			secondReading = value;
    	        	        			value = null;
    	        	        		}
    	        	        	}
    	        	        } else {
        	        			System.out.println("BAD READING! "+value.toString()+"; last: "+lastHPReading.toString());
        	        			value = null;
    	        	        }
    	        		} else {
    	        			lastHPReading = value;
    	        		}
    	        	} catch (Exception e) {
    	        		//e.printStackTrace();
    	        	}
    	        }
    	        if (value != null && tp.getName().contains("HP") && !active) {
    	        	int[] regionBoss = TrackPoint.BOSS.getRegion();
        	        Region r2 = new Region(regionBoss[0], regionBoss[1], regionBoss[2] - regionBoss[0], regionBoss[3] - regionBoss[1]);
	    			m = r2.exists(TrackPoint.BOSS.getImage(), 0.01);
	    			if (m == null) {
	    				System.out.println("Found HP: "+value+", but unable to find [Boss] tag.");
	    			} else {
		    			System.out.println("Starting: "+value);
		    			lastHPReading = value;
		    			firstHPReading = value;
		    			if (m != null && value.compareTo(BigInteger.ZERO) > 0)
		    				start();
	    			}
    	        }
    	        if (active)
    	        	dc.addData(value);
    	        logOutput.println("Adding: "+ value);
    	        logOutput.println(dc.getData().toString());
    		}
    	}
    	((DeltaCollection)data.get(TrackPoint.RAID_DPS)).handleHit(true);
    	((DeltaCollection)data.get(TrackPoint.DMG_MITIGATED)).handleHit(shields > 0, shields == 1 ? 1 : 4);
    	DeltaCollection.preProcess();
    	for (TrackPoint tp : data.keySet()) {
    		DataCollection dc = data.get(tp);
    		if (dc instanceof DeltaCollection) {
    			((DeltaCollection)dc).postProcess();
    		}
    	}
    	DeltaCollection.finalProcess();
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