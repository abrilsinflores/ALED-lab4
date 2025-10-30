package es.upm.dit.aled.lab4.er;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import es.upm.dit.aled.lab4.er.gui.EmergencyRoomGUI;
import es.upm.dit.aled.lab4.er.gui.Position2D;

/**
 * Models a patient in a hospital ER. Each Patient is characterized by its
 * number (which must be unique), its current location and a protocol. The
 * protocol is a List of Transfers. Each Patient also has an index to indicate
 * at which point of the protocol they are at the current time.
 * 
 * Patients are Threads, and therefore must implement the run() method.
 * 
 * Each Patient is represented graphically by a dot of diameter 10 px, centered
 * in a given position and with a custom color.
 * 
 * @author rgarciacarmona
 */
public class Patient extends Thread {

	private int number;
	private List<Transfer> protocol;
	private int indexProtocol;
	private Area location; //área actual del paciente 
	private Position2D position;
	private Color color;

	/**
	 * Builds a new Patient.
	 * 
	 * @param numbre          The number of the Patient.
	 * @param initialLocation The initial location of the Patient.
	 */
	public Patient(int number, Area initialLocation) {
		this.number = number;
		this.protocol = new ArrayList<Transfer>();
		this.indexProtocol = 0;
		this.location = initialLocation;
		this.position = initialLocation.getPosition();
		this.color = Color.GRAY; // Default color
	}

	/**
	 * Returns the number of the Patient.
	 * 
	 * @return The number.
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * Returns the protocol of the Patient.
	 * 
	 * @return The protocol.
	 */
	public List<Transfer> getProtocol() {
		return protocol;
	}

	/**
	 * Returns the current location of the Patient.
	 * 
	 * @return The current location.
	 */
	public Area getLocation() {
		return location;
	}

	/**
	 * Changes the current location of the Patient.
	 * 
	 * @param location The new location.
	 */
	public void setLocation(Area location) {
		this.location = location;
	}

	/**
	 * Returns the position of the Patient in the GUI.
	 * 
	 * @return The position.
	 */
	public Position2D getPosition() {
		return position;
	}

	/**
	 * Changes the position of the Patient in the GUI.
	 * 
	 * @param position The new position.
	 */
	public void setPosition(Position2D position) {
		this.position = position;
	}

	/**
	 * Returns the color of Patient in the GUI.
	 * 
	 * @return The color.
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Changes the color of the Patient in the GUI.
	 * 
	 * @param color The new color.
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * Adds a new Transfer at the end of the Patient's protocol.
	 * 
	 * @param transfer The new Transfer.
	 */
	public void addToProtocol(Transfer transfer) {
		this.protocol.add(transfer);
	}

	/**
	 * Advances the Patient's protocol. The Patient is moved to the new Area, the
	 * movement is animated by the GUI and the index is increased by one.
	 */
	private void advanceProtocol() {
		// TODO
	private void advanceProtocol() {// TODO
		//para poder usar la interfaz gráfica
		Patient p = new Patient(this.number,this.location); //creo el paciente a mover
		Transfer movement = this.protocol.get(this.indexProtocol); // la transferencia d slaa a hacer
		EmergencyRoom er = new EmergencyRoom(); //creamos la er del paciente para poder dibujarla
		er.addPatient(p);
		er.addArea(this.location);
		//interfaz gráfica mueve al paciente
		EmergencyRoomGUI.initialize(er).animateTransfer(p, movement);
		//cambiar ubicación actual paciente
		this.location = movement.getTo();
		//avanzar en el protocolo
		this.indexProtocol +=1;
		
	}

	/**
	 * Simulates the treatment of the Patient at its current location. Therefore,
	 * the Patient must spend at this method the amount of time specified in such
	 * Area.
	 */
	private void attendedAtLocation() {// TODO
		int t = this.location.getTime(); //tiempo de espera del área actual del paciente
		try {
			sleep(t); //hacemos que el paciente espere -> sleep esta hebra
		} catch (InterruptedException e) {
			return;
		} 
	}

	/**
	 * Executes the Patient's behavior. It follows their protocol by being attended
	 * at the current location and then moving onto the next, until the last step of
	 * the protocol is reached. At that point, the Patient is removed from the GUI.
	 */
	@Override
	public void run() {
		// TODO
	public void run() {// TODO
		while(this.indexProtocol<this.protocol.size()) {//vuelta a atender al paciente hasta finalizar protocolo
			attendedAtLocation(); //atendemos paciente en la ubicación actual
			advanceProtocol(); //avanzamos al siguiente paso del protocolo
		}
		//eliminar al paciente
		Patient p = new Patient(this.number,this.location); //creo el paciente a mover
		EmergencyRoom er = new EmergencyRoom(); //creamos la er del paciente para poder dibujarla
		er.addPatient(p);
		er.addArea(this.location);
		EmergencyRoomGUI.initialize(er).removePatient(p); //lo elimino
	}

}