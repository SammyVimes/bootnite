package com.github.sammyvimes.bootnite.service;

import com.github.sammyvimes.bootnite.model.Employee;
import com.github.sammyvimes.bootnite.repo.EmployeeRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Transactional
public class TestService {

	private final EmployeeRepository repository;

	public TestService(final EmployeeRepository repository) {
		this.repository = repository;
	}

	public List<Employee> findAll() {
		return StreamSupport.stream(repository.findAll().spliterator(), false)
				.collect(Collectors.toList());
	}

	public Employee create(final String name) {
		final Employee employee = new Employee();
		final UUID id = UUID.randomUUID();
		employee.setId(id);
		employee.setEmployed(true);
		employee.setName(name);
		return repository.save(id, employee);
	}
}
