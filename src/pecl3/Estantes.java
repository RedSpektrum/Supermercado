/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pecl3;

import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Nacho
 */
public class Estantes extends Zona{
    private Log log;
    
    /*****************************************
    * Constructor de la clase                *
     * @param log
    *****************************************/
    public Estantes(Log log){
        super(20,3,1000,11000,"ESTANTES");
        this.log=log;
}
    
    /**
     * Gestiona el acceso a los estantes
     * @param cliente 
     */
    public void AccederAEstantes(TareaCompradora cliente){
        //No hace falta esperas en los estantes
        log.anadirACola("ESTANTES: "+cliente.getIdent()+" esta en los estantes.");
        //Espera a que el trabajador accabe el proceso
        esperar(3);
    }
    
}
