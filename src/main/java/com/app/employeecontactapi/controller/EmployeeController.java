package com.app.employeecontactapi.controller;

import com.app.employeecontactapi.entity.Employee;
import com.app.employeecontactapi.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employee")
@Slf4j
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/")
    public ResponseEntity<?> saveEmployee(@RequestBody Employee employee) {
        try {
            return ResponseEntity.status(201).body(employeeService.saveEmployee(employee));
        } catch (Exception e) {
            log.error("Error saving employee", e);
            return ResponseEntity.status(500).body("Error saving employee");
        }
    }

    @PutMapping("/")
    public ResponseEntity<?> updateEmployee(@RequestBody Employee employee) {
        try {
            return ResponseEntity.status(200).body(employeeService.updateEmployee(employee));
        } catch (Exception e) {
            log.error("Error updating employee", e);
            return ResponseEntity.status(500).body("Error updating employee");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        try {
            employeeService.deleteEmployee(id);
            return ResponseEntity.status(200).body("Employee deleted");
        } catch (Exception e) {
            log.error("Error deleting employee", e);
            return ResponseEntity.status(500).body("Error deleting employee");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findEmployeeById(@PathVariable Long id) {
        try {
            return ResponseEntity.status(200).body(employeeService.findEmployeeById(id));
        } catch (Exception e) {
            log.error("Error finding employee", e);
            return ResponseEntity.status(500).body("Error finding employee");
        }
    }

    @GetMapping("/")
    public ResponseEntity<?> findAllEmployees(@RequestParam int page, @RequestParam int size) {
        try {
            return ResponseEntity.status(200).body(employeeService.findAllEmployees(page, size));
        } catch (Exception e) {
            log.error("Error finding employees", e);
            return ResponseEntity.status(500).body("Error finding employees");
        }
    }

}
