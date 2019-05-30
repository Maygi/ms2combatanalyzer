package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import model.DataCollection;
import model.DeltaCollection;
import model.HitMissCollection;
import model.MainDriver;
import model.TimeCollection;
import model.MainDriver.TrackPoint;

public class Options extends AbstractLabel {
	
	private static final long serialVersionUID = -6526533159294553453L;

	private static final int START_X = 100;
	private static final int START_Y = 100;
	
	private static final int Y_SPACING = 40;
	
	protected Options(JFrame theFrame, double theXPerc, double theYPerc) {
		super(theFrame, theXPerc, theYPerc);
	}
    
    private static final TrackPoint[] ITEMS = {
    	TrackPoint.GUARDIAN, TrackPoint.CELESTIAL_LIGHT,
    	TrackPoint.HEAVENS_WRATH, TrackPoint.GREATER_HEAL,
    	
    	TrackPoint.SHADOW_CHASER, TrackPoint.FATAL_STRIKES,
    	//TrackPoint.SHADOW_STANCE,
    	    	
    	TrackPoint.IRON_DEFENSE, TrackPoint.SHIELD_MASTERY,
    	TrackPoint.DIVINE_RETRIBUTION,
    	
    	TrackPoint.SNIPE, TrackPoint.BRONZE_EAGLE, TrackPoint.EAGLES_MAJESTY,
    	TrackPoint.RANGERS_FOCUS, TrackPoint.ARCHERS_SECRETS,
    	
    	/*TrackPoint.DARKAURA,
    	TrackPoint.BLOOD_FURY, TrackPoint.RAGING_SOUL,*/
    	
    	TrackPoint.POISON_EDGE, TrackPoint.POISON_VIAL, TrackPoint.RETALIATION, TrackPoint.MESOGUARD, TrackPoint.HASTE,
    	//TrackPoint.RUSH,
    	
    	TrackPoint.FLAME_WAVE, TrackPoint.FLAME_IMP, TrackPoint.MANA_CONTROL, TrackPoint.MANA_CONTROL2,
    	TrackPoint.PERFECT_STORM, TrackPoint.FROST, TrackPoint.CHILL,
    	
    	/*TrackPoint.OVERCOME, TrackPoint.FIGHTING_SPIRIT, TrackPoint.PATTERN_BREAK,
    	TrackPoint.MERIDIAN_FLOW, TrackPoint.MERIDIAN_FLOW2, TrackPoint.MERIDIAN_FLOW3,*/
    	
    	TrackPoint.PINK_BEANS_PRANK, TrackPoint.ROOTED_STRENGTH,
    	TrackPoint.VARR_WINGS, TrackPoint.WEAPON_PROC
    };

    
    private static final TrackPoint[] BUFFS = {
    	TrackPoint.HOLY_SYMBOL, TrackPoint.BLESSINGS, TrackPoint.FOCUSSEAL, TrackPoint.SHARPEYES, TrackPoint.HONINGRUNES, TrackPoint.WARHORN
    };

    
    private static final TrackPoint[] DEBUFFS = {
    	TrackPoint.SMITE, TrackPoint.SHIELDTOSS, TrackPoint.MOD, TrackPoint.STATIC_FLASH, TrackPoint.RAGING_TEMPEST
    };

    private static final GuiButton[] BUTTONS = {
		GuiButton.PERSONAL, GuiButton.BUFFS, GuiButton.DEBUFFS, GuiButton.UP, GuiButton.DOWN
    };

    public void addTooltip(TrackPoint tp, int x, int y) {
    	Tooltip toAdd = new Tooltip(x, y, tp.getName(), tp.getIntro());
    	((OptionsFrame)myFrame).tooltipMappings.put(toAdd, false);
    }
    

    /**
     * Paints the graph.
     * @param theGraphics The graphics context to use for painting.
     */
    @Override
    public void paintComponent(final Graphics theGraphics) {
        super.paintComponent(theGraphics);
        final Graphics2D g2d = (Graphics2D) theGraphics;
        
        int x = START_X;
        int y = START_Y;
        for (int i = 0; i < ITEMS.length; i++) {
            drawImage(theGraphics, ITEMS[i].getIcon(), x, y);
            addTooltip(ITEMS[i], x, y);
            y += Y_SPACING;
        }
    	
        ((OptionsFrame)myFrame).drawTooltips(theGraphics, this);
    }
}
