package servidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.Socket;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class ComprimirCarpeta {

    public void agregarCarpeta(String ruta, String carpeta, ZipOutputStream zip) {
        try {
            File directorio = new File(carpeta);
            for (String nombreArchivo : directorio.list()) {
                if (ruta.equals("")) {
                    agregarArchivo(directorio.getName(), carpeta + "/" + nombreArchivo, zip);
                } else {
                    agregarArchivo(ruta + "/" + directorio.getName(), carpeta + "/" + nombreArchivo, zip);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void agregarArchivo(String ruta, String directorio, ZipOutputStream zip) {
        try {
            File archivo = new File(directorio);
            if (archivo.isDirectory()) {
                agregarCarpeta(ruta, directorio, zip);
            } else {
                byte[] buffer = new byte[4096];
                int leido;
                FileInputStream entrada = new FileInputStream(archivo);
                zip.putNextEntry(new ZipEntry(ruta + "/" + archivo.getName()));
                while ((leido = entrada.read(buffer)) > 0) {
                    zip.write(buffer, 0, leido);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void comprimir(String archivo, String archivoZIP) {
        try {
            ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(archivoZIP));
            agregarCarpeta("", archivo, zip);
            zip.flush();
            zip.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        int pto = 8000;
        String dir = "localhost";
        String nombre = "";
        String path = "";
        long tam = 0;
        ComprimirCarpeta comp = new ComprimirCarpeta();
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jfc.setMultiSelectionEnabled(true);
        int r = jfc.showOpenDialog(jfc);
        if (r == JFileChooser.APPROVE_OPTION) {
            File f[] = jfc.getSelectedFiles();
            for (int i = 0; i <= f.length; i++) {
                File archivoSeleccionado[] = jfc.getSelectedFiles();
//                    nombre = f[i].getName();
//                    path = f[i].getAbsolutePath();
//                    tam = f[i].length();
                String nuevoParent = archivoSeleccionado[i].getAbsolutePath(); //Ruta donde va a estar el zip
                JOptionPane.showMessageDialog(null, "CARPETA SELECCIONADA -> " + nuevoParent);
                String destino = nuevoParent + ".zip";
                JOptionPane.showMessageDialog(null, "DESTINO -> " + destino);
                JOptionPane.showMessageDialog(null, "Comprimiendo...");
                comp.comprimir(nuevoParent, destino);
                JOptionPane.showMessageDialog(null, "Archivo ZIP creado correctamente !");

                Socket cl = new Socket(dir, pto);
                File arch = new File(destino);
                nombre = arch.getName();
                path = arch.getAbsolutePath();
                tam = arch.length();
                System.out.println("Preparandose pare enviar archivo " + path + " de " + tam + " bytes");
                DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
                DataInputStream dis = new DataInputStream(new FileInputStream(path));
                dos.writeUTF(nombre);
                dos.flush();
                dos.writeLong(tam);
                dos.flush();
                long enviados = 0;
                int l = 0, porcentaje = 0;
                while (enviados < tam) {
                    byte[] b = new byte[1500];
                    l = dis.read(b);
                    System.out.println("enviados: " + l);
                    dos.write(b, 0, l);
                    dos.flush();
                    enviados = enviados + l;
                    porcentaje = (int) ((enviados * 100) / tam);
                    //System.out.print("\rEnviado el "+porcentaje+" % del archivo");
                }//while
                System.out.println("\nArchivo enviado..");
                dis.close();
                dos.close();
                cl.close();
            }
        }
    }
}
