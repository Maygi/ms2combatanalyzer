package model;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This is a DataCollection that counts up a portion (multiplier) of data based on the
 * change, per tick, of a parallel collection.
 * @author May
 */
public class DeltaCollection extends DataCollection {
	/**
	 * The data collection to mirror
	 */
	private DataCollection data;
	
	private List<Integer> dataOverTime;
	
	/**
	 * Data accumulates. Deltas give us the value per tick.
	 */
	private List<Integer> deltas;
	
	/**
	 * These delta arrays return the average of the last X deltas, based on the delta constant.
	 */
	private List<Integer> softDeltas;
	
	private static final int DELTA_CONSTANT = 5;
	
	private double multiplier;
	
	private int total;
	
	/**
	 * The amount of "inaccurate" readings that occured.
	 * When an inaccurate reading occurs, the soft delta (used by the graph) is not updated immediately.
	 * Breaks are counted up towards the next accurate reading, and then the difference is
	 * evenly distributed throughout the breakpoints.
	 */
	private int breaks;
	
	public DeltaCollection(DataCollection data, double multiplier) {
		super();
		this.data = data;
		this.multiplier = multiplier;
		this.total = 0;
		this.breaks = 0;
		this.dataOverTime = new ArrayList<Integer>();
		this.deltas = new ArrayList<Integer>();
		this.softDeltas = new ArrayList<Integer>();
	}
	
	public double getMultiplier() {
		return multiplier;
	}
	
	/**
	 * Returns the last (valid) delta behind the given index.
	 * @return
	 */
	public int getLastDelta(int index) {
		if (index > data.getData().size())
			return 0;
		List<Integer> realData = data.getData();
		int last = -1;
		int indexToRead = index;
		int reference = 0;
		while (indexToRead > 0) { //find the last successful reading
			indexToRead--;
			int value = realData.get(index);
			if (value < 0) //if failed to read
				continue;
			else {
				reference = value;
				break;
			}
		}
		return realData.get(index) - reference;
	}

	
	public List<Integer> getDataOverTime() {
		return dataOverTime;
	}
	
	public List<Integer> getDeltas() {
		return deltas;
	}
	
	public List<Integer> getSoftDeltas() {
		return softDeltas;
	}
	
	/**
	 * Must be called before updateSoftDelta
	 * @param addData Whether or not the reading was valid.
	 * @param value The value to add
	 */
	public void updateDelta(boolean addData, int value) {
		if (!addData) {
			breaks++;
			return;
		}
		if (breaks > 0) {
			value /= breaks;
			for (int i = 0; i < breaks; i++) {
				deltas.add(value);
			}
		} else {
			deltas.add(value);
		}
	}
	
	/**
	 * Updates the soft delta, calling itself recursively to update multiple times if breaks occurred.
	 * @param toAdd Whether or not the reading was valid
	 */
	public void updateSoftDelta(boolean toAdd) {
		if (!toAdd) {
			return;
		}
		int total = 0;
		int start = deltas.size() - 1;
		int end = Math.max(0,  deltas.size() - 1 - DELTA_CONSTANT);
		int count = 0;
		for (int i = start; i >= end; i--) {
			total += deltas.get(i);
			count++;
		}
		if (count == 0)
			softDeltas.add(deltas.get(deltas.size() - 1));
		else {
			total /= count;
			softDeltas.add(total);
		}
		if (breaks > 0) {
			breaks--;
			updateSoftDelta(toAdd);
		}
	}

	public void handleHit(boolean hit, double multiplier) {
		if (!MainDriver.active)
			return;
		if (hit) {
			boolean addData = true;
			List<Integer> realData = data.getData();
			//System.out.println("HIT! Data: "+realData.toString());
			int last = -1, secondLast = -1;
			int index = realData.size() - 2;
			if (realData.size() < 2)
				addData = false;
			else {
				last = realData.get(realData.size() - 1);
				secondLast = realData.get(index);
			}
			if (last < 0 || secondLast < 0) //if failed to read either
				addData = false;
			if (secondLast - last < 0) //it healed - can't count it
				addData = false;
			if (secondLast - last > last) //probably counted an extra digit...
				addData = false;
			while (!addData && index > 0 && last > 0) { //find the last successful reading
				index--;
				secondLast = realData.get(index);
				if (last < 0 || secondLast < 0) //if failed to read either
					addData = false;
				else if (secondLast - last < 0) //it healed - can't count it
					addData = false;
				else if (secondLast - last > last) //probably counted an extra digit...
					addData = false;
				else
					addData = true;
			}
			int toAdd = (int)(multiplier * (secondLast - last));
			if (addData) {
				total += toAdd;
			}/* else {
				deltas.add(deltas.size() == 0 ? 0 : deltas.get(deltas.size() - 1));
			}*/
			updateDelta(addData, toAdd);
			updateSoftDelta(addData);
			addData(total);
			if (MainDriver.getEllaspedTime() > 0)
				dataOverTime.add(total / MainDriver.getEllaspedTime());
		}
	}
	public void handleHit(boolean hit) {
		handleHit(hit, multiplier);
	}
	@Override
	public void reset() {
		super.reset();
		total = 0;
		dataOverTime = new ArrayList<Integer>();
		deltas = new ArrayList<Integer>();
		softDeltas = new ArrayList<Integer>();
	}
}
