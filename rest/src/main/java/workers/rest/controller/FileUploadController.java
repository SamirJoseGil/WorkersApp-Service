package workers.rest.controller;

import workers.rest.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/files")
public class FileUploadController {

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/upload/{number}")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable("number") String number) {
        if (file.isEmpty()) {
            return new ResponseEntity<>("Por favor seleccione un archivo", HttpStatus.BAD_REQUEST);
        }

        try {
            String filePath = fileStorageService.storeFile(file, number);
            return new ResponseEntity<>("Archivo subido correctamente: " + filePath, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error al subir el archivo", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}