package workers.rest.controller;

import workers.rest.service.SocketFileReceiverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/files")
public class FileUploadController {

    @Autowired
    private SocketFileReceiverService socketFileReceiverService;

    @PostMapping("/upload/{number}")
    public ResponseEntity<String> uploadFile(@PathVariable("number") String number) {
        try {
            String filePath = socketFileReceiverService.receiveFile(number);
            return new ResponseEntity<>("Archivo subido correctamente: " + filePath, HttpStatus.OK);
        } catch (IOException e) {
            if (e.getMessage().equals("El archivo ya existe")) {
                return new ResponseEntity<>("El archivo ya existe", HttpStatus.CONFLICT);
            } else if (e.getMessage().equals("Tipo de archivo no permitido")) {
                return new ResponseEntity<>("Tipo de archivo no permitido", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
            }
            return new ResponseEntity<>("Error al subir el archivo", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
