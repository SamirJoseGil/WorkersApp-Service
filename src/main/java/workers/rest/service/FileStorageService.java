package workers.rest.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageService {

    private final String rootDir = "C:/uploads/";
    private final String[] allowedExtensions = { ".rar", ".zip" };

    public String storeFile(MultipartFile file, String number) throws IOException {
        validateFileType(file);

        String companyDirPath = rootDir + number;
        File companyDir = new File(companyDirPath);

        if (!companyDir.exists()) {
            companyDir.mkdirs();
        }

        Path filePath = Paths.get(companyDirPath, file.getOriginalFilename());

        if (Files.exists(filePath)) {
            throw new IOException("El archivo ya existe");
        }

        Files.copy(file.getInputStream(), filePath);
        return filePath.toString();
    }

    private void validateFileType(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        if (fileName == null || !isAllowedFileType(fileName)) {
            throw new IOException("Tipo de archivo no permitido");
        }
    }

    private boolean isAllowedFileType(String fileName) {
        for (String extension : allowedExtensions) {
            if (fileName.toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }
}
