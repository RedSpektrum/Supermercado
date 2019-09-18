/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pecl3;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Nacho
 * 
 */
public class Pescaderia extends Zona {
    
    private Log log;
    
    /*****************************************
    * Constructor de la clase                *
    *****************************************/
    public Pescaderia(Log log){
        super(1,1,2000,3000, "PESCADERA"); //1 trabajador + servicio 1 = single thread
        this.log=log;
    }
    
    /**
     * Gestiona la atención al cliente de la pescaderia
     * @param cliente 
     */
    public void atenderCliente(TareaCompradora cliente){
    
        //El control de acceso está fuera, así que fija ocupado
        String ntrabajador = lanzarTrabajador(1);
        log.anadirACola("PESCADERIA: "+cliente.getIdent()+" esta siendo atendido por "+ntrabajador);
        //Espera a que el trabajador accabe el proceso
        log.anadirACola("TIEMPO ==> "+ntrabajador+" ha estado ocupado durante "+(float)(esperar(1))/1000+" segundos en total.");
        
    }
    
    
    
    
    
}
