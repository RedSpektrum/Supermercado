/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pecl3;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nacho
 */
public class Supermercado {
    
    //Atributos
    private Pescaderia pescaderia;
    private Carniceria carniceria;
    private Estantes estantes;
    private Cajas cajas;
    private Arrays arrays;  //Coloca a los clientes en los arrays ligados a los JTextFields de visual
    
    //Datos
    private int aforo=20;
    private AtomicInteger cantidadClientes, clientesAtendidos, tiempoSuper, tiempoCompras, clientes, tiempoZonas;
    private boolean forzado;
    
    //Colas para acceder a las zonas
    private LinkedBlockingQueue<TareaCompradora> colaAcceso = new LinkedBlockingQueue();
    private LinkedBlockingQueue<TareaCompradora> colaSuper = new LinkedBlockingQueue(aforo);
    private LinkedBlockingQueue<TareaCompradora> colaPescaderia = new LinkedBlockingQueue();
    private LinkedBlockingQueue<TareaCompradora> colaCarniceria = new LinkedBlockingQueue();
    private LinkedBlockingQueue<TareaCompradora> colaCajas= new LinkedBlockingQueue();
    private LinkedBlockingQueue<TareaCompradora> colaSalida= new LinkedBlockingQueue();

    //Clientes que abandonan
    private LinkedBlockingQueue fuera= new LinkedBlockingQueue();
    
    //Control de entrada en las zonas y el super
    private Semaphore semaforo = new Semaphore(aforo);
    private Semaphore entradaPes = new Semaphore(1);
    private Semaphore entradaCar = new Semaphore(1);
    private Semaphore entradaCaj = new Semaphore(2);
    
    //Control de ejecucion
    private boolean pararCarnicero, pararPescadero, pararCajas, pararEstantes;
    private Semaphore pausaCarnicero, pausaCajas, pausaPescadero, pausaEstantes;
    
    //Log
    private Log log;
    
    /*****************************************
    * Constructor de la clase                *
    *****************************************/
    public Supermercado(Log log, Arrays arrays){
        //Informacion
        cantidadClientes=new AtomicInteger(0);
        clientesAtendidos =new AtomicInteger(0);
        tiempoSuper=new AtomicInteger(0);
        tiempoCompras=new AtomicInteger(0);
        clientes=new AtomicInteger(0);
        tiempoZonas=new AtomicInteger(0);
        //Zonas
        cajas = new Cajas(log, arrays);
        estantes = new Estantes(log);
        carniceria =  new Carniceria(log);
        pescaderia = new Pescaderia(log);
        //Log
        this.log = log;
        this.log.start();
        //Grafico
        this.arrays=arrays;
        //Pausas
        pausaPescadero = new Semaphore(0);
        pausaCajas  = new Semaphore(0);
        pausaEstantes  = new Semaphore(0);
        pararPescadero=false;   pararCajas=false;
        pausaCarnicero = new Semaphore(0);
        pararCarnicero=false;   pararEstantes=false;
        forzado=false;
    

    }
    
    public void irDeCompras(TareaCompradora cliente)
    {   
        
        //El cliente entra en la cola del establecimiento
        entrar(cliente);
        
        /************************************
        *   YA DENTRO DEL ESTABLECIMIENTO   *
        ************************************/      
        //El cliente se redirige a su respectiva zona y allí le atienden
        redirigir();         
        //Se cobra al cliente
        accederACajas();       
        //El cliente abandona
        abandonar();      
    }
    
    /**
     * Permite el acceso al supermercado pasando por la cola de entrada
     * 
     * @param cliente -> TareaCompradora que se antenderá
     */
    public void entrar(TareaCompradora cliente)
    {
        //Hace cola para entrar al super
        colaAcceso.offer(cliente);
        arrays.colaAcceso(cliente);
        //Toma un permiso para entrar
        try{
            semaforo.acquire();
        }catch(InterruptedException e)
        {
        }
        
        //Tomamos el primer cliente de la cola
        cliente =  colaAcceso.poll();
        clientes.incrementAndGet();
        log.anadirACola("ENTRADA: "+cliente.getIdent()+" entra en la cola del supermercado.");

        //Se mete dentro de la cola del super y abandona el cuadro de texto de espera
        cliente.dentro();
        colaSuper.offer(cliente);
        arrays.entrar(cliente);
        //Despues se incrementa la cantidad de usuarios
        cantidadClientes.incrementAndGet(); 
        log.anadirACola("DENTRO: " + cliente.getIdent() + " ha entrado al supermercado.\nRECUENTO DE CLIENTES ==> Ya han entrado "+clientes.intValue()+" clientes!!!!");
        //Si está lleno lo notificamos
        if(cantidadClientes.intValue() == aforo)
        {
            log.anadirACola("---------------------- Mercado Lleno ----------------------");
        }
    }
    
    /**
     * Redirige desde la entrada del super a la zona correspondiente
     */
    public void redirigir()
    {
        //Sacamos al cliente
        TareaCompradora cliente = colaSuper.poll();    
        //Tomamos su zona para saber a donde mandarlo
        int zona = cliente.getZona();
        switch(zona)
        {
            /*
                Seleccionaremos su zona y se enviará a la cola correspondiente
            */
            case 0:
                log.anadirACola("COLA DE PESCADERIA: "+cliente.getIdent()+" entra en la cola de la pescaderia.");
                colaPescaderia.offer(cliente);
                //Entra en la cola de la pescaderia
                arrays.colaPesc(cliente);
                accederAPescaderia();
                break;
            
            case 1:
                log.anadirACola("COLA DE CARNICERIA: "+cliente.getIdent()+" entra en la cola de la carniceria.");
                colaCarniceria.offer(cliente);
                //Entra en la cola de la carniceria
                arrays.colaCarn(cliente);
                accederACarniceria();
                break;
            
            default:
                log.anadirACola("ESTANTES: "+cliente.getIdent()+" entra en los estantes.");
                //No hay cola de estantes
                accederAEstantes(cliente);
                break;
        }
    }
    /**
     * Gestiona el paso por la pescadería
     */
    public void accederAPescaderia()
    {
        
        //Espera a su turno
        try{
            entradaPes.acquire();
            if(pararPescadero==true)
            {

                pausaPescadero.acquire();
                pausaPescadero = new Semaphore(0);
            }     
        }catch(InterruptedException e){
            
        }
        
        
        
        //En su turno se le atiende       
        TareaCompradora cliente = colaPescaderia.poll(); 
        //abandona la cola de pescaderia
        arrays.abandonarColaPes(cliente);
        //Pasa a ser el atendido
        arrays.atenderPescaderia(cliente);     
        //Se le atiende
        pescaderia.atenderCliente(cliente);
        //Abandona la pescaderia
        arrays.abandonarPescaderia(cliente);
        //Libera permiso al siguiente
        entradaPes.release();       
        colaCajas.offer(cliente);  
        //Accede a la cola de las cajas
        arrays.colaCajas(cliente);
        log.anadirACola("COLA DE CAJAS: "+cliente.getIdent()+" entra en la cola de las cajas");
    }
    
    /**
     * Gestiona el paso por la carniceria
     */
    public void accederACarniceria()
    {
        //Espera a su turno
        try{
            entradaCar.acquire();
            if(pararCarnicero==true)
            {
                pausaCarnicero.acquire();
                pausaCarnicero = new Semaphore(0);
            }
        }catch(InterruptedException e){
            
        }
        
        
        
        //En su turno se le atiende
        TareaCompradora cliente = colaCarniceria.poll();     
        //abandona la cola de carniceria
        arrays.abandonarColaCar(cliente);
        //Pasa a ser el atendido
        arrays.atenderCarniceria(cliente);
        //Se le atiende
        carniceria.atenderCliente(cliente);
        //Abandona la carniceria
        arrays.abandonarCarniceria(cliente);
        //libera permiso al siguiente
        entradaCar.release();
        colaCajas.offer(cliente);
        //Accede a la cola de las cajas
        arrays.colaCajas(cliente);
        log.anadirACola("COLA DE CAJAS: "+cliente.getIdent()+" entra en la cola de las cajas");
    }
    
    /**
     * Gestiona el paso de los clientes por la zona de estantes
     * (Como no hay cola necesita ese parámetro para gestionar dicho cliente)
     * @param cliente 
     */
    public void accederAEstantes(TareaCompradora cliente)
    {        
        try{       
            if(pararEstantes==true)
            {
                pausaEstantes.acquire();
                pausaEstantes = new Semaphore(0);
            }
        }catch(InterruptedException e){
            
        }
        
        //Entra en estantes
        arrays.accederAEstantes();
        estantes.AccederAEstantes(cliente);
        //Abandona los estantes
        arrays.abandonarEstantes();
        if(forzado)
        {
            expulsar();
        }
        //Entra en cola de cajas
        colaCajas.offer(cliente);
        arrays.colaCajas(cliente);
        log.anadirACola("COLA DE CAJAS: "+cliente.getIdent()+" entra en la cola de las cajas");
    }
    
    /**
     * Gestiona el paso por cajas para cobrar al cliente
     */
    public void accederACajas()
    {
        //Espera a su turno
        try{
            entradaCaj.acquire();
            if(pararCajas==true)
            {
                pausaCajas.acquire();
                pausaCajas = new Semaphore(0);
            }
        }catch(InterruptedException e){
            
        }       
        //En su turno se le atiende
        TareaCompradora cliente = colaCajas.poll();
        //Se saca del JTextField
        arrays.abandonarColaCajas(cliente);
        log.anadirACola("COLA DE CAJAS: "+cliente.getIdent()+" entra en cajas.");
        //Se cobra al cliente (La gestion del JTextField en este caso va dentro)
        cajas.cobrarCliente(cliente);       
        entradaCaj.release();
        colaSalida.offer(cliente); 
    }
    
    /**
     * Gestiona el abandono del establecimiento
     */
    public void abandonar()
    {
        //Se saca de la cola de salida
        TareaCompradora cliente = colaSalida.poll();
        log.anadirACola("SALIDA: "+cliente.getIdent() +" abandona el super");
        //Se mete en el array de exterior (Por si mostramos esto)
        fuera.offer(cliente);
        //Decrementamos la cantidad de clientes dentro del super
        cantidadClientes.decrementAndGet();
        clientesAtendidos.incrementAndGet();
        log.anadirACola("RECUENTO DE CLIENTES ==> Ya se ha atendido a "+clientesAtendidos.intValue()+" clientes!!!!");
        
        if(cantidadClientes.intValue()+1==aforo){
            log.anadirACola("---------------------- Mercado ya no está lleno ----------------------");
        }
        //Liberamos un permiso para que los demás puedan entrar
        semaforo.release(1);
    }    
    
    //------------------------------------------------------------------------------------------------------------------//
    //Metodos que pausan el programa
    public void pararPescadero()
    {
        pararPescadero=true;
        log.anadirACola("---------------------- P  A  U  S  A  PESCADERO ----------------------");
        System.out.println("---------------------- P  A  U  S  A PESCADERO----------------------");

    }

    public void continuarPescadero()
    {
        pararPescadero=false;
        pausaPescadero.release(aforo);
        log.anadirACola("---------------------- C  O  N  T  I  N  U  A  R PESCADERO----------------------");
        System.out.println("---------------------- C  O  N  T  I  N  U  A  R PESCADERO----------------------");
    }

    public void pararCarnicero()
    {
        pararCarnicero=true;
        log.anadirACola("---------------------- P  A  U  S  A  CARNICERO ----------------------");
        System.out.println("---------------------- P  A  U  S  A CARNICERO----------------------");

    }

    public void continuarCarnicero()
    {
        pararCarnicero=false;
        pausaCarnicero.release(aforo);
        log.anadirACola("---------------------- C  O  N  T  I  N  U  A  R CARNICERO----------------------");
        System.out.println("---------------------- C  O  N  T  I  N  U  A  R CARNICERO----------------------");
    }
    
    public void pararCajas()
    {
        pararCajas=true;
        log.anadirACola("---------------------- P  A  U  S  A  CAJAS ----------------------");
        System.out.println("---------------------- P  A  U  S  A CARNICERO----------------------");
    }
    
    public void continuarCajas()
    {
        pararCajas=false;
        pausaCajas.release(aforo);
        log.anadirACola("---------------------- C  O  N  T  I  N  U  A  R CAJAS----------------------");
        System.out.println("---------------------- C  O  N  T  I  N  U  A  R CAJAS----------------------");
    }
    
    public void pararEstantes()
    {
        pararCajas=true;
        log.anadirACola("---------------------- P  A  U  S  A  CAJAS ----------------------");
        System.out.println("---------------------- P  A  U  S  A CAJAS----------------------");
    }
    
    public void continuarEstantes()
    {
        pararEstantes=false;
        pausaEstantes.release(aforo);
        log.anadirACola("---------------------- C  O  N  T  I  N  U  A  R ESTANTES----------------------");
        System.out.println("---------------------- C  O  N  T  I  N  U  A  R ESTANTES----------------------");
    }
    
    public void parar()
    {
        pararCajas();   pararEstantes();
        pararPescadero();   pararCarnicero();
    }
    
    public void continuar()
    {
        continuarCajas();   continuarEstantes();
        continuarPescadero();   continuarCarnicero();
    }
    
    public void setTiempoCompras(int[] t, String nombre)
    {
        tiempoCompras.addAndGet(t[0]);
        tiempoSuper.addAndGet(t[1]);
        log.anadirACola("El cliente "+nombre+" paso "+(float)t[0]/1000+" segundos comprando.\nLa media de tiempo en segundos es de: "+(float)tiempoCompras.intValue()/(clientesAtendidos.intValue()*1000)+
        "\nPaso "+(float)(t[1]/1000)+" dentro del mercado.\nLa media de tiempo dentro del mercado es de: "+(float)tiempoSuper.intValue()/(clientesAtendidos.intValue()*1000));
    }
    
    //--------------------------------------------------------------------------------//
    //Metodos que fuerzan el fin del programa
    public void finalizar()
    {
        
        pescaderia.finalizar();
        carniceria.finalizar();
        cajas.finalizar();
        estantes.finalizar();
        colaAcceso.clear(); colaCajas.clear();  
        colaPescaderia.clear(); colaCarniceria.clear();
        colaSuper.clear();  colaSalida.clear();
        forzado=true;
        for(int i=0; i<cantidadClientes.intValue(); i++)
        {
            expulsar();
        }   
    }
    
    //Expulsa a los clientes
    public void expulsar()
    {
        //Se saca de la cola de salida
        colaCajas.poll();
        colaSalida.poll();
        colaPescaderia.poll();
        //Decrementamos la cantidad de clientes dentro del super
        cantidadClientes.decrementAndGet();
    }
    
    public void limpiar()
    {
       arrays.limpiar(); 
    }
}