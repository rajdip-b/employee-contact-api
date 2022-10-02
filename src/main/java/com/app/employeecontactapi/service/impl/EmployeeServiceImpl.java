package com.app.employeecontactapi.service.impl;

import com.app.employeecontactapi.entity.Employee;
import com.app.employeecontactapi.repository.EmployeeRepository;
import com.app.employeecontactapi.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    Employee persistEmployee(Employee oldEmployee, Employee newEmployee) {
        oldEmployee.setName(newEmployee.getName());
        oldEmployee.setJobTitle(newEmployee.getJobTitle());
        oldEmployee.setPhone(newEmployee.getPhone());
        oldEmployee.setEmail(newEmployee.getEmail());
        oldEmployee.setAddress(newEmployee.getAddress());
        oldEmployee.setCity(newEmployee.getCity());
        oldEmployee.setState(newEmployee.getState());

        Employee primaryEmergencyContact = null, secondaryEmergencyContact = null;

        if (newEmployee.getPrimaryEmergencyContact() != null)
            primaryEmergencyContact = employeeRepository.findByNameAndPhone(
                            newEmployee.getPrimaryEmergencyContact().getName(),
                            newEmployee.getPrimaryEmergencyContact().getPhone())
                    .orElse(null);

        if (newEmployee.getSecondaryEmergencyContact() != null)
            secondaryEmergencyContact = employeeRepository.findByNameAndPhone(
                            newEmployee.getSecondaryEmergencyContact().getName(),
                            newEmployee.getSecondaryEmergencyContact().getPhone())
                    .orElse(null);

        if (primaryEmergencyContact != null) {
            oldEmployee.setPrimaryEmergencyContact(primaryEmergencyContact);
            oldEmployee.setPrimaryEmergencyContactRelation(newEmployee.getPrimaryEmergencyContactRelation());
            primaryEmergencyContact.setPrimaryTo(oldEmployee);
        }
        if (secondaryEmergencyContact != null) {
            oldEmployee.setSecondaryEmergencyContact(secondaryEmergencyContact);
            oldEmployee.setSecondaryEmergencyContactRelation(newEmployee.getSecondaryEmergencyContactRelation());
            secondaryEmergencyContact.setSecondaryTo(oldEmployee);
        }

        return employeeRepository.save(oldEmployee);
    }

    private static List<Employee> removeMapping(List<Employee> employees) {
        return employees.stream().peek(EmployeeServiceImpl::removeMapping).collect(Collectors.toList());
    }

    private static Employee removeMapping(Employee e) {
        var primary = e.getPrimaryEmergencyContact();
        var secondary = e.getSecondaryEmergencyContact();
        var p = new Employee();
        var s = new Employee();
        if (primary != null) {
            p.setName(primary.getName());
            p.setPhone(primary.getPhone());
            e.setPrimaryEmergencyContact(p);
        }
        if (secondary != null) {
            s.setName(secondary.getName());
            s.setPhone(secondary.getPhone());
            e.setSecondaryEmergencyContact(s);
        }
        return e;
    }

    @Override
    @Transactional
    public Long saveEmployee(Employee employee) {
        var e = new Employee();
        return persistEmployee(e, employee).getId();
    }

    @Override
    @Transactional
    public Long updateEmployee(Employee employee) throws Exception {
        var e = findEmployeeByIdWithMapping(employee.getId());
        return persistEmployee(e, employee).getId();
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) throws Exception {
        var employee = findEmployeeByIdWithMapping(id);
        if (employee.getSecondaryTo() != null) {
            employee.getSecondaryTo().setSecondaryEmergencyContact(null);
            employee.getSecondaryTo().setSecondaryEmergencyContactRelation(null);
        }
        if (employee.getPrimaryTo() != null) {
            employee.getPrimaryTo().setPrimaryEmergencyContact(null);
            employee.getPrimaryTo().setPrimaryEmergencyContactRelation(null);
        }
        if (employee.getPrimaryEmergencyContact() != null)
            employee.getPrimaryEmergencyContact().setPrimaryTo(null);
        if (employee.getSecondaryEmergencyContact() != null)
            employee.getSecondaryEmergencyContact().setSecondaryTo(null);

        employeeRepository.delete(employee);
    }

    @Override
    public Employee findEmployeeById(Long id) throws Exception {
        return removeMapping(employeeRepository.findById(id).orElseThrow(() -> new Exception("Employee not found")));
    }

    private Employee findEmployeeByIdWithMapping(Long id) throws Exception {
        return employeeRepository.findById(id).orElseThrow(() -> new Exception("Employee not found"));
    }

    @Override
    public Page<Employee> findAllEmployees(int page, int size) {
        var employees = employeeRepository.findAll(PageRequest.of(page, size, Sort.by("name")));
        return new PageImpl<>(
                removeMapping(employees.getContent()),
                employees.getPageable(),
                employees.getSize()
        );
    }
}
