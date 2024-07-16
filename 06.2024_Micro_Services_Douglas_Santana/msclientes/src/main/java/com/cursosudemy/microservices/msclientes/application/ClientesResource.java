package com.cursosudemy.microservices.msclientes.application;

import com.cursosudemy.microservices.msclientes.application.representation.ClienteSaveRequest;
import com.cursosudemy.microservices.msclientes.domain.Cliente;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("clientes")
@RequiredArgsConstructor
public class ClientesResource {

    private final ClienteService service;

    @GetMapping
    private String status() {
        return "ok";
    }

    @PostMapping
    public ResponseEntity save(@RequestBody ClienteSaveRequest request){
        Cliente cliente = request.toModel();
        service.save(cliente);
        // return the URI to query Client by Cpf
        URI headerLoction = ServletUriComponentsBuilder
                .fromCurrentRequest() //http://localhost ...
                .query("cpf={cpf}")
                .buildAndExpand(cliente.getCpf())
                .toUri();

        return ResponseEntity.created(headerLoction).build();
    }

    @GetMapping(params = "cpf")
    public ResponseEntity dadosCliente(@RequestParam("cpf") String cpf) {
        Optional<Cliente> cliente = service.getByCpf(cpf);
        if(cliente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cliente);
    }

}
