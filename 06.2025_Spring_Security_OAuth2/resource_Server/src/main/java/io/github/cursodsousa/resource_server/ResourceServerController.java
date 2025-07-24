package io.github.cursodsousa.resource_server;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResourceServerController {

    @GetMapping("/public")
    public ResponseEntity<String> publicEndPoint() {
        return ResponseEntity.ok("PUBLIC ENDPOINT OK!");
    }

    @GetMapping("/private")
    public ResponseEntity<String> privateEndPoint() {
        return ResponseEntity.ok("PRIVATE ENDPOINT OK!");
    }

}
