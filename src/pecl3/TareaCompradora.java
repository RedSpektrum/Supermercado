/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pecl3;

/**
 *
 * @author Nacho
 */
public class TareaCompradora extends Thread {
    ///ATRIBUTOS        //Se llama ide para no interferir con
    private String ide; // el id extendido por la clase Thread
    private Supermercado supermercado;
    private int zona;   
    private int inicio;
    private int tiempo;
    private int tiempoDentro;
    /*************************************************
    * La zona indica a dónde irá el comprador        *
    *  0 = Pescadería | 1 = Carnicería | 2 = Estante *
    *************************************************/

    ///MÉTODOS
    /*****************************************
    * Constructor de la clase                *
    *****************************************/
    public TareaCompradora (int id, Supermercado su)
    {
        this.ide="Comprador "+id;
        this.supermercado = su;
        /// Iniciamos los parámetros de zona
        zona = (int) (Math.random()*3);
    }

    public TareaCompradora ()
    {
        this.ide="";
    }
    
    /***********************
    * Lanzamiento del hilo *
    ***********************/
    @Override
    public void run()
    {
        inicio = (int) System.currentTimeMillis();
        System.out.println(getIdent()+" se va al mercado");
        tiempo=tiempoPrograma();
        // Accedemos al mercado
        try
            {
                //Tiempo que duerme para intentar enviar mensajes
                sleep((int)(800)+200);
            } catch (InterruptedException ex) {}
        
        supermercado.irDeCompras(this);

        System.out.println(getIdent()+" abandona el mercado");
        
        tiempo=tiempoPrograma()-tiempo;     //Tiempo que estuvo comprando desde que llego hasta que se fue
        tiempoDentro = tiempo-tiempoDentro; //Tiempo que estuvo comprando - lo que espero para entrar en el super
        
        int[] array = new int[2];   array[0]=tiempo; array[1]=tiempoDentro;
        supermercado.setTiempoCompras(array, ide);
    }
    
    /**********************************************
     * @return ide -> identificador del comprador *
     *********************************************/
    public String getIdent()
    {
        return ide;
    }
    
    /*******************************************************
     * @return zona -> zona a la que accedera el comprador *
     ******************************************************/
    public int getZona()
    {
        return zona;
    }
    
    /**
     * medida de cuando entra en el supermercado
     */
    public void dentro()
    {
        tiempoDentro=tiempoPrograma();
        //Por algún motivo toma este tiempo en negativo a veces.
        if(tiempoDentro<0){ tiempoDentro=-tiempoDentro;}
    }
    
    /**
     * Toma el momento actual restandole el inicio para tomar el tiempo exacto de ejecución
     * @return el tiempo de ejecución en milisegundos
     */
    public int tiempoPrograma()
    {
        return (int)System.currentTimeMillis()-inicio;
    }
    
}
