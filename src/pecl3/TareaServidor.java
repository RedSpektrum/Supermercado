/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pecl3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author javir
 */
public class TareaServidor extends Thread{
    

    private int msg;
    private ServerSocket servidor;
    private Socket conexion;
    private DataOutputStream salida;
    private DataInputStream entrada;
    private Supermercado mercado;

    public TareaServidor(int puerto, Supermercado mercado)
    {
        try
        {
            servidor = new ServerSocket(puerto);
        }catch(IOException e){}
        this.mercado = mercado;
        
    }
    
    public void run()
    {
        try{          
            while(true)
            {

                conexion = servidor.accept();
                entrada = new DataInputStream(conexion.getInputStream());
                msg=entrada.readInt();
                
                if(msg==1)
                {
                    mercado.continuar();
                }
                else if(msg==0)
                {
                    mercado.parar();
                }
                if(msg==2)
                {
                    mercado.continuarPescadero();
                }
                else if(msg==3)
                {
                    mercado.pararPescadero();
                }
                if(msg==4)
                {
                    mercado.continuarCarnicero();
                }
                else if(msg==5)
                {
                    mercado.pararCarnicero();
                }
                
                entrada.close();
                conexion.close();               
            }
        }catch(IOException e){}       
    }
    
}
