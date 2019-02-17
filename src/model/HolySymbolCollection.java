package model;

import java.math.BigInteger;
import java.util.List;

/**
 * The calculation of estimated Holy Symbol contribution is so specific that it needs a
 * class on its own.
 * 
 * Holy Symbol estimated damage is taken by:
 * 1) Taking the total damage dealt during Holy Symbol
 * 2) Comparing it to the highest damage period of similar length, in the previous (scope 
 * multiplier * duration) of the Holy Symbol. If there is no previous period, it instead
 * uses the next period. 
 * 3) Subtracting the comparison "normal" damage from the HS damage.
 * @author May
 */
public class HolySymbolCollection extends DataCollection {
	
	/**
	 * The scope multiplier for finding a similar DPS window.
	 * A multiplier of 4 means that, with a 21-second Holy Symbol, the collection will
	 * attempt to search for the highest 21-second DPS window within 4*21 = 84 seconds
	 * of the Holy Symbol itself.
	 * Scope must be at least 2.
	 */
	private static final int SCOPE_MULTIPLIER = 4;
	
	private HitMissCollection hs;
	private DeltaCollection raidDamage;

	/**
	 * Standard constructor for a HolySymbolCollection.
	 * This is a DataCollection that estimates Holy Symbol's damage contribution.
	 */
	public HolySymbolCollection(HitMissCollection hs, DeltaCollection raidDamage) {
		super();
		this.hs = hs;
		this.raidDamage = raidDamage;
	}
	
	public void calculate() {
		List<Integer[]> endPoints = hs.getEndpoints();
		List<Integer> data = raidDamage.getData();
		BigInteger total = new BigInteger("0");
		if (endPoints.size() > 0) {
			for (int i = 0; i < endPoints.size(); i++) {
				Integer[] endPointSet = endPoints.get(i);
				BigInteger startDamage = new BigInteger(data.get(endPointSet[0]) + "");
				BigInteger endDamage = new BigInteger(data.get(endPointSet[1]) + "");
				BigInteger differential = endDamage.subtract(startDamage);
				BigInteger comparison = findComparison(endPointSet);
				System.out.println("Attempting to compare between: "+endPointSet[0]+", "+endPointSet[1]+". HS damage: "+endDamage.toString()+"-"+startDamage.toString());
				if (comparison == null) //not enough data
					return;
				BigInteger result = differential.subtract(comparison);
				BigInteger toAdd = differential.multiply(new BigInteger("19")).divide(new BigInteger("100")); 
				if (result.compareTo(toAdd) > 0) {
					total = total.add(result);
				} else {
					total = total.add(toAdd);
					System.out.println("Adding pity points: "+toAdd.toString());
				}
				System.out.println("HS Damage: "+differential+". Comparison Damage: "+comparison+".");
			}
		}
		addData(total.toString());
	}
	
	public BigInteger findComparison(Integer[] endPointSet) {
		int length = endPointSet[1] - endPointSet[0];
		int[] scope = new int[2];
		List<Integer> data = raidDamage.getData();
		if (endPointSet[0] - 1 - length * SCOPE_MULTIPLIER < 0) { //use some data from later on
			if (data.size() < endPointSet[1] + 1 + length * SCOPE_MULTIPLIER) { //not enough data
				return null;
			}
			scope[0] = endPointSet[1] + 1;
			scope[1] = endPointSet[1] + 1 + length * SCOPE_MULTIPLIER;
		} else {
			scope[0] = endPointSet[0] - length * SCOPE_MULTIPLIER;
			scope[1] = endPointSet[0] - 1;
		}
		//System.out.println("Scope: "+Arrays.toString(scope)+"; length of HS: "+length);
		BigInteger largest = new BigInteger("0");
		int start = 0;
		int end = 0;
		for (int i = scope[0]; i < scope[1] - length; i++) {
			BigInteger sum = new BigInteger((data.get(i + length) - data.get(i)) + "");
			if (sum.compareTo(largest) > 0) {
				largest = sum;
				start = i;
				end = i + length;
			}
		}
		System.out.println("Comparison point: "+largest.toString()+", from "+start+" to "+end);
		return largest;
	}
}
