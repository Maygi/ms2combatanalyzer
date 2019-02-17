package model;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a class that collects and stores data.
 * @author May
 */
public class DataCollection {
	protected List<Integer> data;
	protected List<String> stringData;
	
	/**
	 * Standard constructor for a DataCollection.
	 * Most DataCollections will use integers; some will use strings.
	 */
	public DataCollection() {
		data = new ArrayList<Integer>();
		stringData = new ArrayList<String>();
	}
	
	public void equalize(int length) {
		if (data.size() > 0) {
			while (data.size() < length) {
				data.add(0, data.get(0));
			}
		}
		if (stringData.size() > 0) {
			while (stringData.size() < length) {
				stringData.add(0, stringData.get(0));
			}
		}
	}
	
	public void addData(String value) {
		stringData.add(value);
	}
	
	public void addData(int value) {
		data.add(value);
	}

	public String getLastString() {
		if (stringData.size() > 0)
			return stringData.get(stringData.size() - 1);
		return null;
	}
	public List<String> getStringData() {
		return stringData;
	}
	
	public List<Integer> getData() {
		return data;
	}
	public Integer getLast() {
		if (data.size() > 0)
			return data.get(data.size() - 1);
		return 0;
	}
	
	public void reset() {
		data = new ArrayList<Integer>();
	}
}
