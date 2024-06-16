package workers.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import workers.rest.service.SocketFileReceiverService;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class RestApplication {

    private final SocketFileReceiverService socketFileReceiverService;

    public RestApplication(SocketFileReceiverService socketFileReceiverService) {
        this.socketFileReceiverService = socketFileReceiverService;
    }

    public static void main(String[] args) {
        SpringApplication.run(RestApplication.class, args);
    }

    @PostConstruct
    public void startSocketServer() {
        new Thread(() -> socketFileReceiverService.startServer()).start();
    }
}
