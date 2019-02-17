package model;

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
	
	private double multiplier;
	
	private int total;
	
	public DeltaCollection(DataCollection data, double multiplier) {
		super();
		this.data = data;
		this.multiplier = multiplier;
		this.total = 0;
	}
	
	public double getMultiplier() {
		return multiplier;
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
			if (addData)
				total += (int)(multiplier * (secondLast - last));
			addData(total);
		}
	}
	public void handleHit(boolean hit) {
		handleHit(hit, multiplier);
	}
	public void reset() {
		super.reset();
		total = 0;
	}
}
