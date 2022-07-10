package org.enhance.cache.demo.service;

import org.enhance.cache.demo.entity.Employee;
import org.enhance.cache.demo.mapper.EmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * 使用@CacheConfig抽取公共配置
 */
@Service
@CacheConfig(cacheNames = "emp")
public class EmployeeService {
    @Autowired
    EmployeeMapper employeeMapper;

    @Cacheable(key = "#id"/*, condition = "#result != null && #result.id == 2"*/)
    public Employee getEmpById(Integer id) {
        return employeeMapper.getEmpById(id);
    }

    @CachePut(key = "#employee.id")
    public Employee updateEmp(Employee employee) {
        employeeMapper.updateEmp(employee);
        return employee;
    }

    @CacheEvict(key = "#id", beforeInvocation = true)
    public void delEmp(Integer id) {
        employeeMapper.deleteEmpById(id);
    }
}