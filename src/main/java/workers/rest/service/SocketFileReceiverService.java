package workers.rest.service;

import org.springframework.stereotype.Service;

import java.io.*;
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

    // Método para recibir archivos usando StreamReader y StreamWriter
    public String receiveFile(String number) throws IOException {
        String companyDirPath = rootDir + number;
        File companyDir = new File(companyDirPath);

        if (!companyDir.exists()) {
            companyDir.mkdirs();
        }

        Path filePath = Paths.get(companyDirPath, "received_file");

        try (ServerSocket serverSocket = new ServerSocket(PORT);
             Socket socket = serverSocket.accept();
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter writer = Files.newBufferedWriter(filePath)) {

            // Leer el número de empresa
            String companyNumber = reader.readLine();
            System.out.println("Número de empresa recibido: " + companyNumber);

            // Leer y escribir el archivo
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
        }

        return filePath.toString();
    }

    private static class FileReceiver implements Runnable {
        private final Socket socket;

        public FileReceiver(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                // Leer el número de empresa
                String companyNumber = reader.readLine();
                System.out.println("Número de empresa recibido en hilo secundario: " + companyNumber);

                // Guardar el archivo en una ubicación específica
                String companyDirPath = "C:/uploads/" + companyNumber;
                File companyDir = new File(companyDirPath);

                if (!companyDir.exists()) {
                    companyDir.mkdirs();
                }

                Path filePath = Paths.get(companyDirPath, "received_file");

                try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        writer.write(line);
                        writer.newLine();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
