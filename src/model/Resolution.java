package model;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A class for handling methods related to finding screen regions given different resolutions.
 * @author May
 *
 */
public class Resolution {
	
	/**
	 * The indexes of certain screen areas.
	 */
	public static final int BOSS_DEBUFFS = 0;
	public static final int BOSS_BUFFS = 1;
	public static final int BUFFS = 2;
	public static final int DEBUFFS = 3;
	public static final int BOSS_HEALTH = 4;
	public static final int BOSS_TAG = 5;
	public static final int SPIRIT = 6;
	public static final int TOMBSTONE = 7;
	public static final int DUNGEON_CLEAR = 8;
	
	private Dimension screenSize;
	
	private List<Integer[]> regions;
	private boolean match = false;
	
	public Resolution(Dimension screenSize) {
		this.screenSize = screenSize;
		initialize();
	}
	
	/**
	 * Derives a new set of regions based on a list of points.
	 * @param points
	 * @param sizes
	 * @return
	 */
	@SuppressWarnings("unused")
	private List<Integer[]> derive(List<Integer[]> points) {
		List<Integer[]> toReturn = new ArrayList<Integer[]>();
		for (int i = 0; i < points.size(); i++) {
			Integer[] point = points.get(i);
			Integer[] region = new Integer[4];
			region[0] = point[0];
			region[1] = point[1];
			region[2] = point[0] + sizes.get(i)[0];
			region[3] = point[1] + sizes.get(i)[1];
			toReturn.add(region);
		}
		return toReturn;
	}
	
	private List<Integer[]> baseRegion, sizes;
	
	private void initialize() {
		//configure 1920x1080 sizes. region sizes should be similar
		
		baseRegion = new ArrayList<Integer[]>();
		baseRegion.add(new Integer[]{(int) (screenSize.getWidth() / 2 - 390), 125,
				(int) (screenSize.getWidth() / 2 + 390), 220});
		baseRegion.add(new Integer[]{(int) (screenSize.getWidth() / 2 - 400), 0,
				(int) (screenSize.getWidth() / 2), 55});
		baseRegion.add(new Integer[]{(int) (screenSize.getWidth() / 2 - 410), (int) (screenSize.getHeight() - 300),
				(int) (screenSize.getWidth() / 2 - 60), (int) (screenSize.getHeight() - 195)});
		baseRegion.add(new Integer[]{(int) (screenSize.getWidth() / 2 + 60), (int) (screenSize.getHeight() - 300),
				(int) (screenSize.getWidth() / 2 + 360), (int) (screenSize.getHeight() - 195)});
		baseRegion.add(new Integer[]{(int) (screenSize.getWidth() / 2 + 140), 76,
				(int) (screenSize.getWidth() / 2 + 140 + 198), 76 + 29});
		baseRegion.add(new Integer[]{(int) (screenSize.getWidth() / 2 - 315), 77,
				(int) (screenSize.getWidth() / 2 - 275), 102});
		baseRegion.add(new Integer[]{(int) (screenSize.getWidth() / 2 - 10), (int) (screenSize.getHeight() - 255),
				(int) (screenSize.getWidth() / 2 + 50), (int) (screenSize.getHeight() - 185)});
		
		//baseRegion.add(new Integer[]{640, 130, 1330, 240});		
		//baseRegion.add(new Integer[]{630, 0, 730, 55});
		//baseRegion.add(new Integer[]{550, 740, 890, 890});
		//baseRegion.add(new Integer[]{1000, 750, 1300, 900});
		//baseRegion.add(new Integer[]{1099, 76, 1099 + 198, 76 + 29});
		//baseRegion.add(new Integer[]{643, 77, 685, 102});
		//baseRegion.add(new Integer[]{950, 840, 1050, 1030});
		
		//...just now realized that this could probably be a better way of handling things. might normalize the above later
		baseRegion.add(new Integer[]{(int) (screenSize.getWidth() / 2 - 75), (int) (screenSize.getHeight() - 135),
				(int) (screenSize.getWidth() / 2), (int) (screenSize.getHeight() - 100)});
		/*baseRegion.add(new Integer[]{(int) (screenSize.getWidth() / 2 - 40), 0,
									(int) (screenSize.getWidth() / 2 + 40), (int) (screenSize.getHeight() / 2)});*/
		baseRegion.add(new Integer[]{(int) (screenSize.getWidth() / 2 - 300), (int) (screenSize.getHeight() / 8), 
									(int) (screenSize.getWidth() / 2 + 300), (int) (screenSize.getHeight() / 2)});
		
		
		sizes = new ArrayList<Integer[]>();
		for (int i = 0; i < baseRegion.size(); i++) {
			Integer[] newSize = new Integer[2];
			newSize[0] = baseRegion.get(i)[2] - baseRegion.get(i)[0];
			newSize[1] = baseRegion.get(i)[3] - baseRegion.get(i)[1];
			sizes.add(newSize);
		}
		
		regions = new ArrayList<Integer[]>(); 
		match = true;
		regions = baseRegion;
		/*if (screenSize.getWidth() == 1920 && screenSize.getHeight() == 1080) {
			match = true;
			regions = baseRegion;
		} else if (screenSize.getWidth() == 3440 && screenSize.getHeight() == 1440) {
			match = true;
			List<Integer[]> points = new ArrayList<Integer[]>();
			points.add(new Integer[]{1390, 120});
			points.add(new Integer[]{1370, 10});
			points.add(new Integer[]{1300, 1100});
			points.add(new Integer[]{1770, 1100});
			points.add(new Integer[]{1860, 76});
			points.add(new Integer[]{1400, 77});
			points.add(new Integer[]{1710, 1200});
			regions = derive(points);
			regions.add(baseRegion.get(TOMBSTONE));
			regions.add(baseRegion.get(DUNGEON_CLEAR));
		} else if (screenSize.getWidth() == 2560 && screenSize.getHeight() == 1440) {
			match = true;
			List<Integer[]> points = new ArrayList<Integer[]>();
			points.add(new Integer[]{960, 125});
			points.add(new Integer[]{960, 10});
			points.add(new Integer[]{875, 1140});
			points.add(new Integer[]{875 + 470, 1100});
			points.add(new Integer[]{1418, 76});
			points.add(new Integer[]{966, 78});
			points.add(new Integer[]{1277, 1200});
			regions = derive(points);
			regions.add(baseRegion.get(TOMBSTONE));
			regions.add(baseRegion.get(DUNGEON_CLEAR));
		} else { //1920x1080
			match = false;
			regions = baseRegion;
		}*/
	}
	
	/**
	 * Returns whether a valid configuration is found for the current resolution.
	 * If no resolution match is found, the regions will be scaled based on the 1920x1080 regions.
	 * @return
	 */
	public boolean hasMatch() {
		return match;
	}
	
	public Integer[] getRegion(int index) {
		return regions.get(index);
	}
}
