/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

/**
 *
 * @author odalys
 */
import java.net.*;
import java.io.*; 
import java.util.ArrayList;
 
public class ServidorMulticast extends Thread{
    public static final String MCAST_ADDR = "230.1.1.1";//dir clase D valida, grupo al que nos vamos a unir
    public static final int MCAST_PORT = 4000;
    public static final int DGRAM_BUF_LEN = 512;
    public static ArrayList<String> usuarios_linea = new ArrayList<String>();
    
        public static void EnviarListaUsuarios (MulticastSocket socket,InetAddress group){
            String userList = "list;";
            for(int i = 0; i < usuarios_linea.size(); i++){
                        userList = userList + (String) usuarios_linea.get(i) +";";
            }
            try{
    		byte[] m = userList.getBytes();
                DatagramPacket mensajeSalida =
                    new DatagramPacket(m, m.length, group,MCAST_PORT);
                    socket.send(mensajeSalida);
    		
            }catch(IOException e){
    		e.printStackTrace();
    		System.exit(2);
    	}
            
}

	public void run(){
            InetAddress group = null;
            try{
            	//msg=InetAddress.getLocalHost().getHostAddress();
    		group = InetAddress.getByName(MCAST_ADDR); //se trata de resolver dir multicast  		
            }catch(UnknownHostException e){
    		e.printStackTrace();
    		System.exit(1);
            }
/********************inicia loop***************************/
            for(;;){
                try{
                    MulticastSocket socket = new MulticastSocket(MCAST_PORT);
                    socket.joinGroup(group); // se configura para escuchar el paquete
                    System.out.println("Servidor iniciado esperando clientes...");
                
                    byte[] bufer = new byte[DGRAM_BUF_LEN];//crea arreglo de bytes 
                    String linea;
                    DatagramPacket mensajeEntrada =
                        new DatagramPacket(bufer, bufer.length);
                    socket.receive(mensajeEntrada);
                    linea = new String(mensajeEntrada.getData(), 0, mensajeEntrada.getLength());
        
                    //Se ha conectado un usuario
                    if(linea.startsWith("inicio")){
                        String[] info = linea.split(";");
                        if(!(usuarios_linea.contains(info[1]))){
                            usuarios_linea.add(info[1]);
                        }                        
                        EnviarListaUsuarios(socket,group);                        
                    }
                    
                    //Se ha desconectado un usuario
                    if(linea.startsWith("fin")){
                        String[] info = linea.split(";");
                        if((usuarios_linea.contains(info[1]))){
                            usuarios_linea.remove(info[1]);
                        }                        
                        EnviarListaUsuarios(socket,group);                        
                    }
                    
                    String usuario = "";
                    for(int i = 0; i < usuarios_linea.size(); i++){
                        usuario = (String) usuarios_linea.get(i);                 
                        System.out.println("usuario["+i+"]:" + usuario);
                    }
                
                
                socket.close();    		
            }catch(IOException e){
    		e.printStackTrace();
    		System.exit(2);
    	}

	try{
		Thread.sleep(1000*5);
	}catch(InterruptedException ie){}
        }//for;;
/*****************termina Loop***************************/    	

    	
	}//run
    	
    public static void main(String[] args) {
        try{
	    ServidorMulticast mc2 = new ServidorMulticast();
	    mc2.start();
	}catch(Exception e){e.printStackTrace();}

	
    }//main
}//class
