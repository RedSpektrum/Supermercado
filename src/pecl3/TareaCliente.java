/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pecl3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 *
 * @author javir
 */
public class TareaCliente extends Thread{
    
    private ModuloControl modCon;
    private Socket cliente;
    private DataOutputStream salida;
    private DataInputStream entrada;
    private int mensaje;
    
    public TareaCliente(int msg)
    {
        try
        {   
            cliente = new Socket(InetAddress.getLocalHost(),5050);
        }catch(IOException e){}
        this.mensaje = msg;
    }
    
    public void run()
    {
        try
        {
            salida = new DataOutputStream(cliente.getOutputStream());
            salida.writeInt(mensaje);
                
            if(mensaje==-2)
            {
                salida.close();
                cliente.close();
            }
        }catch(IOException e){}
        
    }
    
}
