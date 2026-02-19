package org.example;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;

public class FTPClient_4_2 {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("ERROR: indicar como parámetros:");
            System.out.println("servidor usuario(opcional) contraseña(opcional)");
            System.exit(1);
        }
        String servidorFTP = args[0];
        String usuario = "", password = "";

        if (args.length >= 2) {
            usuario = args[1];
        }

        if (args.length >= 3) {
            password = args[2];
        }

        FTPClient clienteFTP = new FTPClient();

        try {
            clienteFTP.connect(servidorFTP, 21);
            int codResp = clienteFTP.getReplyCode();

            if (!FTPReply.isPositiveCompletion(codResp)) {
                System.out.printf("ERROR: Conexión rechazada con código de respuesta %d.\n", codResp);
                System.exit(2);
            }

            clienteFTP.enterLocalPassiveMode();
            clienteFTP.setFileType(FTP.BINARY_FILE_TYPE);

            if (usuario != null && password != null) {
                boolean loginOK = clienteFTP.login(usuario, password);

                if (loginOK) {
                    System.out.printf("INFO: Login con usuario %s realizado.\n", usuario);
                } else {
                    System.out.printf("ERROR: Login con usuario %s rechazado.\n", usuario);
                    return;
                }
            }
            System.out.printf("INFO: Conexión establecida, mensaje de bienvenida del servidor:\n====\n%s\n====\n", clienteFTP.getReplyString());

            String archivo = "desdeElPrograma.txt";

            clienteFTP.mkd("desdeJava");
            clienteFTP.cwd("desdeJava");

            String texto = "texto";
            InputStream inputStream = new ByteArrayInputStream(texto.getBytes());
            clienteFTP.storeFile(archivo, inputStream);
            inputStream.close();

            InputStream inputStreamReader = clienteFTP.retrieveFileStream(archivo);

            if (inputStreamReader != null) {
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(inputStreamReader)
                );

                String linea;
                System.out.printf("%nContenido archivo: %s%n", archivo);
                while((linea = bufferedReader.readLine()) != null) {
                    System.out.println(linea);
                }

                bufferedReader.close();
                inputStreamReader.close();
            }

            /*
            Imprescindible para el funcionamiento del cliente FTP porque permite cerrar
            la conexión de datos creada al usar el comando retrieveFileStream()
             */
            clienteFTP.completePendingCommand();

            System.out.printf("INFO: Directorio actual en servidor: %s, contenidos:\n", clienteFTP.printWorkingDirectory());

            FTPFile[] fichServ = clienteFTP.listFiles();

            for (FTPFile f : fichServ) {
                String infoAdicFich = "";
                if (f.getType() == FTPFile.DIRECTORY_TYPE) {
                    infoAdicFich = "/";
                } else if (f.getType() == FTPFile.SYMBOLIC_LINK_TYPE) {
                    infoAdicFich = " -> " + f.getLink();
                }
                System.out.printf("%s%s\n", f.getName(), infoAdicFich);
            }

            clienteFTP.cwd("/");

            System.out.printf("INFO: Directorio actual en servidor: %s, contenidos:\n", clienteFTP.printWorkingDirectory());

            FTPFile[] fichServ2 = clienteFTP.listFiles();

            for (FTPFile f : fichServ2) {
                String infoAdicFich = "";
                if (f.getType() == FTPFile.DIRECTORY_TYPE) {
                    infoAdicFich = "/";
                } else if (f.getType() == FTPFile.SYMBOLIC_LINK_TYPE) {
                    infoAdicFich = " -> " + f.getLink();
                }
                System.out.printf("%s%s\n", f.getName(), infoAdicFich);
            }

        } catch (IOException e) {
            System.out.println("ERROR: conectando al servidor");
            e.printStackTrace();
            return;
        } finally {
            if (clienteFTP != null) {
                try {
                    clienteFTP.disconnect();
                    System.out.println("INFO: conexión cerrada.");
                } catch (IOException e) {
                    System.out.println("AVISO: no se pudo cerrar la conexión.");
                }
            }
        }
    }
}