package servidor;

import java.net.*;
import java.io.*;
import java.util.Scanner;
import javax.swing.JFileChooser;

public class Cliente {

    public void enlistarCarpetas(File f2, int nivel) {
        File aux[] = f2.listFiles();
        for (int i = 0; i < (aux.length); i++) {
            for (int j = 0; j < nivel; j++) {
                System.out.print("  -");
            }
            if (aux[i].isDirectory()) {
                System.out.println("Carpeta " + aux[i].getName() + "\n");
                enlistarCarpetas(aux[i], nivel + 1);

            } else {
                System.out.println(aux[i].getName() + "\n");
            }
        }
    }

    public void eliminarArchivoCarpeta(File f) {
        if (f.isDirectory()) {
            File f2[] = f.listFiles();
            for (int i = 0; i < f2.length; i++) {
                eliminarArchivoCarpeta(f2[i]);
            }
        }
        f.delete();
    }

    public static void main(String[] args) throws Exception {
        int ptoC = 8001;
        String dirC = "172.22.80.1";
        Socket cC = new Socket(dirC, ptoC);
        Scanner sn = new Scanner(System.in);
        boolean salir = false;
        int opcion;
        while (!salir) {
            System.out.println("1. Subir archivos al servidor");
            System.out.println("2. Subir carpetas al servidor");
            System.out.println("3. Eliminar archivos o carpetas de mi servidor");
            System.out.println("4. Eliminar archivos y carpetas localmente");
            System.out.println("5. Descargar archivos de mi servidor");
            System.out.println("6. Descargar carpetas de mi servidor");
            System.out.println("7. Salir");
            System.out.println("Escoja una opcion: ");
            opcion = sn.nextInt();

            switch (opcion) {
                case 1:
                    try {
                    int pto = 8000;
                    String dir = "localhost";

                    System.out.println("Archivos en el servidor");
                    File carpetaServidor = new File("./archivos");
                    System.out.println(carpetaServidor.getAbsolutePath());
                    Cliente cEnlistarS = new Cliente();
                    cEnlistarS.enlistarCarpetas(carpetaServidor, 0);

                    System.out.println("Archivos en la carpeta local del cliente");
                    File carpetaCliente = new File("./cliente");
                    System.out.println(carpetaServidor.getAbsolutePath());
                    Cliente cEnlistarC = new Cliente();
                    cEnlistarC.enlistarCarpetas(carpetaCliente, 0);

                    System.out.println("Conexion con servidor establecida.. lanzando FileChooser..");
                    JFileChooser jf = new JFileChooser("./cliente");
                    jf.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                    jf.setMultiSelectionEnabled(true);
                    int r = jf.showOpenDialog(null);
                    if (r == JFileChooser.APPROVE_OPTION) {
                        File f[] = jf.getSelectedFiles();
                        for (File f1 : f) {
                            Socket cl = new Socket(dir, pto);
                            System.out.println("Preparandose pare enviar archivo " + f1.getAbsolutePath() + " de " + f1.length() + " bytes");
                            DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
                            DataInputStream dis = new DataInputStream(new FileInputStream(f1.getAbsolutePath()));
                            dos.writeInt(1);
                            dos.flush();
                            dos.writeUTF(f1.getName());
                            dos.flush();
                            dos.writeLong(f1.length());
                            dos.flush();
                            dos.writeUTF(f1.getAbsolutePath());
                            dos.flush();
                            long enviados = 0;
                            int l = 0;
                            while (enviados < f1.length()) {
                                byte[] b = new byte[1500];
                                l = dis.read(b);
                                System.out.println("enviados: " + l);
                                dos.write(b, 0, l);
                                dos.flush();
                                enviados = enviados + l;
                            } //while
                            System.out.println("\nArchivo enviado..");
                            dis.close();
                            dos.close();
                            cl.close();
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } // catch
                break;

                case 2:
                    try {
                    int pto = 8000;
                    String dir = "localhost";
                    System.out.println("Archivos en el servidor");
                    File carpetaServidor = new File("./archivos");
                    System.out.println(carpetaServidor.getAbsolutePath());
                    Cliente cEnlistarS = new Cliente();
                    cEnlistarS.enlistarCarpetas(carpetaServidor, 0);

                    System.out.println("Archivos en la carpeta local del cliente");
                    File carpetaCliente = new File("./cliente");
                    System.out.println(carpetaServidor.getAbsolutePath());
                    Cliente cEnlistarC = new Cliente();
                    cEnlistarC.enlistarCarpetas(carpetaCliente, 0);

                    System.out.println("Conexion con servidor establecida.. lanzando FileChooser..");
                    ComprimirCarpeta comp = new ComprimirCarpeta();
                    JFileChooser jfc = new JFileChooser("./cliente");
                    jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    jfc.setMultiSelectionEnabled(true);
                    int r = jfc.showOpenDialog(jfc);
                    if (r == JFileChooser.APPROVE_OPTION) {
                        File f[] = jfc.getSelectedFiles();
                        for (int i = 0; i < f.length; i++) {
                            Socket cl = new Socket(dir, pto);
                            File archivoSeleccionado[] = jfc.getSelectedFiles();
                            String nuevoParent = archivoSeleccionado[i].getAbsolutePath();
                            String destino = nuevoParent + ".zip";
                            comp.comprimir(nuevoParent, destino);
                            File aux = new File(destino);
                            System.out.println("Preparandose pare enviar carpeta comprimida " + aux.getAbsolutePath() + " de " + aux.length() + " bytes");
                            DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
                            DataInputStream dis = new DataInputStream(new FileInputStream(aux.getAbsoluteFile()));
                            dos.writeInt(1);
                            dos.flush();
                            dos.writeUTF(aux.getName());
                            dos.flush();
                            dos.writeLong(aux.length());
                            dos.flush();
                            dos.writeUTF(aux.getAbsolutePath());
                            dos.flush();
                            long enviados = 0;
                            int l = 0, porcentaje = 0;
                            while (enviados < aux.length()) {
                                byte[] b = new byte[1500];
                                l = dis.read(b);
                                System.out.println("enviados: " + l);
                                dos.write(b, 0, l);
                                dos.flush();
                                enviados = enviados + l;
                                porcentaje = (int) ((enviados * 100) / aux.length());
                                System.out.print("\rEnviado el " + porcentaje + " % del archivo");
                            }//while
                            Cliente auxC = new Cliente();
                            auxC.eliminarArchivoCarpeta(aux);
                            System.out.println("\nArchivo enviado..");

                            dis.close();
                            dos.close();
                            cl.close();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } // catch
                break;

                case 3:
                    try {
                    int pto = 8000;
                    String dir = "localhost";
                    System.out.println("Archivos en el servidor");
                    File carpetaServidor = new File("./archivos");
                    System.out.println(carpetaServidor.getAbsolutePath());
                    Cliente cEnlistarS = new Cliente();
                    cEnlistarS.enlistarCarpetas(carpetaServidor, 0);

                    System.out.println("Archivos en la carpeta local del cliente");
                    File carpetaCliente = new File("./cliente");
                    System.out.println(carpetaServidor.getAbsolutePath());
                    Cliente cEnlistarC = new Cliente();
                    cEnlistarC.enlistarCarpetas(carpetaCliente, 0);
                    System.out.println("Conexion con servidor establecida.. lanzando FileChooser..");
                    JFileChooser jf = new JFileChooser("./archivos");
                    jf.setMultiSelectionEnabled(true);
                    jf.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                    int r = jf.showOpenDialog(null);
                    if (r == JFileChooser.APPROVE_OPTION) {
                        File f[] = jf.getSelectedFiles();
                        Cliente cliente = new Cliente();
                        for (int i = 0; i < f.length; i++) {
                            Socket cl = new Socket(dir, pto);
                            DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
                            dos.writeInt(3);
                            dos.flush();
                            dos.writeUTF(f[i].getName());
                            dos.flush();
                            dos.writeLong(f[i].length());
                            dos.flush();
                            dos.writeUTF(f[i].getAbsolutePath());
                            dos.flush();
                            cliente.eliminarArchivoCarpeta(f[i]);
                            dos.close();
                            cl.close();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } // catch
                break;

                case 4:
                    try {
                    int pto = 8000;
                    String dir = "localhost";
                    System.out.println("Archivos en el servidor");
                    File carpetaServidor = new File("./archivos");
                    System.out.println(carpetaServidor.getAbsolutePath());
                    Cliente cEnlistarS = new Cliente();
                    cEnlistarS.enlistarCarpetas(carpetaServidor, 0);

                    System.out.println("Archivos en la carpeta local del cliente");
                    File carpetaCliente = new File("./cliente");
                    System.out.println(carpetaServidor.getAbsolutePath());
                    Cliente cEnlistarC = new Cliente();
                    cEnlistarC.enlistarCarpetas(carpetaCliente, 0);
                    System.out.println("Lanzando FileChooser..");
                    JFileChooser jf1 = new JFileChooser("./cliente");
                    jf1.setMultiSelectionEnabled(true);
                    jf1.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                    int r1 = jf1.showOpenDialog(null);
                    if (r1 == JFileChooser.APPROVE_OPTION) {
                        File f[] = jf1.getSelectedFiles();
                        Cliente cliente = new Cliente();
                        for (int i = 0; i < f.length; i++) {
                            Socket cl = new Socket(dir, pto);
                            DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
                            dos.writeInt(3);
                            dos.flush();
                            dos.writeUTF(f[i].getName());
                            dos.flush();
                            dos.writeLong(f[i].length());
                            dos.flush();
                            dos.writeUTF(f[i].getAbsolutePath());
                            dos.flush();
                            cliente.eliminarArchivoCarpeta(f[i]);
                            dos.close();
                            cl.close();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } // catch
                break;
                case 5:
                    try {
                    int pto = 8000;
                    String dir = "localhost";
                    System.out.println("Archivos en el servidor");
                    File carpetaServidor = new File("./archivos");
                    System.out.println(carpetaServidor.getAbsolutePath());
                    Cliente cEnlistarS = new Cliente();
                    cEnlistarS.enlistarCarpetas(carpetaServidor, 0);

                    System.out.println("Archivos en la carpeta local del cliente");
                    File carpetaCliente = new File("./cliente");
                    System.out.println(carpetaServidor.getAbsolutePath());
                    Cliente cEnlistarC = new Cliente();
                    cEnlistarC.enlistarCarpetas(carpetaCliente, 0);
                    System.out.println("Conexion con servidor establecida.. lanzando FileChooser..");
                    JFileChooser jf2 = new JFileChooser("./archivos");
                    jf2.setMultiSelectionEnabled(true);
                    jf2.requestFocus();
                    int r2 = jf2.showOpenDialog(null);
                    if (r2 == JFileChooser.APPROVE_OPTION) {
                        //JFileChooser jf3 = new JFileChooser();
                        //jf3.requestFocus();
                        //jf3.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        //int r3 = jf3.showOpenDialog(null);
                        //if (r3 == JFileChooser.APPROVE_OPTION) {
                        File f[] = jf2.getSelectedFiles();
                        //File f2 = jf3.getSelectedFile();
                        for (int i = 0; i < f.length; i++) {
                            Socket cl = new Socket(dir, pto);
                            DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
                            DataInputStream dis = new DataInputStream(cl.getInputStream());
                            dos.writeInt(5);
                            dos.flush();
                            dos.writeUTF(f[i].getName());
                            dos.flush();
                            dos.writeLong(f[i].length());
                            dos.flush();
                            dos.writeUTF(f[i].getAbsolutePath());
                            dos.flush();

                            DataOutputStream dos2 = new DataOutputStream(new FileOutputStream("./cliente" + "/" + f[i].getName()));

                            long enviados = 0;
                            int l = 0, porcentaje = 0;
                            while (enviados < f[i].length()) {
                                byte[] b = new byte[1500];
                                l = dis.read(b);
                                System.out.println("enviados: " + l);
                                dos2.write(b, 0, l);
                                dos2.flush();
                                enviados = enviados + l;
                                porcentaje = (int) ((enviados * 100) / f[i].length());
                                System.out.print("\rEnviado el " + porcentaje + " % del archivo");
                            }

                            System.out.println("Se ha recicibido el archivo");

                            dos2.close();
                            dis.close();
                            dos.close();
                            cl.close();
                        }
                        //}
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } // catch
                break;
                case 6:
                    try {
                    int pto = 8000;
                    String dir = "localhost";
                    System.out.println("Archivos en el servidor");
                    File carpetaServidor = new File("./archivos");
                    System.out.println(carpetaServidor.getAbsolutePath());
                    Cliente cEnlistarS = new Cliente();
                    cEnlistarS.enlistarCarpetas(carpetaServidor, 0);

                    System.out.println("Archivos en la carpeta local del cliente");
                    File carpetaCliente = new File("./cliente");
                    System.out.println(carpetaServidor.getAbsolutePath());
                    Cliente cEnlistarC = new Cliente();
                    cEnlistarC.enlistarCarpetas(carpetaCliente, 0);
                    ComprimirCarpeta comp = new ComprimirCarpeta();
                    JFileChooser jf3 = new JFileChooser("./archivos");
                    jf3.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    jf3.setMultiSelectionEnabled(true);
                    jf3.requestFocus();
                    int r3 = jf3.showOpenDialog(null);
                    if (r3 == JFileChooser.APPROVE_OPTION) {
                        //JFileChooser jf2 = new JFileChooser();
                        //jf2.requestFocus();
                        //jf2.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        //int r2 = jf2.showOpenDialog(null);
                        //if (r2 == JFileChooser.APPROVE_OPTION) {
                        File f[] = jf3.getSelectedFiles();
                        //File f2 = jf2.getSelectedFile();
                        for (int i = 0; i < f.length; i++) {
                            String nuevoParent = f[i].getAbsolutePath(); // Ruta donde va a estar el zip
                            String destino = nuevoParent + ".zip";
                            comp.comprimir(nuevoParent, destino);
                            Socket cl = new Socket(dir, pto);
                            File aux = new File(destino);
                            System.out.println("Preparandose pare enviar archivo " + aux.getAbsolutePath() + " de " + aux.length() + " bytes");
                            DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
                            DataInputStream dis = new DataInputStream(cl.getInputStream());
                            dos.writeInt(5);
                            dos.flush();
                            dos.writeUTF(aux.getName());
                            dos.flush();
                            dos.writeLong(aux.length());
                            dos.flush();
                            dos.writeUTF(aux.getAbsolutePath());
                            dos.flush();
                            DataOutputStream dos2 = new DataOutputStream(new FileOutputStream("./cliente" + "/" + aux.getName()));
                            long enviados = 0;
                            int l = 0, porcentaje = 0;
                            while (enviados < aux.length()) {
                                byte[] b = new byte[1500];
                                l = dis.read(b);
                                System.out.println("enviados: " + l);
                                dos2.write(b, 0, l);
                                dos2.flush();
                                enviados = enviados + l;
                                porcentaje = (int) ((enviados * 100) / aux.length());
                                System.out.print("\rEnviado el " + porcentaje + " % del archivo");
                            }
                            System.out.println("Se ha recicibido la carpeta");

                            dos2.close();
                            dis.close();
                            dos.close();
                            cl.close();
                        }
                        //}
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
                case 7:
                    try {
                    int pto = 8000;
                    String dir = "localhost";
                    Socket cl = new Socket(dir, pto);
                    DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
                    dos.writeInt(7);
                    dos.flush();
                    dos.writeUTF("");
                    dos.flush();
                    dos.writeLong(0);
                    dos.flush();
                    dos.writeUTF("");
                    dos.flush();
                    cl.close();
                    salir = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

                default:
                    try {
                    int pto = 8000;
                    String dir = "localhost";
                    Socket cl = new Socket(dir, pto);
                    System.out.println("Opcion no valida, por favor intente de nuevo");
                    DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
                    dos.writeInt(8);
                    dos.flush();
                    dos.writeUTF("");
                    dos.flush();
                    dos.writeLong(0);
                    dos.flush();
                    dos.writeUTF("");
                    dos.flush();
                    cl.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }// switch
        } // while

        cC.close();
    }// main
}
