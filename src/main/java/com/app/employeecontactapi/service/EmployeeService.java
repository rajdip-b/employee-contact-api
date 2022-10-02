package com.app.employeecontactapi.service;

import com.app.employeecontactapi.entity.Employee;
import org.springframework.data.domain.Page;

public interface EmployeeService {

    Long saveEmployee(Employee form);

    Long updateEmployee(Employee form) throws Exception;

    void deleteEmployee(Long id) throws Exception;

    Employee findEmployeeById(Long id) throws Exception;

    Page<Employee> findAllEmployees(int page, int size);

}
