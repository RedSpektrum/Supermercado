/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pecl3;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Nacho
 */
public class Carniceria extends Zona {
    
    private TareaCompradora cliente ;
    private Log log;

    /*****************************************
    * Constructor de la clase                *
    *****************************************/
    public Carniceria(Log log){
        super(1,1,1500,2500,"CARNICERA"); //1 trabajador + servicio 1 = single thread
        this.log = log;
    }
    
    /**
     * Gestiona el paso del cliente por la carnicería
     * @param cliente 
     */
    public void atenderCliente(TareaCompradora cliente){
    
        Semaphore semaforo = new Semaphore(0);
        //El control de acceso está fuera, así que fija ocupado
        String ntrabajador = lanzarTrabajador(1);
        log.anadirACola("CARNICERIA: "+cliente.getIdent()+" esta siendo atendido por "+ntrabajador);
        //Espera a que el trabajador accabe el proceso
        log.anadirACola("TIEMPO ==> "+ntrabajador+" ha estado ocupado durante "+(float)(esperar(1))/1000+" segundos en total.");    
    }
    public void finalizarCarniceria()
    {
        Thread.currentThread().destroy();
    }
        
}
