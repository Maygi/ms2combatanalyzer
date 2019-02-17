package gui;

import java.util.Objects;

/**
 * A class that represents tooltips.
 * @author May
 */
public class Tooltip {
	
	public static final int TEXT_SIZE = 16;
	private int x, y;
	private String title, text;
	
	public Tooltip(int x, int y, String title, String text) {
		this.x = x;
		this.y = y;
		this.title = title;
		this.text = text;
	}
	
	/**
	 * Returns the coordinates of the upper-left corner of the tooltip.
	 * @return The coordinates of the upper-left corner of the tooltip.
	 */
	public int[] getCoords() {
		return new int[] {x, y};
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getText() {
		return text;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Tooltip))
			return false;
		return (((Tooltip) other).getTitle().equalsIgnoreCase(title));
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(title, text);
	}
}
