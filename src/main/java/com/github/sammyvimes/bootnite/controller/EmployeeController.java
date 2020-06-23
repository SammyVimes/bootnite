package com.github.sammyvimes.bootnite.controller;

import com.github.sammyvimes.bootnite.model.EmployeeDTO;
import com.github.sammyvimes.bootnite.service.TestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    private final TestService service;

    public EmployeeController(final TestService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> employees() {
        return ResponseEntity.ok(service.findAll());
    }

    @PostMapping
    public ResponseEntity<EmployeeDTO> create(@RequestParam(name = "name") final String name) {
        return ResponseEntity.ok(service.create(name));
    }

}
