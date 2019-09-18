/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pecl3;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Pool de hilos a lanzar
 * En el constructor se decide el tipo de servicio
 * 
 * @author Nacho
 */
public abstract class Zona {
    
    private int nTrabajadores;  //Cantidad de hilos
    protected ExecutorService ex;        //Executor de hilos
    private int servicio;       //Servicio de executor
    protected TareaTrabajador trabajador;
    private int t_minimo; //tiempo minimo para atender a un cliente
    private int t_maximo; //tiempo maximo para atender a un cliente
    private String nombre; //Nombre del hilo
    private CyclicBarrier espera = new CyclicBarrier(2);
    private CyclicBarrier espera2 = new CyclicBarrier(2);
    private AtomicInteger tiempo;
    
    /*****************************************
    * Constructor de la clase                *
    *****************************************/
    public Zona(int ntrab, int servicio, int t_min, int t_max, String nombre)
    {
        this.nTrabajadores = ntrab;
        this.servicio=servicio;
        t_minimo=t_min;
        t_maximo=t_max;
        this.nombre = nombre;
        tiempo = new AtomicInteger(0);
  
        //Si servicio es 0 es FixedThreadPool
        if(servicio==0)
        {
            ex = Executors.newFixedThreadPool(nTrabajadores);
        }
        //Si servicio es 1 es SingleThreadExecutor
        else if(servicio == 1)
        {
            ex = Executors.newSingleThreadExecutor();
        }
        //Si servicio es 2 es ScheduledThreadPool
        else if(servicio == 2)
        {
            ex = Executors.newScheduledThreadPool(nTrabajadores);
        }
        //Si servicio es 3 es Catched
        else if (servicio==3)
        {
            ex = Executors.newCachedThreadPool();
        }
    }
    
    /**
     * Lanza el trabajador numero 1 o el 2 dependiendo del argumento 
     * @param i
     * @return 
     */
    public String lanzarTrabajador(int i){
        if(i==1){              
                trabajador = new TareaTrabajador(t_minimo, t_maximo,nombre, espera);
            }
        else if(i==2){
                trabajador = new TareaTrabajador(t_minimo, t_maximo, nombre, espera2 );
            }
        else{
                
        
        }
        ex.execute(trabajador);
        return trabajador.getNombre();
    }

    /**
     * Genera una espera por parte de la ejecución al final del trabajo del hilo trabajador
     * @param i 
     */
    protected int esperar(int i) {
        int tiempo = (int)System.currentTimeMillis();
        CyclicBarrier barrera = new CyclicBarrier(2);
        try{
            if(i==1){
                espera.await();
            }
            else if(i==2){
                espera2.await();
            }
            else{
                trabajador = new TareaTrabajador(t_minimo, t_maximo,nombre, barrera);
                ex.execute(trabajador);
                barrera.await();
            }
            tiempo=(int)System.currentTimeMillis()-tiempo;
            this.tiempo.addAndGet(tiempo);
        }catch(InterruptedException e){
            
        }catch(BrokenBarrierException ex){}
        
        return this.tiempo.intValue();
    }
    
    //Finaliza el programa y acaba al trabajador.
    public void finalizar()
    {
        ex.shutdownNow();
    }
    
}
