package com.example.springwithreact.employee;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class EmployeeController {

	private final EmployeeService employeeService;

	public EmployeeController(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}

	@GetMapping
	public List<Employee> list() {
		return employeeService.list();
	}

	@GetMapping("/{id}")
	public Employee get(@PathVariable Long id) {
		return employeeService.get(id);
	}

	@PostMapping
	public ResponseEntity<Employee> create(
			@Valid @RequestBody Employee employee,
			UriComponentsBuilder uriComponentsBuilder
	) {
		Employee created = employeeService.create(employee);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@PutMapping("/updateEmployee")
	public Employee update( @Valid @RequestBody Employee employee) {
		return employeeService.update(employee.getId(), employee);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		employeeService.delete(id);
		return ResponseEntity.noContent().build();
	}
}

