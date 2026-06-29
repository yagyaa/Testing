package com.example.springwithreact.employee;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeService {

	private final EmployeeRepository employeeRepository;

	public EmployeeService(EmployeeRepository employeeRepository) {
		this.employeeRepository = employeeRepository;
	}

	@Transactional(readOnly = true)
	public List<Employee> list() {
		return employeeRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Employee get(Long id) {
		return employeeRepository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
	}

	@Transactional
	public Employee create(Employee employee) {
		try {
			return employeeRepository.save(employee);
		} catch (DataIntegrityViolationException ex) {
			throw new IllegalArgumentException("Email must be unique");
		}
	}

	@Transactional
	public Employee update(Long id, Employee update) {
		Employee existing = employeeRepository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
		existing.setFirstName(update.getFirstName());
		existing.setLastName(update.getLastName());
		existing.setEmail(update.getEmail());
		try {
			return employeeRepository.save(existing);
		} catch (DataIntegrityViolationException ex) {
			throw new IllegalArgumentException("Email must be unique");
		}
	}

	@Transactional
	public void delete(Long id) {
		if (!employeeRepository.existsById(id)) {
			throw new EmployeeNotFoundException(id);
		}
		employeeRepository.deleteById(id);
	}
}

