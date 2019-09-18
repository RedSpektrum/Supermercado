/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pecl3;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Nacho
 */
public class Cajas extends Zona{
    
    private Log log;
    private AtomicInteger nCajera = new AtomicInteger(1); //Numero de cajera
    private Arrays arrays;
    
    /*****************************************
    * Constructor de la clase                *
    *****************************************/
    public Cajas( Log log, Arrays arrays){
        super(2, 2, 3000, 5000, "CAJERA"); // 2 Cajeras y servicio 2 = scheduled thread pool
        this.log=log;
        nCajera.set(1);
        this.arrays=arrays;
    }
    
    /**
     * Gestiona el paso por cajas del cliente
     * @param cliente 
     */
    public void cobrarCliente(TareaCompradora cliente){

        int numero=0; //Indica el número de cajera que atiende
        Semaphore semaforo = new Semaphore(0);
        
        //Tomamos el numero de cajera que libra
        numero=nCajera.intValue();
        
        //Elegimos el numero de la otra cajera si una está trabajando
        //por si llega otro cliente y la actual está ocupada
        if(nCajera.intValue()==1){
            nCajera.set(2);
            //Marca al cliente que va a atender
            arrays.Caja1(cliente);
        }
        else{
            nCajera.set(1);
            //Marca al cliente que va a atender
            arrays.Caja2(cliente);
        }
        //La cajera empieza a trabajar
        String ntrabajador = lanzarTrabajador(numero);       
        log.anadirACola("CAJAS: "+cliente.getIdent()+" está siendo atendido por "+ntrabajador+" "+numero);
        //Esperamos a que la cajera acabe
        log.anadirACola("TIEMPO ==> "+ntrabajador+"S han estado ocupadas durante "+(float)(esperar(numero))/1000+" segundos en total.");
        
        //Cuando acaba se marca a si misma como libre
        if(numero==1){
            nCajera.set(1);
            //Marca el fin de su trabajo
            arrays.dejarCaja1(cliente);
        }
        
        else{
            nCajera.set(2);
            //Marca el fin de su trabajo
            arrays.dejarCaja2(cliente);
        }

    }
    
    

}
