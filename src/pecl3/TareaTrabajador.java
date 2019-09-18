/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pecl3;

import static java.lang.Thread.sleep;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author javir
 */
public class TareaTrabajador extends Thread{
    private int tiempominimo;   //Tiempo de procesamiento minimo
    private int tiempomaximo;   //Tiempo de procesamiento maximo
    private String nombre;     //Nombre del trabajador
    private CyclicBarrier barrera;
    private Semaphore pausa;
    
    /*****************************************
    * Constructor de la clase                *
    *****************************************/
    public TareaTrabajador(int tiempominimo, int tiempomaximo,String nombre, CyclicBarrier barrera){
        this.tiempomaximo = tiempomaximo;
        this.tiempominimo = tiempominimo;
        this.barrera = barrera;
        this.nombre=nombre;
        pausa=new Semaphore(0);
    }

    /***********************
    * Lanzamiento del hilo *
    ***********************/
     public void run()
    {
        //Se repite la cantidad de mensajes que vaya a leer cada uno
        while(true)
        {

            //Calculo del tiempo
            float tiempo=0;


            tiempo=(int)(Math.random()*(tiempomaximo-tiempominimo)) + (tiempominimo);
            try
            {
                //Tiempo que tarda en tomar mensajes
                sleep((int)(tiempo));
                barrera.await();
            } 

            catch (InterruptedException ex) 
            { 
                
            }
            catch(BrokenBarrierException e){}
           
        }
        
    }
    
     /**
      * Devuelve el nombre del trabajador
      * @return 
      */
    public String getNombre(){return nombre;}


}