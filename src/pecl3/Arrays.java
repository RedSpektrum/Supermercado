/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pecl3;

import java.util.ArrayList;
import javax.swing.JTextField;

/**
 *
 * @author Nacho
 */
public class Arrays {
   
    private ArrayList<TareaCompradora> acceso;
    private ArrayList<TareaCompradora> colaCajas;
    private ArrayList<TareaCompradora> colaCarni;
    private ArrayList<TareaCompradora> colaPesc;
    private int genteEnEstantes;
    private TareaCompradora caja1, caja2, carnicero, pescadero;
    
    //Mostrar en pantalla
    private JTextField panelAcceso, panelCCajas, panelCCarn, panelCPesc, panelEstantes, panelCaja1, panelCaja2, panelPescadero, panelCarnicero;
    
    /*****************************************
    * Constructor de la clase                *
    *****************************************/
    public Arrays( JTextField pAcceso, JTextField pCCar, JTextField pCar, JTextField pCPes, JTextField pPes, JTextField pCCaj, JTextField pC1, JTextField pC2, JTextField pEst)
    {
       
        acceso = new ArrayList<TareaCompradora>();
        colaCajas = new ArrayList<TareaCompradora>();
        colaCarni = new ArrayList<TareaCompradora>();
        colaPesc = new ArrayList<TareaCompradora>();
        genteEnEstantes = 0;
        caja1 = new TareaCompradora();  caja2 = new TareaCompradora();
        carnicero = new TareaCompradora(); pescadero = new TareaCompradora();
        panelAcceso=pAcceso;
        panelCCarn = pCCar; panelCPesc = pCPes; panelCCajas=pCCaj;
        panelCarnicero = pCar; panelPescadero=pPes;
        panelCaja1 = pC1; panelCaja2 = pC2;
        panelEstantes = pEst;
    }
    //------------------------------ACCESO-----------------------------------------//
    
    //Entra en el array acceso
    public synchronized void colaAcceso(TareaCompradora cliente)
    {
            acceso.add(cliente);
            imprimirAcceso();
    }
    
    //Sale del array accceso
    public synchronized void entrar(TareaCompradora cliente)
    {

        acceso.remove(cliente);   
        imprimirAcceso();
        
    }
    
    //Imprime el array Acceso
    public void imprimirAcceso()
    {
        
            String contenido="";
            for(int i=0; i<acceso.size(); i++)
            {
              contenido=contenido+acceso.get(i).getIdent()+" ";
            }
            panelAcceso.setText(contenido);
        
    }
    
    
    //------------------------PESCADERIA-----------------------------------------------//
    
    //Entra en el array colaPescaderia
    public synchronized void colaPesc(TareaCompradora cliente)
    {
        colaPesc.add(cliente);
        imprimirCPescaderia();
    }
    
    //Sale del array colaPescaderia
    public synchronized void abandonarColaPes(TareaCompradora cliente)
    {
        colaPesc.remove(cliente);
        imprimirCPescaderia();
    }
    
    //Turno de pescaderia
    public synchronized void atenderPescaderia(TareaCompradora cliente)
    {
        pescadero = cliente;
        imprimirPescadero();
    }
    
    //Fin de turno de pescaderia
    public synchronized void abandonarPescaderia(TareaCompradora cliente)
    {
        pescadero = new TareaCompradora();
        imprimirPescadero();
    }
    
    //Imprime el array colaPescaderia
    public void imprimirCPescaderia()
    {
       
            String contenido="";
            for(int i=0; i<colaPesc.size(); i++)
            {
               contenido=contenido+colaPesc.get(i).getIdent()+" ";
            }
            panelCPesc.setText(contenido);
        
    }
    
    //Imprime el cliente atendido por el pescadero
    public void imprimirPescadero()
    {
       
            panelPescadero.setText(pescadero.getIdent());
        
    }
    
    //-------------------------CARNICERIA----------------------------------------------//
    
    //Entra en el array colaCarniceria
    public synchronized void colaCarn (TareaCompradora cliente)
    {
        colaCarni.add(cliente);
        imprimirCCarniceria();
    }
    
    //Sale del array colaCarniceria
    public synchronized void abandonarColaCar(TareaCompradora cliente)
    {
        colaCarni.remove(cliente);
        imprimirCCarniceria();
    }
    
    //Turno de carniceria
    public synchronized void atenderCarniceria(TareaCompradora cliente)
    {
        carnicero = cliente;
        imprimirCarnicero();
    }
    
    //Fin de turno de carniceria
    public synchronized void abandonarCarniceria(TareaCompradora cliente)
    {
        carnicero = new TareaCompradora();
        imprimirCarnicero();
    }
    
    //Imprime el array colaCarniceria
    public void imprimirCCarniceria()
    {
        
            String contenido="";
            for(int i=0; i<colaCarni.size(); i++)
            {
               contenido=contenido+colaCarni.get(i).getIdent()+" ";
            }
            panelCCarn.setText(contenido);
        
    }
    
    //Imprime el cliente atendido por el carnicero
    public void imprimirCarnicero()
    {
        
            panelCarnicero.setText(carnicero.getIdent());
        
    }
    
    //---------------------CAJAS--------------------------------------------------//
    
    //Entra en el array colaCajas
    public synchronized void colaCajas (TareaCompradora cliente)
    {
        colaCajas.add(cliente);
        imprimirCCajas();
    }
    
    //Sale del array colaCajas
    public synchronized void abandonarColaCajas(TareaCompradora cliente)
    {
        colaCajas.remove(cliente);
        imprimirCCajas();
    }
    
    //Entra en caja1
    public synchronized void Caja1(TareaCompradora cliente)
    {
        caja1 = cliente;
        imprimirCaja1();
    }
    
    //Sale de caja1
    public synchronized void dejarCaja1(TareaCompradora cliente)
    {
        caja1 = new TareaCompradora();
        imprimirCaja1();
    }
    
    //Entra en caja2
    public synchronized void Caja2(TareaCompradora cliente)
    {
        caja2 = cliente;
        imprimirCaja2();
    }
    
    //Sale de caja2
    public synchronized void dejarCaja2(TareaCompradora cliente)
    {
        caja2 = new TareaCompradora();
        imprimirCaja2();
    }
    
    //Imprime el array colaCajas
    public void imprimirCCajas()
    {
       
            String contenido="";
            for(int i=0; i<colaCajas.size(); i++)
            {
            contenido=contenido+colaCajas.get(i).getIdent()+" ";
            }
            panelCCajas.setText(contenido);
        
    }
    
    //Imprime caja1
    public void imprimirCaja1()
    {     
        
            panelCaja1.setText(caja1.getIdent());
        
    }
    
    //Imprime caja2
    public void imprimirCaja2()
    {
        
            panelCaja2.setText(caja2.getIdent());
        
    }
    
    //-----------------------ESTANTES------------------------------------------------//
    
    //Acceso de una persona a estantes
    public synchronized void accederAEstantes()
    {
        genteEnEstantes++;
        imprimirEstantes();
    }
    
    //Abandono de los estantes por parte de una persona
    public synchronized void abandonarEstantes()
    {
        genteEnEstantes--;
        imprimirEstantes();
    }
    
    //Imprime la gente en estantes
    public void imprimirEstantes()
    {

        panelEstantes.setText(""+genteEnEstantes );

    }
    
    //----------------------------LIMPIAR-------------------------------------------//
 
    //Limpia los JTextFields
    public void limpiar()
    { 
        acceso.clear();     imprimirAcceso();
        colaCajas.clear();  imprimirCCajas();
        caja1 = new TareaCompradora();  imprimirCaja1();
        caja2 = new TareaCompradora();  imprimirCaja2();
        colaPesc.clear();   imprimirCPescaderia();
        pescadero=new TareaCompradora();    imprimirPescadero();
        colaCarni.clear();  imprimirCCarniceria();
        carnicero = new TareaCompradora(); imprimirCarnicero();
        genteEnEstantes=0;  imprimirEstantes();
        
    }
    
}
