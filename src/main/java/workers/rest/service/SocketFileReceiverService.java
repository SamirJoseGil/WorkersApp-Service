package workers.rest.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class SocketFileReceiverService {

    private final String rootDir = "C:/uploads/";
    private final int PORT = 5000;

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor escuchando en el puerto " + PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new FileReceiver(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String receiveFile(String number) throws IOException {
        String companyDirPath = rootDir + number;
        File companyDir = new File(companyDirPath);

        if (!companyDir.exists()) {
            companyDir.mkdirs();
        }

        Path filePath = Paths.get(companyDirPath, "received_file");

        try (Socket socket = new ServerSocket(PORT).accept();
             FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = socket.getInputStream().read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }

            if (!isAllowedFileType(filePath.toString())) {
                Files.delete(filePath);
                throw new IOException("Tipo de archivo no permitido");
            }
        } catch (IOException e) {
            throw e;
        }

        return filePath.toString();
    }

    private boolean isAllowedFileType(String filePath) {
        String[] allowedExtensions = {".rar", ".zip"};
        for (String extension : allowedExtensions) {
            if (filePath.toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    private class FileReceiver implements Runnable {
        private Socket socket;

        public FileReceiver(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                String number = "default";
                String companyDirPath = rootDir + number;
                File companyDir = new File(companyDirPath);

                if (!companyDir.exists()) {
                    companyDir.mkdirs();
                }

                Path filePath = Paths.get(companyDirPath, "received_file");

                try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;

                    while ((bytesRead = socket.getInputStream().read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                } finally {
                    socket.close();
                }

                if (!isAllowedFileType(filePath.toString())) {
                    Files.delete(filePath);
                    throw new IOException("Tipo de archivo no permitido");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
