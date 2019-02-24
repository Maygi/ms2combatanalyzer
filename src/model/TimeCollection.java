package model;

import java.util.ArrayList;
import java.util.List;

import model.MainDriver.TrackPoint;

/**
 * A DataCollection for calculating the current time, as well as the estimated clear time.
 * @author May
 *
 */
public class TimeCollection extends DataCollection {
	protected List<Long> data;
	
	/**
	 * The max raid time in seconds.
	 */
	private static final int MAX_RAID_TIME = 900;
	
	private static final int HS_CD = 180;
	
	private int lastReading = 0;
	
	public TimeCollection() {
		this.data = new ArrayList<Long>();
	}
	
	/**
	 * Returns the time (in seconds) between the two indices.
	 * @param start The starting index.
	 * @param end The ending index.
	 * @return The time between the two indices in seconds. 0 if the indices are invalid.
	 */
	public int getTime(int start, int end) {
		if (start < 0 || end >= data.size())
			return 0;
		return (int)((data.get(end) - data.get(start)) / 1000);
	}
	
	/**
	 * Returns the time in milliseconds at the current index.
	 * @param index The index to return from.
	 * @return The time in milliseconds.
	 */
	public Long getTime(int index) {
		return data.get(index);
	}
	
	/**
	 * Returns a formatting string for the estimated time.
	 * @return The estimated time as a string, or --- if not enough data exists.
	 */
	public String getEstimate() {
		int time = estimateTime();
		if (time > 0)
			return MainDriver.timeToString(time);
		return "---";
	}
	
	/**
	 * Estimates the clear time of the fight.
	 * @return The estimated clear time of the fight in seconds; 0 if data is missing or insufficient.
	 */
	public int estimateTime() {
		try {
			DPSCollection dps = ((DPSCollection)MainDriver.data.get(TrackPoint.HOLY_SYMBOL_DAMAGE));
			int hsDuration = dps.getAverageHSTime();
			int lastHS = MainDriver.getEllaspedTime(getTime(dps.getLastHSTime())); 
			int hsDPS = dps.getHSDPS();
			int avgDPS = dps.getAverageDPS();
			int hp = MainDriver.data.get(TrackPoint.HP).getLast();
			if (hp <= 1)
				return lastReading;
			if (avgDPS == 0) //not enough data
				return 0;
			int rough = MainDriver.getEllaspedTime() + (hp / avgDPS);
			int calcTime = Math.min(rough, MAX_RAID_TIME);
			int hsCastsLeft = (calcTime - lastHS) / HS_CD;
			double hsWeight = ((double)hsDuration * hsCastsLeft) / (double)rough;
			int avgRealDPS = (int)((double)(avgDPS * (1.0 - hsWeight)) + (double)hsDPS * hsWeight);
			int real = MainDriver.getEllaspedTime() + (hp / avgRealDPS);
			System.out.println("HS Duration: "+hsDuration+"; HS DPS: "+hsDPS+"; AVG DPS: "+avgDPS+"; HP: "+hp+"; Real DPS: "+avgRealDPS+"; HS casts left: "+hsCastsLeft);
			lastReading = real;
			return real;
		} catch (Exception e) {
			return 0;
		}
	}
	
	public void addData(long time) {
		data.add(time);
	}
	
	@Override
	public void reset() {
		data = new ArrayList<Long>();
	}

}
