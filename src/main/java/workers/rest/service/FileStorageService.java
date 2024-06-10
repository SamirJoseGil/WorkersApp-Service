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

    private final String rootDir = "C:/uploads";

    public String storeFile(MultipartFile file, String companyNumber) throws IOException {
        String companyDirPath = rootDir + "/" + companyNumber;
        File companyDir = new File(companyDirPath);
        
        if (!companyDir.exists()) {
            companyDir.mkdirs();
        }

        Path filePath = Paths.get(companyDirPath, file.getOriginalFilename());
        if (Files.exists(filePath)) {
            throw new IOException("File already exists");
        }

        Files.copy(file.getInputStream(), filePath);
        return filePath.toString();
    }
}
