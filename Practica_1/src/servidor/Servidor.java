package servidor;

import java.net.*;
import java.io.*;


public class Servidor {

    public void conectar() {
        try {
            int pto = 8000;
            ServerSocket s = new ServerSocket(pto);
            s.setReuseAddress(true);
            File f = new File("");
            String rutaServidor = f.getAbsolutePath();
            String carpeta = "archivos";
            String ruta_archivos = rutaServidor + "\\" + carpeta + "\\";
            System.out.println("ruta:" + ruta_archivos);
            File f2 = new File(ruta_archivos);
            f2.mkdirs();
            f2.setWritable(true);
            System.out.println("Servidor establecido...Esperando Cliente");
            for (;;) {
                Socket c1 = s.accept();
                DataInputStream dis = new DataInputStream(c1.getInputStream());
                int accion = dis.readInt();
                String nombre = dis.readUTF();
                long tam = dis.readLong();
                String ruta = dis.readUTF();

                if (accion == 1) {
                    System.out.println("Comienza descarga del archivo " + nombre + " de " + tam + " bytes\n\n");
                    DataOutputStream dos = new DataOutputStream(new FileOutputStream(ruta_archivos + nombre));
                    long recibidos = 0;
                    int l = 0;
                    int porcentaje = 0;
                    while (recibidos < tam) {
                        byte[] b = new byte[1500];
                        l = dis.read(b);
                        System.out.println("leidos: " + l);
                        
                        dos.write(b, 0, l);
                        dos.flush();
                        recibidos = recibidos + l;
                        porcentaje = (int) ((recibidos * 100) / tam);
                        System.out.print("\rEnviado el " + porcentaje + " % del archivo");
                    }//while
                    System.out.println("\rArchivo recibido..");
                    dos.close();
                } else if (accion == 3) {
                    System.out.println("El archivo" + nombre + " de " + tam + " bytes ha sido eliminado del servidor\n\n");
                } else if (accion == 4) {
                    System.out.println("El archivo" + nombre + " de " + tam + " bytes ha sido eliminado de su carpeta local\n\n");
                } else if (accion == 5) {

                    DataOutputStream dos = new DataOutputStream(c1.getOutputStream());

                    System.out.println(ruta + tam + nombre);
                    DataInputStream dis2 = new DataInputStream(new FileInputStream(ruta));
                    long recibidos = 0;
                    int l = 0, porcentaje = 0;

                    while (recibidos < tam) {
                        byte[] b = new byte[1500];
                        l = dis2.read(b);
                        System.out.println("leidos: " + l);
                        dos.write(b, 0, l);
                        dos.flush();
                        recibidos = recibidos + l;
                        porcentaje = (int) ((recibidos * 100) / tam);
                        System.out.print("\rEnviado el " + porcentaje + " % del archivo");
                    }
                    dos.close();
                } else if (accion == 7) {
                    System.out.println("Finalizada la conexión del servidor\n\n");
                } else if (accion == 8) {
                    System.out.println("Opcion no valida, por favor intente de nuevo");
                }
                c1.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            int ptoP = 8001;
            ServerSocket s1 = new ServerSocket(ptoP);
            s1.setReuseAddress(true);
            //System.out.println("Servidor Principal Iniciado");
            for (;;) {
                Socket cP = s1.accept();
                System.out.println("Cliente conectado desde " + cP.getInetAddress() + ":" + cP.getPort());
                Servidor s = new Servidor();
                s.conectar();
                cP.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//main
}
