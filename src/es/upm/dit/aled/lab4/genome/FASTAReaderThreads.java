package es.upm.dit.aled.lab4.genome;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Reads a FASTA file containing genetic information and allows for the search
 * of specific patterns within these data. The information is stored as an array
 * of bytes that contain nucleotides in the FASTA format. Since this array is
 * usually created before knowing how many characters in the origin FASTA file
 * are valid, an int indicating how many bytes of the array are valid is also
 * stored. All valid characters will be at the beginning of the array.
 * 
 * @author mmiguel, rgarciacarmona
 *
 */
public class FASTAReaderThreads {

	// All threads can access the same content and valid bytes since they are never
	// modified after the file is loaded.
	protected byte[] content;
	protected int validBytes;

	/**
	 * Creates a new FASTAReader from a FASTA file.
	 * 
	 * @param fileName The name of the FASTA file.
	 */
	public FASTAReaderThreads(String fileName) {
		try {
			this.readFile(fileName);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return;
		}
	}

	/*
	 * Helper method to read from a file. It populates the data array with upper
	 * case version of all the nucleotids found in the file. Throws an IOException
	 * if there is a problem accessing the file or the file is to big to fit in an
	 * array.
	 */
	private void readFile(String fileName) throws IOException {
		File f = new File(fileName);
		FileInputStream fis = new FileInputStream(f);
		DataInput fid = new DataInputStream(fis);
		long len = (int) fis.getChannel().size();
		if (len > Integer.MAX_VALUE) {
			fis.close();
			throw new IOException("The file " + fileName + " is too big. Can't be contained in an array.");
		}
		byte[] content = new byte[(int) len];
		int bytesRead = 0;
		int numRead = 0;
		String line;
		while ((line = fid.readLine()) != null) {
			// Put every character in upper case
			line = line.toUpperCase();
			numRead = line.length();
			byte[] newData = line.getBytes();
			for (int i = 0; i < numRead; i++)
				content[bytesRead + i] = newData[i];
			bytesRead += numRead;
		}
		fis.close();
		this.content = content;
		this.validBytes = bytesRead;
	}

	/**
	 * Provides the data array that contains the complete sequence of nucleotids
	 * extracted from the FASTA file.
	 * 
	 * @return The data array with each nucleotid taking one byte.
	 */
	public byte[] getContent() {
		return content;
	}

	/**
	 * Provides the amount of bytes in the data array that are valid. Since this
	 * array is created before the amount of bytes in the FASTA file that contain
	 * actual nucleotids are know, a worst-case scenario is assumed. So, only
	 * positions between content[0] and content[validBytes -1] have actual genomic
	 * data.
	 * 
	 * @return The number of valid bytes.
	 */
	public int getValidBytes() {
		return validBytes;
	}

	/**
	 * Performs a linear search to look for the provided pattern in the data array.
	 * To do this, it creates a thread pool with as many threads as cores has the
	 * computer this code is running on. Each of these threads will perform a linear
	 * search on the same byte[] (content), but only in a space 1/(number of cores)
	 * the size of the genome, so the work is split equally between all threads.
	 * When all threads have finished, it aggregates the results. Returns a List of
	 * Integers that point to the initial positions of all the occurrences of the
	 * pattern in the data.
	 * 
	 * @param pattern The pattern to be found.
	 * @return All the positions of the first character of every occurrence of the
	 *         pattern in the data.
	 */
	public List<Integer> search(byte[] pattern) {// TODO
		////lista con las pos en las q se ha encontrado el patrón
		List<Integer> posOfConcurrences = new ArrayList<Integer>(); 
		//nº procesadores q tiene el ordenador
		int cores = Runtime.getRuntime().availableProcessors();
		//longitud de cada sección del genoma máxima en la que podemos dividir el genoma
		int lsection = this.content.length/cores;
		int lo = 0;
		int hi = 0;
				
		//para lanzar y gestionar los Threads (schedule them)
		ExecutorService executor = Executors.newFixedThreadPool(cores);
		//creamos una lista de resultados futuros para q podamos dsps obtener los resultados
		//d todas las tareas FUERA del for y así concurran varios threads a la vez
		List<Future<List<Integer>>> resultadosFuturos = new ArrayList<>();
		
		for(int i = 0; i<=cores; i++) {
			//para cada tarea los índices de cada sección del genoma cambian 
			//teniendo en cuenta q el último trozo es más pequeño
			if(i<cores) {
				lo = i*lsection;
				hi = lo+lsection;
			}
			if(i==cores) {
				lo = i*lsection;
				hi = this.validBytes;
			}
			//tarea a ejecutar
			Callable<List<Integer>> tarea = new FASTASearchCallable(this,lo,hi,pattern);
			//enviamos tareas al servicio de ejecución
			Future<List<Integer>> resultadoFuturo = executor.submit(tarea);
			//y las guardamos en nuestra lista d resultados
			resultadosFuturos.add(resultadoFuturo);
		}
			//obtenemos el resultado de la tarea d cada hebra CÓDIGO SE BLOQUEA
		for(Future<List<Integer>> resultado : resultadosFuturos) {
			//añadimos la lista d la sección a la lista general 
			try {
				posOfConcurrences.addAll(resultado.get());
			} catch (Exception e) {
				return null;
			}
		}
			//cerramos el ExecutorService
			executor.shutdown();
	
		return posOfConcurrences;
	}

	public static void main(String[] args) {
		long t1 = System.nanoTime();
		FASTAReaderThreads reader = new FASTAReaderThreads(args[0]);
		if (args.length == 1)
			return;
		System.out.println("Tiempo de apertura de fichero: " + (System.nanoTime() - t1));
		long t2 = System.nanoTime();
		List<Integer> posiciones = reader.search(args[1].getBytes());
		System.out.println("Tiempo de búsqueda: " + (System.nanoTime() - t2));
		if (posiciones.size() > 0) {
			for (Integer pos : posiciones)
				System.out.println("Encontrado " + args[1] + " en " + pos);
		} else
			System.out.println("No he encontrado : " + args[1] + " en ningun sitio");
		System.out.println("Tiempo total: " + (System.nanoTime() - t1));
	}
}
