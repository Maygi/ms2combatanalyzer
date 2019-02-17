package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.border.BevelBorder;

import model.DataCollection;
import model.MainDriver;
import model.MainDriver.TrackPoint;
import particles.CircleParticle;
import particles.Particle;
import particles.ParticleEmitter;

/**
 * This is the main GUI for the data chart.
 * Lots of hard coding, I know.
 * @author May
 */
public class Chart extends AbstractLabel {

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
    
    public enum ChartButton {
    	PAUSE(410, 12, "images/ui/pause.png"),
    	RESET(440, 12, "images/ui/reset.png"),
    	CLOSE(470, 12, "images/ui/close.png");
    	
    	public static final int BUTTON_SIZE = 20;
    	private int x, y;
    	private String image;
    	private ChartButton(int x, int y, String image) {
    		this.x = x;
    		this.y = y;
    		this.image = image;
    	}
    	public int[] getCoords() {
    		return new int[]{x, y};
    	}
    	public String getImage() {
    		return image;
    	}
    }
    
    private static final TrackPoint[] PERSONAL = {
    	TrackPoint.SPIRIT,
    	TrackPoint.GUARDIAN, TrackPoint.CELESTIAL_LIGHT,
    	TrackPoint.SHADOW_CHASER,
    	TrackPoint.IRON_DEFENSE,
    	TrackPoint.SNIPE,
    	TrackPoint.DARKAURA,
    	TrackPoint.POISON_EDGE, TrackPoint.POISON_VIAL,
    	TrackPoint.VARR_WINGS, TrackPoint.WEAPON_PROC
    };
    
    private static final TrackPoint[] RAID = {
    	TrackPoint.RAID_DPS, TrackPoint.TIME, TrackPoint.BOSS_HEAL
    };
    
    private static final TrackPoint[] BUFFS = {
    	TrackPoint.BLESSINGS, TrackPoint.FOCUSSEAL, TrackPoint.SHARPEYES, TrackPoint.HONINGRUNES, TrackPoint.WARHORN,
    	TrackPoint.HOLY_SYMBOL_DAMAGE, TrackPoint.HOLY_SYMBOL_DAMAGE_RAW
    };
    
    private static final TrackPoint[] DEBUFFS = {
    	TrackPoint.SMITE, TrackPoint.SHIELDTOSS, TrackPoint.MOD, TrackPoint.STATIC_FLASH, TrackPoint.RAGING_TEMPEST
    };
    
    private JFrame myFrame;

	protected Chart(JFrame theFrame) {
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

    public void addTooltip(TrackPoint tp, int x, int y) {
    	Tooltip toAdd = new Tooltip(x, y, tp.getName(), tp.getIntro());
    	((Overlay)myFrame).tooltipMappings.put(toAdd, false);
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
					format(new BigDecimal((int)(dc.getLast() / MainDriver.getEllaspedTime())))  + " DPS";
	        	image = "images/tableicons/partydps.png";
			} else if (RAID[i].getName().equalsIgnoreCase("Time")) {
				text = MainDriver.timeToString(MainDriver.getEllaspedTime());
				image = RAID[i].getIcon();
			} else {
				if (dc.getLast() <= 0)
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
        		line -= 3;
        		xOffset = 100;
        	}
	        DataCollection dc = MainDriver.data.get(PERSONAL[i]);
	        try {
	        	text = dc.getLast() + "%";
	        } catch (Exception e) {
	        	continue;
	        }
			if (PERSONAL[i].getName().contains("SP")) {
				image = "images/tableicons/spirit.png";
			} else { //personal buff or debuff
				image = PERSONAL[i].getIcon();
				if (dc.getLast() <= 0) //don't show other class properties
					continue;
			}
			line++;
	        drawNormalText(g2d, text, FONT_SIZE, 24 + MARGIN * 2 + xOffset,
	                (int) (getSize().getHeight() * TEXT_PERCENT) * line, 1,
	                Color.WHITE, SHADOW_COLOR.darker());
	        int x = MARGIN + xOffset;
	        int y =(int) (getSize().getHeight() * TEXT_PERCENT) * line - 20;
            drawImage(theGraphics, image, x, y);
            addTooltip(PERSONAL[i], x, y);
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
	        	sb.append(dc.getLast());
	        	sb.append("%");
	        }
			/*if (BUFFS[i].getName().contains("Blessings")) {
				sb.append(" | ");
				sb.append(MainDriver.data.get(TrackPoint.BLESSINGS_AMP).getLast());
			}
			if (BUFFS[i].getName().contains("Smiting")) {
				sb.append(" | ");
				sb.append(MainDriver.data.get(TrackPoint.SMITE_AMP).getLast());
			}*/
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
        String mscaText = "MS2 Combat Analyzer v1.0";
        drawNormalText(g2d, mscaText, Tooltip.TEXT_SIZE * 3 / 4, (int)getSize().getWidth() - 200,
        		(int)getSize().getHeight() - 20, 1,
                Color.WHITE, SHADOW_COLOR.darker());
        drawNormalText(g2d, "<3 Maygi", Tooltip.TEXT_SIZE * 3 / 4, (int)getSize().getWidth() - 100,
        		(int)getSize().getHeight() - 5, 1,
                Color.WHITE, SHADOW_COLOR.darker());
        drawImage(theGraphics, "images/ui/avi.gif", (int)getSize().getWidth() - 40, (int)getSize().getHeight() - 40);

        drawText(g2d, "Debuffs", FONT_SIZE, MARGIN + (int)((GAP * 1.6)),
                (int) (getSize().getHeight() * TEXT_PERCENT), 1,
                Color.WHITE, SHADOW_COLOR.darker());
        line = 1;
        for (int i = 0; i < DEBUFFS.length; i++) {
	        DataCollection dc = MainDriver.data.get(DEBUFFS[i]);
	        StringBuilder sb = new StringBuilder();
	        sb.append(dc.getLast());
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
        for (final ChartButton b : ChartButton.class.getEnumConstants()) {
        	int[] coords = b.getCoords();
            drawImage(theGraphics, b.getImage(), coords[0],  coords[1]);
            Point p = MouseInfo.getPointerInfo().getLocation();
            if (p != null) {
            	Point diff = getLocationOnScreen();
            	p.translate((int)(-1 * diff.getX()), (int)(-1 * diff.getY()));
                if (p.getX() >= coords[0] && p.getX() <= coords[0] + ChartButton.BUTTON_SIZE &&
                		p.getY() >= coords[1] && p.getY() <= coords[1] + ChartButton.BUTTON_SIZE) {
                	if (b.getImage().contains("pause") && !MainDriver.active) {
                		//discrete logic where
                	} else {
                		drawSquare(theGraphics, coords[0], coords[1], ChartButton.BUTTON_SIZE, Color.WHITE, (float) 0.5);
                	}
                }
            }
        	if (b.getImage().contains("pause") && !MainDriver.active) {
        		drawSquare(theGraphics, coords[0], coords[1], ChartButton.BUTTON_SIZE, Color.BLACK, (float) 0.5);
        	}
        }
        Tooltip hover = ((Overlay)myFrame).hoveredTooltip;
        if (hover != null) {
            Point p = MouseInfo.getPointerInfo().getLocation();
        	Point diff = getLocationOnScreen();
        	p.translate((int)(-1 * diff.getX()), (int)(-1 * diff.getY()));
        	p.translate(20, 20);
        	//g2d.getFontMetrics().stringWidth(hover.getTitle())
	        String[] realLines = getLines(hover.getText(), g2d);
	        int boxY = Tooltip.TEXT_SIZE * (realLines.length + 1) + MARGIN * 3;
	        int yOffset = 0;
	        if ((int)p.getY() + boxY > getBounds().getHeight()) {
	        	yOffset = -1 * boxY;
	        }
	        if (p.getY() + yOffset + boxY > getBounds().getHeight()) {
	        	int total = (int)(p.getY() + yOffset + boxY);
	        	yOffset -= total - getBounds().getHeight();
	        }
    		drawRect(theGraphics, (int)p.getX(), (int)p.getY() + yOffset, (int)(getMaxLineWidth(g2d, hover.getTitle(), realLines) * 0.75) + MARGIN * 2,
    				boxY, Color.BLACK, (float) 0.7);
	        drawNormalText(g2d, hover.getTitle(), Tooltip.TEXT_SIZE, (int)p.getX() + MARGIN, (int)p.getY() + MARGIN * 3 + yOffset, 1,
	                Color.WHITE, SHADOW_COLOR.darker());
	        for (int i = 0; i < realLines.length; i++) {
		        drawNormalText(g2d, realLines[i], Tooltip.TEXT_SIZE, (int)p.getX() + MARGIN,
		        		(int)p.getY() + MARGIN * 5 + Tooltip.TEXT_SIZE * (i + 1) + yOffset, 1, Color.WHITE, SHADOW_COLOR.darker());
	        }
        }
    }
    
    private int getMaxLineWidth(Graphics2D g2d, String title, String[] lines) {
    	int maxWidth = 0;
    	for (String s : lines) {
    		if (s == null)
    			continue;
    		int width = (int)g2d.getFontMetrics().stringWidth(s);
    		if (width > maxWidth)
    			maxWidth = width;
    	}
    	return Math.max(maxWidth, g2d.getFontMetrics().stringWidth(title));
    }
    
    private String[] getLines(String text, Graphics2D g2d) {
    	final int WIDTH = 240;
    	int lines = (int)(Math.ceil((double)g2d.getFontMetrics().stringWidth(text) / (double)WIDTH));
    	String[] toReturn = new String[lines];
    	int word = 0;
    	String[] words = text.split(" ");
    	int index = 0;
    	while (word < words.length && text.length() > 0) {
    		StringBuilder newLine = new StringBuilder();
    		while (word < words.length && g2d.getFontMetrics().stringWidth(newLine.toString()) < WIDTH) {
    			newLine.append(words[word]);
    			newLine.append(" ");
    			if (g2d.getFontMetrics().stringWidth(newLine.toString().substring(0, newLine.toString().length() - 1)) > WIDTH) { //carry-over
    				String oversize = newLine.toString().substring(0, newLine.toString().length() - 1);
    				newLine = new StringBuilder();
    				newLine.append(oversize.substring(0, oversize.lastIndexOf(" ")));
    				break;
    			} else {
    				word++;
    			}
    		}
    		toReturn[index] = newLine.toString();
    		index++;
    	}
    	return toReturn;
    }

}
