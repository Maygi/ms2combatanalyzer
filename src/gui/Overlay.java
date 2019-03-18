package gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.SpringLayout;

import model.MainDriver;
import sound.Sound;

/**
 * The main JFrame that contains the chart and handles several mouse events.
 * @author May
 */
public class Overlay extends AbstractFrame {
	
	private final SpringLayout myLayout;
	
	private final Chart myChart;
    
	private static final long serialVersionUID = -2095870715941752227L;
	
	private int x;
    private int y;
    
    private boolean hover = false;

    public Overlay() {
        super("");
        //setIconImage((new ImageIcon("images/ui/avi.gif")).getImage());
        setPreferredSize(getPreferredSize());
        setMinimumSize(getPreferredSize());
        myLayout = new SpringLayout();
        myChart = new Chart(this);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(500, 260);
    }
    
    /**
     * Loads various properties from the property file.
     */
    private void loadProps() {
        try {
        	int propX = Integer.parseInt(MainDriver.props.getProperty("x"));
        	int propY = Integer.parseInt(MainDriver.props.getProperty("y"));
        	setLocation(propX, propY);
        } catch (Exception e) {
        	System.err.println("Unable to load properties on Overlay.");
        }
    }
    
    public void openReport() {
    	Report report = new Report();
    	report.start();
    	Sound.SELECT.play();
    }
    
    public void start() {
    	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    	x = (int) (screenSize.getWidth() / 2);
    	y = (int) (screenSize.getHeight() / 2);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setAutoRequestFocus(false);
        setFocusableWindowState(false);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        setAlwaysOnTop(true);
        
        getRootPane().putClientProperty("apple.awt.draggableWindowBackground", false);
        setLocationRelativeTo(null);
        setVisible(true);
        addKeyListener(new KeyboardListener());
    	loadProps();
        
        addMouseListener(new MouseAdapter(){
           public void mousePressed(MouseEvent ev) {
	            x = ev.getX();
	            y = ev.getY();
	            for (final GuiButton b : GuiButton.class.getEnumConstants()) {
	            	int[] coords = b.getCoords();
	                if (x >= coords[0] && x <= coords[0] +  b.getWidth()  &&
	                		y >= coords[1] && y <= coords[1] + b.getHeight()) {
	                	if (b.getImage().contains("report")) {
	                		openReport();
	                	}
	                	if (b.getImage().contains("mute")) {
	                		MainDriver.toggleMute();
	                	}
	                	if (b.getImage().contains("pause")) {
	                		MainDriver.pause();
	                	}
	                	if (b.getImage().contains("reset")) {
	                		MainDriver.reset();
	                	}
	                	if (b.getImage().contains("close")) {
	                		MainDriver.saveProps();
	                		Sound.SELECT.play();
	                		System.exit(0);
	                	}
	                }
	            }
           }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
        	public void mouseMoved(MouseEvent ev) {
        		hoveredTooltip = null;
	            x = ev.getX();
	            y = ev.getY();
	            boolean hit = false;
	            for (final GuiButton b : GuiButton.class.getEnumConstants()) {
	            	int[] coords = b.getCoords();
	                if (x >= coords[0] && x <= coords[0] + b.getWidth() &&
	                		y >= coords[1] && y <= coords[1] + b.getHeight()) {
	                	if (!hover) {
	                    	if (b.getImage().contains("pause") && !MainDriver.active) {
	                    		//discrete logic (^:
	                    	} else {
	                    		Sound.HOVER.play();	                    		
	                    	}
	                	}
	                	myChart.repaint();
	                	hover = true;
	                	hit = true;
	                }
	            }
	            for (final Tooltip tooltip : tooltipMappings.keySet()) {
	                if (tooltip.checkCollision(x, y)) {
	                	hoveredTooltip = tooltip;
	                	if (!hover) {
	                    	Sound.HOVER.play();
	                	}
	                	myChart.repaint();
	                	hover = true;
	                	hit = true;
	                	break;
	                }
	            }
	            if (!hit) {
	            	hover = false;
	            	if (hoveredTooltip == null)
	            		myChart.repaint();
	            }
        	}
            public void mouseDragged(MouseEvent evt) {
                int x1 = evt.getXOnScreen() - x;
                int y1 = evt.getYOnScreen() - y;
                setLocation(x1, y1);
                MainDriver.saveWindowPosition(x1, y1);
	            //System.out.println("Move: "+x1+", "+y1);

            }
        });

        setupFrame();
        pack();
        
        setLayout(myLayout);
    }
    private void setupFrame() {

        final Container container = getContentPane();

        myLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, myChart, 0, 
                               SpringLayout.HORIZONTAL_CENTER,
                               container);
        myLayout.putConstraint(SpringLayout.VERTICAL_CENTER, myChart, 0,
                             SpringLayout.VERTICAL_CENTER, container);
        add(myChart);
    }
    public void refresh() {
    	/*getContentPane().removeAll();
    	for (TrackPoint tp : MainDriver.data.keySet()) {
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
    		getContentPane().add(new JTextArea(text));
    	}*/
        setVisible(true);
    	revalidate();
    	repaint();
    }
    
    /**
     * An inner class to handle keyboard events.
     */
    private class KeyboardListener implements KeyListener {

        /**
         * Handles the pressing of a key.
         */
        @Override
        public void keyPressed(final KeyEvent theEvent) {
            final int key = theEvent.getKeyCode();
            if (key == KeyEvent.VK_PAGE_DOWN) {
            	MainDriver.reset();
            }
            if (key == KeyEvent.VK_P) {
            	MainDriver.pause();
            }
        }

        @Override
        public void keyReleased(final KeyEvent theEvent) {
        }

        @Override
        public void keyTyped(final KeyEvent theEvent) {
        }
        
    }
}
