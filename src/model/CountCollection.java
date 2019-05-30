package model;

import java.math.BigInteger;

/**
 * A DataCollection that counts up every time a certain image is found.
 * There is a cooldown between how often it can count.
 * @author May
 */
public class CountCollection extends DataCollection {
	private int hits;
	private int countDelay;
	private long lastCount = 0;
	
	/**
	 * Standard constructor for a CountCollection.
	 */
	public CountCollection(int delay) {
		super();
		hits = 0;
		countDelay = delay;
	}
	
	public void handleHit(boolean hit) {
		if (!MainDriver.active)
			return;
		if (hit) {
			final long time = System.currentTimeMillis();
			if (Math.abs(time - lastCount) / 1000 > countDelay) {
				lastCount = time; 
				hits++;
			}
		}
		addData(new BigInteger(Integer.toString(hits)));
	}

	@Override
	public void reset() {
		super.reset();
		hits = 0;
	}
}
