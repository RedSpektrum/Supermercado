/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pecl3;

import java.io.File;
import java.io.FileWriter;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nacho
 */
public class Log extends Thread{
    private LinkedBlockingQueue<String> cola = new LinkedBlockingQueue(); //Cola que contiene el texto a escribir
    private Semaphore semaforo = new Semaphore(0);                        //Control de las peticiones de escritura
    private File archivo;
    
    /*****************************************
    * Constructor de la clase                *
    *****************************************/
    public Log (){ 
        archivo = new File("texto.txt");
        anadirACola("INICIO"); //Marcamos el inicio con esto
    }
    
    /**
     * Método que mantiene el hilo Log en espera hasta recibir peticiones de escritura en el archivo de texto
     */
    @Override
    public void run(){
        long inicio = System.currentTimeMillis();
        //Bucle infinito que mantiene a la espera de peticiones al hilo Log
         while(true){
            //Referencia temporal del inicio del programa  
            try {
                    //Espera petición para escribir
                    semaforo.acquire(); 
                    //Toma el tiempo
                    float tiempo = (float) (System.currentTimeMillis()-inicio);
                    //Escribe en el fichero
                    escribirTexto("Tiempo = "+(tiempo/1000) + " s \n"+ cola.element() + "\n-------------------------------------------------------------\n");   
                    //Elimina el elemento de la cola
                    cola.remove();
                    
            } catch (InterruptedException ex) {
                Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
            }
         }               
    }
    
    /**
     * Escribe texto en el archivo texto.txt
     * @param texto 
     */
    public void escribirTexto(String texto){
        try{
            //Crea el escritor de archivo
            FileWriter escribir=new FileWriter(archivo,true);
            //Escribe
            escribir.write(texto);
            //Cierra el archivo
            escribir.close();
        }catch(Exception e)
         {System.out.println("Error al escribir");}   
    }

    
    /**
     * Método al que se llama desde supermercado para generar peticiones de escritura
     * @param texto 
     */
    public void anadirACola(String texto){
            
        //Se encola el texto
        cola.offer(texto);
        //Se añade un permiso para que el hilo Log escriba
        semaforo.release();
    }
    
    
}
