package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;

import model.DataCollection;
import model.DeltaCollection;
import model.HitMissCollection;
import model.MainDriver;
import model.TimeCollection;
import model.MainDriver.TrackPoint;

public class Graph extends AbstractLabel {

	private static final long serialVersionUID = -5253919380768020634L;
	
	/**
	 * The extra room of margin on a graph. If the max value in a data set is 100, then
	 * a value of 0.25 would set the highest point on the axis to 125.
	 */
	private static final double GRAPH_MARGIN_PERCENT = 0.25;
	
	private static final int[] GRAPH_ORIGIN = {250, 100};
	private static final int[] GRAPH_DIM = {700, 300};

	private static final Color GRAPH_LINE_COLOR = new Color(100, 149, 237);
	private static final int GRAPH_LINE_WIDTH = 4;
	private static final int BUFF_SPACING = 30;
	
	private static final Color DEATH_GRAY = new Color(100, 100, 100);
	
	
	/**
	 * The number of axis markers on the Y axis.
	 */
	private static final int GRAPH_Y_MARKERS = 5;
	
	protected String currentTab = "Personal";
	
	protected Graph(JFrame theFrame, double theXPerc, double theYPerc) {
		super(theFrame, theXPerc, theYPerc);
	}
	
	/**
	 * Returns a value to use as a "prettier" max, for the graph.
	 * This way the axes can be like 25000, 50000, 75000, instead of 24737, etc.
	 * @param value
	 * @return
	 */
	private int makePretty(int value) {
		String valueAsString = Integer.toString(value);
		if (valueAsString.length() == 1) //good enough
			return value;
		else {
			if (valueAsString.charAt(0) == '9') { //something like 990 -> 1000
				return (int)Math.pow(10, valueAsString.length() - 1);
			} else {
				int num = Integer.parseInt(valueAsString.charAt(0) + "");
				int num2 = Integer.parseInt(valueAsString.charAt(1) + "");
				return (num * (int)Math.pow(10, valueAsString.length() - 1)) + (num2 + 1) * (int)Math.pow(10, valueAsString.length() - 2);
			}
		}
	}


    
    private static final TrackPoint[] PERSONAL = {
    	TrackPoint.GUARDIAN, TrackPoint.CELESTIAL_LIGHT,
    	TrackPoint.SHADOW_CHASER, TrackPoint.FATAL_STRIKES,
    	TrackPoint.IRON_DEFENSE, TrackPoint.SHIELD_MASTERY,
    	TrackPoint.SNIPE, TrackPoint.BRONZE_EAGLE, TrackPoint.EAGLES_MAJESTY,
    	TrackPoint.DARKAURA,
    	TrackPoint.POISON_EDGE, TrackPoint.POISON_VIAL,
    	TrackPoint.VARR_WINGS, TrackPoint.WEAPON_PROC,
    };

    
    private static final TrackPoint[] BUFFS = {
    	TrackPoint.HOLY_SYMBOL, TrackPoint.BLESSINGS, TrackPoint.FOCUSSEAL, TrackPoint.SHARPEYES, TrackPoint.HONINGRUNES, TrackPoint.WARHORN
    };

    
    private static final TrackPoint[] DEBUFFS = {
    	TrackPoint.SMITE, TrackPoint.SHIELDTOSS, TrackPoint.MOD, TrackPoint.STATIC_FLASH, TrackPoint.RAGING_TEMPEST
    };

    private static final GuiButton[] BUTTONS = {
		GuiButton.PERSONAL, GuiButton.BUFFS, GuiButton.DEBUFFS
    };

    public void addTooltip(TrackPoint tp, int x, int y) {
    	Tooltip toAdd = new Tooltip(x, y, tp.getName(), tp.getIntro());
    	((Report)myFrame).tooltipMappings.put(toAdd, false);
    }
    

    /**
     * Paints the graph.
     * @param theGraphics The graphics context to use for painting.
     */
    @Override
    public void paintComponent(final Graphics theGraphics) {
        super.paintComponent(theGraphics);
        final Graphics2D g2d = (Graphics2D) theGraphics;
        TimeCollection tc = (TimeCollection)MainDriver.data.get(TrackPoint.TIME);
        HitMissCollection deaths = (HitMissCollection)MainDriver.data.get(TrackPoint.TOMBSTONE);
        int totalTime = tc.getTime(0, tc.getTimeData().size() - 1);
        int timeTicks = (int)Math.ceil((double)totalTime / 30.0);
        List<Integer> raidDamage = ((DeltaCollection)MainDriver.data.get(TrackPoint.RAID_DPS)).getSoftDeltas();
        int highest = -1;
        int mouseIndex = -1;
        List<Point> graphPoints = new ArrayList<Point>();
        if (raidDamage.size() < 1) {
        	//System.out.println("Not enough data to graph.");
        } else {
	        for (int i = 0; i < raidDamage.size(); i++) {
	        	int value = raidDamage.get(i);
	        	if (value > highest)
	        		highest = value;
	        }
	        highest = makePretty((int)((double)highest * (1 + GRAPH_MARGIN_PERCENT)));
	        for (int i = 0; i < raidDamage.size(); i++) {
	        	int value = raidDamage.get(i);
	        	double xPerc = (double)i / (double)raidDamage.size();
	        	double yPerc = (double)value / (double)highest;
	        	int x = GRAPH_ORIGIN[0] + (int)(xPerc * GRAPH_DIM[0]);
	        	int y = GRAPH_ORIGIN[1] + GRAPH_DIM[1] - (int)(yPerc * GRAPH_DIM[1]);
	        	graphPoints.add(new Point(x, y));
	        }
        }
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(GRAPH_ORIGIN[0], GRAPH_ORIGIN[1] - (int)(GRAPH_DIM[1] * (GRAPH_MARGIN_PERCENT)), GRAPH_DIM[0], (int)(GRAPH_DIM[1] * (1 + GRAPH_MARGIN_PERCENT)));

        for (final GuiButton b : BUTTONS) {
        	b.handleDraw(this, theGraphics);
        }
        g2d.setColor(Color.GRAY);
        for (int i = 0; i < timeTicks; i++) {
        	int x = GRAPH_ORIGIN[0] + (GRAPH_DIM[0] / timeTicks) * (i);
        	int y1 = GRAPH_ORIGIN[1] + GRAPH_DIM[1];
        	int y2 = GRAPH_ORIGIN[1] + GRAPH_DIM[1] - (timeTicks % 2 == 0 ? 20 : 10);
        	if (i % 2 == 0 || timeTicks < 4) {
    	        drawNormalText(g2d, MainDriver.timeToString(i * 30), FONT_SIZE / 2, x - 8, y1 + 24, 1, Color.WHITE, SHADOW_COLOR.darker());
        	}
            g2d.setColor(Color.GRAY);
        	g2d.drawLine(x, y1, x, y2);
        	
        }
        Point p = MouseInfo.getPointerInfo().getLocation();
    	Point diff = getLocationOnScreen();
    	p.translate((int)(-1 * diff.getX()), (int)(-1 * diff.getY()));
        for (int i = 0; i < graphPoints.size() - 1; i++) { //draw death areas before y markers
        	boolean dead = deaths.getRawData().get(i);
        	int x1 = (int)graphPoints.get(i).getX();
        	int x2 = (int)graphPoints.get(i + 1).getX();
        	if (dead) {
            	g2d.setColor(DEATH_GRAY);
        		g2d.fillRect(x1, GRAPH_ORIGIN[1], x2 - x1, GRAPH_DIM[1]);
        	}
        }
        for (int i = 0; i < GRAPH_Y_MARKERS; i++) {
        	int x1 = GRAPH_ORIGIN[0];
        	int x2 = GRAPH_ORIGIN[0] + GRAPH_DIM[0];
        	int y = (int)(GRAPH_ORIGIN[1] + GRAPH_DIM[1] * ((double)i / GRAPH_Y_MARKERS));
        	g2d.drawLine(x1, y, x2, y);
        }
        for (int i = 0; i < graphPoints.size() - 1; i++) {
        	int x1 = (int)graphPoints.get(i).getX();
        	int y1 = (int)graphPoints.get(i).getY();
        	int x2 = (int)graphPoints.get(i + 1).getX();
        	int y2 = (int)graphPoints.get(i + 1).getY();
        	g2d.setColor(GRAPH_LINE_COLOR);
        	g2d.setStroke(new BasicStroke(GRAPH_LINE_WIDTH));
        	g2d.drawLine(x1, y1, x2, y2);
        	if (p.getX() >= x1 && p.getX() <= x2) {
        		mouseIndex = i;
        	}
        }
        //draw the numbers on the top
        for (int i = 0; i < GRAPH_Y_MARKERS; i++) {
        	int y = (int)(GRAPH_ORIGIN[1] + GRAPH_DIM[1] * ((double)i / GRAPH_Y_MARKERS));
            int value = (int)(highest - (((double)i / GRAPH_Y_MARKERS) * highest));
	        drawNormalText(g2d, Integer.toString(value), FONT_SIZE, GRAPH_ORIGIN[0], y, 1, Color.WHITE, SHADOW_COLOR.darker());
        }
        
        int x = GRAPH_ORIGIN[0] - 32;
        int y = GRAPH_ORIGIN[1] + GRAPH_DIM[1] + 32;

        TrackPoint[] toUse;
        if (currentTab.equalsIgnoreCase("Personal"))
        	toUse = PERSONAL;
        else if (currentTab.equalsIgnoreCase("Buffs"))
        	toUse = BUFFS;
        else
        	toUse = DEBUFFS;
        	
        for (int i = 0; i < toUse.length; i++) {
	        DataCollection dc = MainDriver.data.get(toUse[i]);
	        List<Boolean> data = ((HitMissCollection)(dc)).getRawData();
	        if (dc.getLast() == 0)
	        	continue;
	        boolean hit = false;
	        int x1 = 0;
        	int x2 = 0;
	        g2d.setColor(Color.GRAY);
	        g2d.fillRect(x - 2, y - 3, GRAPH_DIM[0] + 32, BUFF_SPACING);
	        g2d.setColor(Color.DARK_GRAY);
	        g2d.fillRect(x, y - 1, GRAPH_DIM[0] + 32 - 2, BUFF_SPACING - 2);
	        for (int j = 0; j < data.size(); j++) {
	        	boolean dead = deaths.getRawData().get(j);
	        	if (dead) {
    	        	double xPerc = (double)j / (double)data.size();
    	        	double xPerc2 = Math.min(1, (double)(j + 1) / (double)data.size());
                	int start = GRAPH_ORIGIN[0] + (int)(xPerc * GRAPH_DIM[0]);
                	int end = GRAPH_ORIGIN[0] + (int)(xPerc2 * GRAPH_DIM[0]);
                	g2d.setColor(DEATH_GRAY);
	    	        g2d.fillRect(start, y - 1, end - start, BUFF_SPACING - 2);
	        	}
	        	if (data.get(j)) {
	        		if (!hit) {
	    	        	double xPerc = (double)j / (double)data.size();
	                	x1 = GRAPH_ORIGIN[0] + (int)(xPerc * GRAPH_DIM[0]);
	        		}
	        		hit = true;
	        	} else {
	        		if (hit) {
	        			hit = false;
	    	        	double xPerc = (double)j / (double)data.size();
	                	x2 = GRAPH_ORIGIN[0] + (int)(xPerc * GRAPH_DIM[0]);
	                	g2d.setColor(GRAPH_LINE_COLOR);
	                	g2d.setStroke(new BasicStroke(GRAPH_LINE_WIDTH * 2));
	                	g2d.drawLine(x1, y + 12, x2, y + 12);
	        		}
	        	}
	        	
	        }
            drawImage(theGraphics, toUse[i].getIcon(), x, y);
            addTooltip(toUse[i], x, y);
            y += BUFF_SPACING;
        }
        //add a tooltip for the graph
        if (mouseIndex > -1) {
	        StringBuilder tooltipText = new StringBuilder();
	        tooltipText.append(MainDriver.timeToString(tc.getTime(0, mouseIndex)));
	        tooltipText.append(" - ");
	        tooltipText.append(raidDamage.get(mouseIndex));
	    	Tooltip toAdd = new Tooltip(GRAPH_ORIGIN[0], GRAPH_ORIGIN[1], "Party DPS", tooltipText.toString(), GRAPH_DIM[0], GRAPH_DIM[1]);
	    	for (Tooltip tp : ((Report)myFrame).tooltipMappings.keySet()) {
	    		if (tp.getTitle().contains("Party DPS")) {
	    			((Report)myFrame).tooltipMappings.remove(tp);
	    			break;
	    		}
	    	}
	    	((Report)myFrame).tooltipMappings.put(toAdd, false);
        }
    	
        ((Report)myFrame).drawTooltips(theGraphics, this);
    }
}
