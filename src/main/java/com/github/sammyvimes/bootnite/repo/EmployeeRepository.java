package com.github.sammyvimes.bootnite.repo;

import com.github.sammyvimes.bootnite.model.Employee;
import org.apache.ignite.springdata22.repository.IgniteRepository;
import org.apache.ignite.springdata22.repository.config.RepositoryConfig;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RepositoryConfig(cacheName = "employeeCache")
public interface EmployeeRepository
		extends IgniteRepository<Employee, UUID> {
	Employee getEmployeeDTOById(UUID id);
}
