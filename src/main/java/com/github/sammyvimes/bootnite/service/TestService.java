package com.github.sammyvimes.bootnite.service;

import com.github.sammyvimes.bootnite.model.EmployeeDTO;
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

	public List<EmployeeDTO> findAll() {
		return StreamSupport.stream(repository.findAll().spliterator(), false)
				.collect(Collectors.toList());
	}

	public EmployeeDTO create(final String name) {
		final EmployeeDTO employeeDTO = new EmployeeDTO();
		final UUID id = UUID.randomUUID();
		employeeDTO.setId(id);
		employeeDTO.setEmployed(true);
		employeeDTO.setName(name);
		return repository.save(id, employeeDTO);
	}
}
