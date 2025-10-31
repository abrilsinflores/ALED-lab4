package es.upm.dit.aled.lab4.er;

import java.util.Objects;

/**
 * Models an transfer (the movement of a patient to a new area) in a hospital
 * ER. Each Transfer is characterized by the destination Area and the time
 * needed to perform that movement (in milliseconds).
 * 
 * @author rgarciacarmona
 */
public class Transfer {

	private Area to; //área hacia la que se mueve el paciente
	private int time;

	/**
	 * Builds a new Transfer.
	 * 
	 * @param to	The destination Area.
	 * @param time	The time needed to reach the destination Area (in milliseconds)
	 */
	public Transfer(Area to, int time) {
		this.to = to;
		this.time = time;
	}

	/**
	 * Returns the destination Area.
	 * 
	 * @return The destination.
	 */
	public Area getTo() {
		return to;
	}

	/**
	 * Returns the time needed to perform the Transfer.
	 * 
	 * @return The time.
	 */
	public int getTime() {
		return time;
	}

	@Override
	public int hashCode() {
		return Objects.hash(time, to);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Transfer other = (Transfer) obj;
		return Double.doubleToLongBits(time) == Double.doubleToLongBits(other.time) && Objects.equals(to, other.to);
	}

	@Override
	public String toString() {
		return "Transfer to " + to;
	}
}