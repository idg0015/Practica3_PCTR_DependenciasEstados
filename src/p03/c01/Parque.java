package src.p03.c01;

import java.util.Enumeration;
import java.util.Hashtable;

public class Parque implements IParque{


	private int max_personas = 50;
	private int contadorPersonasTotales;
	private Hashtable<String, Integer> contadoresPersonasPuerta;
	
	
	public Parque(int max_personas) {	
		contadorPersonasTotales = 0;
		contadoresPersonasPuerta = new Hashtable<String, Integer>();
		this.max_personas = max_personas;
	}


	@Override
	public synchronized void entrarAlParque(String puerta){
		
		// Si no hay entradas por esa puerta, inicializamos
		if (contadoresPersonasPuerta.get(puerta) == null){
			contadoresPersonasPuerta.put(puerta, 0);
		}
		
		//Comprobamos si se puede entrar
		try {
			this.comprobarAntesDeEntrar();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
				
		// Aumentamos el contador total y el individual
		contadorPersonasTotales++;		
		contadoresPersonasPuerta.put(puerta, contadoresPersonasPuerta.get(puerta)+1);
		
		// Imprimimos el estado del parque
		imprimirInfo(puerta, "Entrada");
		//Comprobamos los invariantes
		checkInvariante();
		//Notificamos a todos los hilos
		notifyAll();
	}
	
	@Override
	public synchronized void salirDelParque(String puerta) {
		// Si no hay salidas por esa puerta, inicializamos
		if (contadoresPersonasPuerta.get(puerta) == null){
			contadoresPersonasPuerta.put(puerta, 0);
		}
				
        //Comprobamos si se puede salir
		try {
			this.comprobarAntesDeSalir();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// Disminuimos el contador total y el individual
		contadorPersonasTotales--;		
		contadoresPersonasPuerta.put(puerta, contadoresPersonasPuerta.get(puerta)-1);
		
		// Imprimimos el estado del parque
		imprimirInfo(puerta, "Salida");
		
		//Comprobamos los invariantes
		checkInvariante();
		//Notificamos a todos los hilos
		notifyAll();		
	}
	
	
	private void imprimirInfo (String puerta, String movimiento){
		System.out.println(movimiento + " por puerta " + puerta);
		System.out.println("--> Personas en el parque " + contadorPersonasTotales); //+ " tiempo medio de estancia: "  + tmedio);
		
		// Iteramos por todas las puertas e imprimimos sus entradas
		for(String p: contadoresPersonasPuerta.keySet()){
			System.out.println("----> Por puerta " + p + " " + contadoresPersonasPuerta.get(p));
		}
		System.out.println(" ");
	}
	
	private int sumarContadoresPuerta() {
		int sumaContadoresPuerta = 0;
			Enumeration<Integer> iterPuertas = contadoresPersonasPuerta.elements();
			while (iterPuertas.hasMoreElements()) {
				sumaContadoresPuerta += iterPuertas.nextElement();
			}
		return sumaContadoresPuerta;
	}
	
	protected void checkInvariante() {
		assert sumarContadoresPuerta() == contadorPersonasTotales : "INV: La suma de contadores de las puertas debe ser igual al valor del contador del parte";
		assert contadorPersonasTotales <= max_personas: "INV: Aforo superado";
		assert contadorPersonasTotales >= 0: "INV: No se puede salir del parque si esta vacio"; 
	}

	protected void comprobarAntesDeEntrar() throws InterruptedException{
		while(contadorPersonasTotales == max_personas) {
			wait();
		}
	}

	protected void comprobarAntesDeSalir() throws InterruptedException{		
		while(contadorPersonasTotales == 0) {
			wait();
		}
	}

}
