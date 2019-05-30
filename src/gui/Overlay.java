package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.border.BevelBorder;

import model.DataCollection;
import model.MainDriver;
import model.MainDriver.TrackPoint;
import model.TimeCollection;
import particles.Particle;

/**
 * This is the main GUI for the data chart.
 * Lots of hard coding, I know.
 * @author May
 */
public class Overlay extends AbstractLabel {

	private static final long serialVersionUID = -6204287424593635872L;
    
    /**
     * The margin (in pixels) between an icon and the text.
     */
    private static final int MARGIN = 6;
    
    /**
     * The horizontal distance between table columns.
     */
    private static final int GAP = 180;
    
    /**
     * The percentage of the height of each line of text.
     */
    private static final double TEXT_PERCENT = 0.12;
    
    private static final GuiButton[] BUTTONS = {
		GuiButton.UPDATE, 
		GuiButton.CLOSE, GuiButton.MUTE, GuiButton.PAUSE,
		GuiButton.REPORT, GuiButton.RESET
    };
    
    
    private static final TrackPoint[] PERSONAL = {
    	TrackPoint.SPIRIT,
    	TrackPoint.GUARDIAN, TrackPoint.CELESTIAL_LIGHT,
    	TrackPoint.HEAVENS_WRATH, TrackPoint.GREATER_HEAL,
    	
    	TrackPoint.SHADOW_CHASER, TrackPoint.FATAL_STRIKES,
    	TrackPoint.SHADOW_STANCE,
    	    	
    	TrackPoint.IRON_DEFENSE, TrackPoint.SHIELD_MASTERY,
    	TrackPoint.DIVINE_RETRIBUTION,
    	
    	TrackPoint.SNIPE, TrackPoint.BRONZE_EAGLE, TrackPoint.EAGLES_MAJESTY,
    	TrackPoint.RANGERS_FOCUS, TrackPoint.ARCHERS_SECRETS,
    	
    	TrackPoint.DARKAURA,
    	TrackPoint.BLOOD_FURY, TrackPoint.RAGING_SOUL,
    	
    	TrackPoint.POISON_EDGE, TrackPoint.POISON_VIAL, TrackPoint.RETALIATION, TrackPoint.MESOGUARD, TrackPoint.HASTE,
    	TrackPoint.RUSH,
    	
    	TrackPoint.FLAME_WAVE, TrackPoint.FLAME_IMP, TrackPoint.MANA_CONTROL, TrackPoint.MANA_CONTROL2,
    	TrackPoint.PERFECT_STORM, TrackPoint.FROST, TrackPoint.CHILL,
    	
    	TrackPoint.OVERCOME, TrackPoint.FIGHTING_SPIRIT, TrackPoint.PATTERN_BREAK,
    	TrackPoint.MERIDIAN_FLOW, TrackPoint.MERIDIAN_FLOW2, TrackPoint.MERIDIAN_FLOW3,
    	
    	TrackPoint.PINK_BEANS_PRANK,
    	TrackPoint.VARR_WINGS, TrackPoint.WEAPON_PROC
    };
    
    private static final TrackPoint[] RAID = {
    	TrackPoint.RAID_DPS, TrackPoint.TIME
    };
    
    private static final TrackPoint[] BUFFS = {
    	TrackPoint.BLESSINGS, TrackPoint.VITALITY, TrackPoint.FOCUSSEAL, TrackPoint.SHARPEYES, TrackPoint.GREATER_SHARP_EYES,
    	TrackPoint.HONINGRUNES, TrackPoint.WARHORN,
    	TrackPoint.HOLY_SYMBOL_DAMAGE, TrackPoint.HOLY_SYMBOL_DAMAGE_RAW
    };
    
    private static final TrackPoint[] DEBUFFS = {
    	TrackPoint.SMITE, TrackPoint.PURIFYING_LIGHT, 
    	TrackPoint.SHIELDTOSS, TrackPoint.CYCLONE_SHIELD,
    	TrackPoint.MOD, 
    	TrackPoint.SOUL_FLOCK, TrackPoint.STATIC_FLASH, 
    	TrackPoint.SOUL_DISSONANCE, TrackPoint.RAGING_TEMPEST
    };
    
    private static final TrackPoint[] MISC = {
    	TrackPoint.BOSS_HEAL
    };
    
    private JFrame myFrame;

	protected Overlay(JFrame theFrame) {
		super(theFrame, 1, 1);
		myFrame = theFrame;
		setup();
	}
    
    /**
     * Handles various setup methods.
     */
    private void setup() {
        setBackground(BASE_COLOR);
        //setOpaque(true);
        setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, BASE_COLOR.brighter(),
                                                  BASE_COLOR.darker()));

        setVisible(true);
    }

    public void replaceTooltip(TrackPoint tp, int x, int y) {
    	Tooltip toAdd = new Tooltip(x, y, tp.getName(), tp.getIntro());
    	OverlayFrame o = ((OverlayFrame)myFrame);
    	for (final Tooltip t : o.tooltipMappings.keySet()) {
    		if (t.getCoords()[0] == x && t.getCoords()[1] == y) {
    			o.tooltipMappings.remove(t);
    			break;
    		}
    	}
    	((OverlayFrame)myFrame).tooltipMappings.put(toAdd, false);
    }

    public void addTooltip(TrackPoint tp, int x, int y) {
    	Tooltip toAdd = new Tooltip(x, y, tp.getName(), tp.getIntro());
    	((OverlayFrame)myFrame).tooltipMappings.put(toAdd, false);
    }
    
    /**
     * Returns the amount of decimal places in a BigDecimal.
     * @param bigDecimal The number to check.
     * @return The number of decimal places.
     */
	public static int getDecimalPlaces(BigDecimal bigDecimal) {
		String string = bigDecimal.stripTrailingZeros().toPlainString();
		int index = string.indexOf(".");
		return index < 0 ? 0 : string.length() - index - 1;
	}
	
	/**
	 * Formats a number to take less space.
	 * @param number The number to format.
	 * @return A formatted value of the number, using K, M, or B to shorten it.
	 */
	public static String format(BigDecimal number) {
		if (number.compareTo(BigDecimal.ZERO) == 0)
			return "0";
		String[] marks = { "1000000000", "1000000", "1000", "1" };
		String[] labels = { "B", "M", "K", "" };
		BigDecimal value;
		StringBuilder sb = new StringBuilder();
		DecimalFormat df = new DecimalFormat("0.##");
		int decimalPlaces, numberPlaces, totalPlaces;
		for (int i = 0; i < marks.length; i++) {
			if (number.compareTo(new BigDecimal(marks[i])) == 1 || number.compareTo(new BigDecimal(marks[i])) == 0) {
				value = number.divide(new BigDecimal(marks[i]));
				value = value.multiply(new BigDecimal("1000"));
				value = value.setScale(0, RoundingMode.FLOOR);
				value = value.divide(new BigDecimal("1000"));
				numberPlaces = value.toBigInteger().toString().length();
				decimalPlaces = getDecimalPlaces(value);
				if (decimalPlaces <= 1) {
					sb.append(df.format(value));
					sb.append(labels[i]);
				} else {
					totalPlaces = numberPlaces + decimalPlaces;
					if (totalPlaces <= 4) {
						sb.append(df.format(value.multiply(new BigDecimal("1000"))));
						sb.append(labels[i + 1]);
					} else { // don't bring it down - just round
						sb.append(df.format(value.setScale(6 - totalPlaces, BigDecimal.ROUND_HALF_UP)));
						sb.append(labels[i]);
					}
				}
				break;
			}
		}
		return sb.toString();
	}

    /**
     * Paints the chart.
     * @param theGraphics The graphics context to use for painting.
     */
    @Override
    public void paintComponent(final Graphics theGraphics) {
        super.paintComponent(theGraphics);
        final Graphics2D g2d = (Graphics2D) theGraphics;
        drawFilm(theGraphics, MainDriver.MAIN_COLOR);
        if (!MainDriver.active) {
            drawFilm(theGraphics, Color.GRAY);
        }
        if (MainDriver.started) {
	        drawText(g2d, "Party Stats", FONT_SIZE, MARGIN,
	                (int) (getSize().getHeight() * TEXT_PERCENT), 1,
	                Color.WHITE, SHADOW_COLOR.darker());
	        int line = 1;
			String text = "";
	        String image = "";
	        
	        //here comes the hardcoding, do-do-do-do...
	        for (int i = 0; i < RAID.length; i++) {
	        	text = "";
		        DataCollection dc = MainDriver.data.get(RAID[i]);
				if (RAID[i].getName().equalsIgnoreCase("Raid DPS")) {
					text = MainDriver.getEllaspedTime() == 0 ? "---" : 
						format(new BigDecimal((dc.getLast().divide(new BigInteger(Integer.toString(MainDriver.getEllaspedTime()))))))  + " DPS";
		        	image = "images/tableicons/partydps.png";
				} else if (RAID[i].getName().equalsIgnoreCase("Time")) {
					text = MainDriver.timeToString(MainDriver.getEllaspedTime()) + " | " + 
							(((TimeCollection)(MainDriver.data.get(TrackPoint.TIME))).getEstimate());
					image = RAID[i].getIcon();
				} else {
					if (dc.getLast().compareTo(BigInteger.ZERO) <= 0)
						continue;
		        	text = format(new BigDecimal(dc.getLast().toString()));
					image = RAID[i].getIcon();
				}
				line++;
		        drawNormalText(g2d, text, FONT_SIZE, 24 + MARGIN * 2,
		                (int) (getSize().getHeight() * TEXT_PERCENT) * line, 1,
		                Color.WHITE, SHADOW_COLOR.darker());
		        int x = MARGIN;
		        int y = (int) (getSize().getHeight() * TEXT_PERCENT) * line - 20;
	            drawImage(theGraphics, image, x, y);
	            addTooltip(RAID[i], x, y);
	        }
	        line++;
	        drawText(g2d, "Personal Stats", FONT_SIZE, MARGIN,
	                (int) (getSize().getHeight() * TEXT_PERCENT) * line, 1,
	                Color.WHITE, SHADOW_COLOR.darker());
	    	int xOffset = 0;
	        for (int i = 0; i < PERSONAL.length; i++) {
	        	if (line > 7) {
	        		line -= 4;
	        		xOffset = 100;
	        	}
		        DataCollection dc = MainDriver.data.get(PERSONAL[i]);
		        try {
		        	text = dc.getLastInt() + "%";
		        } catch (Exception e) {
		        	continue;
		        }
				if (PERSONAL[i].getName().contains("SP Ef")) {
					image = "images/tableicons/spirit.png";
				} else { //personal buff or debuff
					image = PERSONAL[i].getIcon();
					if (dc.getLastInt() <= 0) //don't show other class properties
						continue;
				}
				line++;
		        drawNormalText(g2d, text, FONT_SIZE, 24 + MARGIN * 2 + xOffset,
		                (int) (getSize().getHeight() * TEXT_PERCENT) * line, 1,
		                Color.WHITE, SHADOW_COLOR.darker());
		        int x = MARGIN + xOffset;
		        int y = (int) (getSize().getHeight() * TEXT_PERCENT) * line - 20;
	            drawImage(theGraphics, image, x, y);
	            replaceTooltip(PERSONAL[i], x, y);
	        }
	
	        drawText(g2d, "Buffs", FONT_SIZE, MARGIN + GAP,
	                (int) (getSize().getHeight() * TEXT_PERCENT), 1,
	                Color.WHITE, SHADOW_COLOR.darker());
	        line = 1;
	        for (int i = 0; i < BUFFS.length; i++) {
		        DataCollection dc = MainDriver.data.get(BUFFS[i]);
		        StringBuilder sb = new StringBuilder();
		        if (BUFFS[i].getName().contains("Holy Symbol")) {
		    		if (dc == null) {
		    			continue;
		    		}
		    		if (BUFFS[i].getName().contains("Raw")) {
			        	sb.append(format(new BigDecimal(dc.getLast())));
		    		} else {
			        	String lastString = dc.getLastString();
			        	sb.append(lastString == null ? "---" : format(new BigDecimal(lastString)));
		    		}
		        } else {
		        	sb.append(dc.getLastAsString());
		        	sb.append("%");
		        }
		        if (new BigInteger(dc.getLastAsString()).compareTo(BigInteger.ZERO) <= 0)
		        	continue;
				text = sb.toString();
				line++;
		        drawNormalText(g2d, text, FONT_SIZE, 24 + MARGIN * 2 + GAP,
		                (int) (getSize().getHeight() * TEXT_PERCENT) * line, 1,
		                Color.WHITE, SHADOW_COLOR.darker());
		        int x = MARGIN + GAP;
		        int y = (int) (getSize().getHeight() * TEXT_PERCENT) * line - 20;
	            drawImage(theGraphics, BUFFS[i].getIcon(), x, y);
	            addTooltip(BUFFS[i], x, y);
	        }
	        drawText(g2d, "Debuffs", FONT_SIZE, MARGIN + (int)((GAP * 1.6)),
	                (int) (getSize().getHeight() * TEXT_PERCENT), 1,
	                Color.WHITE, SHADOW_COLOR.darker());
	        line = 1;
	        for (int i = 0; i < DEBUFFS.length; i++) {
		        DataCollection dc = MainDriver.data.get(DEBUFFS[i]);
		        StringBuilder sb = new StringBuilder();
		        if (new BigInteger(dc.getLastAsString()).compareTo(BigInteger.ZERO) <= 0)
		        	continue;
		        sb.append(dc.getLastAsString());
				sb.append("%");
				if (DEBUFFS[i].getName().contains("Smiting")) {
					sb.append(" | ");
					sb.append(format(new BigDecimal(MainDriver.data.get(TrackPoint.SMITE_AMP).getLast())));
				}
				if (DEBUFFS[i].getName().contains("Shield Toss")) {
					sb.append(" | ");
					sb.append(format(new BigDecimal(MainDriver.data.get(TrackPoint.SHIELDTOSS_AMP).getLast())));
				}
				if (DEBUFFS[i].getName().contains("Mark of Death")) {
					sb.append(" | ");
					sb.append(format(new BigDecimal(MainDriver.data.get(TrackPoint.MOD_AMP).getLast())));
				}
				if (DEBUFFS[i].getName().contains("Static Flash")) {
					sb.append(" | ");
					sb.append(format(new BigDecimal(MainDriver.data.get(TrackPoint.STATIC_FLASH_AMP).getLast())));
				}
				text = sb.toString();
				line++;
		        drawNormalText(g2d, text, FONT_SIZE, 24 + MARGIN * 2 + (int)((GAP * 1.6)),
		                (int) (getSize().getHeight() * TEXT_PERCENT) * line, 1,
		                Color.WHITE, SHADOW_COLOR.darker());
		        int x = MARGIN + (int)((GAP * 1.6));
		        int y = (int) (getSize().getHeight() * TEXT_PERCENT) * line - 20;
	            drawImage(theGraphics, DEBUFFS[i].getIcon(), x, y);
	            addTooltip(DEBUFFS[i], x, y);
	        }
	        line = 5;
	        for (int i = 0; i < MISC.length; i++) {
	        	text = "";
		        DataCollection dc = MainDriver.data.get(MISC[i]);
				if (dc.getLast().compareTo(BigInteger.ZERO) <= 0)
					continue;
	        	text = format(new BigDecimal(dc.getLast().toString()));
				image = MISC[i].getIcon();
				line++;
		        drawNormalText(g2d, text, FONT_SIZE, 24 + MARGIN * 3 + (int)((GAP * 2.3)),
		                (int) (getSize().getHeight() * TEXT_PERCENT) * line, 1,
		                Color.WHITE, SHADOW_COLOR.darker());
		        int x = MARGIN + (int)((GAP * 2.3));
		        int y = (int) (getSize().getHeight() * TEXT_PERCENT) * line - 20;
	            drawImage(theGraphics, image, x, y);
	            addTooltip(MISC[i], x, y);
	        }
        } else {
        	if (!MainDriver.VERSION.equals(MainDriver.liveVersion))  {
		        drawText(g2d, "New build available!", FONT_SIZE,  (int) (getSize().getWidth() * 0.3),
		                (int) (getSize().getHeight() * 0.3), 1,
		                Color.WHITE, SHADOW_COLOR.darker());
		        drawNormalTextCentered(g2d, "Click the red \"!\" button to download it!", FONT_SIZE,  (int) (getSize().getWidth() * 0.5),
		                (int) (getSize().getHeight() * 0.55), 1,
		                Color.WHITE, SHADOW_COLOR.darker());
        		
        	} else {
		        drawText(g2d, "Waiting for data...", FONT_SIZE,  (int) (getSize().getWidth() * 0.3),
		                (int) (getSize().getHeight() * 0.3), 1,
		                Color.WHITE, SHADOW_COLOR.darker());
		        drawNormalTextCentered(g2d, "Enter combat with a boss to begin!", FONT_SIZE,  (int) (getSize().getWidth() * 0.5),
		                (int) (getSize().getHeight() * 0.55), 1,
		                Color.WHITE, SHADOW_COLOR.darker());
		        drawNormalTextCentered(g2d, "(make sure UI size is set to 50%)", FONT_SIZE,  (int) (getSize().getWidth() * 0.5),
		                (int) (getSize().getHeight() * 0.7), 1,
		                Color.WHITE, SHADOW_COLOR.darker());
        	}
        }

        String mscaText = "MS2 Combat Analyzer v" + MainDriver.VERSION;
        drawNormalText(g2d, mscaText, Tooltip.TEXT_SIZE * 3 / 4, (int)getSize().getWidth() - 200,
        		(int)getSize().getHeight() - 20, 1,
                Color.WHITE, SHADOW_COLOR.darker());
        drawNormalText(g2d, "<3 Maygi", Tooltip.TEXT_SIZE * 3 / 4, (int)getSize().getWidth() - 100,
        		(int)getSize().getHeight() - 5, 1,
                Color.WHITE, SHADOW_COLOR.darker());
        drawImage(theGraphics, "images/ui/avi.gif", (int)getSize().getWidth() - 40, (int)getSize().getHeight() - 40);
        
        
        
    	/*for (TrackPoint tp : MainDriver.data.keySet()) {
    		DataCollection dc = MainDriver.data.get(tp);
    		String text = "";
    		if (tp.getName().equalsIgnoreCase("HP") || tp.getName().contains("Infernog") || tp.getName().contains("Wings"))
    			continue;
    		if (dc instanceof HitMissCollection) {
    			if (!MainDriver.active)
    				text = tp.getIntro();
    			else
    				text = tp.getName() + " "+dc.getLast()+"%";
    		} else {
    			if (!MainDriver.active)
    				text = tp.getIntro();
    			else
    				text = tp.getName() + " "+dc.getLast();    			
    		}
            line++;
            drawImage(theGraphics, "images/tableicons/partydps.png", MARGIN,  (int) (getSize().getHeight() * TEXT_PERCENT) * line - 20);
            drawStrongText(g2d, text, FONT_SIZE, 24 + MARGIN * 2,
                    (int) (getSize().getHeight() * TEXT_PERCENT) * line, 1,
                    Color.WHITE, SHADOW_COLOR.darker());
    	}*/
        for (final Particle p : myParticles) {
            p.draw(g2d);
        }
        for (final GuiButton b : BUTTONS) {
        	if (b.getImage().contains("update")) {
        		if (MainDriver.started || MainDriver.VERSION.equals(MainDriver.liveVersion))
        			continue;
        	}
        	b.handleDraw(this, theGraphics);
        }
        ((OverlayFrame)myFrame).drawTooltips(theGraphics, this);
    }
}
