package es.upm.dit.aled.lab4.er;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import es.upm.dit.aled.lab4.er.gui.Position2D;

/**
 * Models a hospital ER. It is characterized by a colletion of patients and the
 * areas that these patients will be treated in. Patients extends the class Thread.
 * 
 * @author rgarciacarmona
 */
public class EmergencyRoom {

	private Map<String, Area> areas;
	private Map<Integer, Patient> patients;

	/**
	 * Builds a new EmergencyRoom
	 */
	public EmergencyRoom() {
		this.areas = new HashMap<String, Area>();
		this.patients = new HashMap<Integer, Patient>();
	}

	/**
	 * Adds a new Area to the EmergencyRoom
	 * 
	 * @param area The new Area.
	 */
	public void addArea(Area area) {
		this.areas.put(area.getName(), area);
	}

	/**
	 * Returns the Area that corresponds to the given name.
	 * 
	 * @param name The name of the Area.
	 * @return     The Area.
	 */
	public Area getArea(String name) {
		return areas.get(name);
	}

	/**
	 * Returns a List with all the Areas of the EmergencyRoom.
	 * 
	 * @return The List.
	 */
	public List<Area> getAreas() {
		Collection<Area> areas = this.areas.values();
		return new ArrayList<Area>(areas);
	}

	/**
	 * Adds a new Patient to the EmergencyRoom
	 * 
	 * @param patient The new Patient.
	 */
	public void addPatient(Patient patient) {
		this.patients.put(patient.getNumber(), patient);
	}

	/**
	 * Returns a List with all the Patients of the EmergencyRoom.
	 * 
	 * @return The List.
	 */
	public List<Patient> getPatients() {
		Collection<Patient> patients = this.patients.values();
		return new ArrayList<Patient>(patients);
	}

	/**
	 * Populates the EmergencyRoom with the data contained in a text file.
	 * 
	 * @param in The path of the file.
	 */
	public void readFile(Scanner in) {
		Random rand = new Random(13); // For assigning random colors

		while (in.hasNext()) {
			String tipo = in.next();
			if (tipo.equals("AREA")) {
				int x = in.nextInt();
				int y = in.nextInt();
				int time = in.nextInt();
				String name = in.next().trim();
				Area area = new Area(name, time, new Position2D(x, y));
				// Assign a random-ish color for visualization
				area.setColor(Color.getHSBColor(rand.nextFloat(), 0.3f, 0.9f));
				areas.put(name, area);
			} else if (tipo.equals("PATIENT")) {
				int id = in.nextInt();
				Area initial = areas.get(in.next());
				Patient patient = new Patient(id, initial);
				// Assign a random-ish color for visualization
				patient.setColor(Color.getHSBColor(rand.nextFloat(), 1.0f, 0.8f));
				patients.put(id, patient);
			} else if (tipo.equals("TRANSFER")) {
				int patientId = in.nextInt();
				String areaName = in.next();
				int time = in.nextInt();
				Transfer transfer = new Transfer(areas.get(areaName), time);
				patients.get(patientId).addToProtocol(transfer);
			} else {
				System.err.println("Error in input file. Unknown type: " + tipo);
			}
		}
		in.close();
	}

	/**
	 * Starts the protocol of a Patient.
	 * 
	 * @param patient The Patient.
	 */
	public void admit(Patient patient) {
		patient.start();
		System.out.println("Patient " + patient.getNumber() + " has been admitted.");

	}

	/**
	 * Waits until the Patient has finished their protocol.
	 * 
	 * @param patient The Patient.
	 */
	public void waitForDischarge(Patient patient) { 
		try {
			patient.join();
			System.out.println("Patient " + patient.getNumber() + " has been discharged.");

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}