package com.alibou.security.contoller;

import com.alibou.security.dto.ClientDto;
import com.alibou.security.dto.UserClientDto;
import com.alibou.security.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/clients")
@RequiredArgsConstructor
public class ClientController {
    private final ClientService clientService;

    @PostMapping("/create")
    public ResponseEntity<ClientDto> create(@RequestBody ClientDto clientDto) {
        return ResponseEntity.ok(clientService.create(clientDto));
    }
    @PostMapping("/user/create")
    public ResponseEntity<ClientDto> create(@RequestBody UserClientDto userClientDto, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(clientService.create(userClientDto, userDetails));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ClientDto> findById(@PathVariable("id") Integer id){
        Optional<ClientDto> clientDto = clientService.findById(id);
        return clientDto.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    @GetMapping
    public ResponseEntity<List<ClientDto>> findAll(){
        List<ClientDto> clientDtoList = clientService.findAll();
        return ResponseEntity.ok(clientDtoList);
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<ClientDto> update(@PathVariable("id") Integer id, @RequestBody ClientDto clientDto){
        return ResponseEntity.ok(clientService.update(id, clientDto));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable("id") Integer id){
        return ResponseEntity.ok(clientService.deleteById(id));
    }
}