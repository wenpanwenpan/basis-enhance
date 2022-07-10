package org.enhance.cache.demo.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.enhance.cache.demo.entity.Employee;

public interface EmployeeMapper {

    @Select("SELECT * FROM employee WHERE id = #{id}")
    public Employee getEmpById(Integer id);

    @Insert("INSERT INTO employee(lastName,email,gender,d_id) VALUES(# {lastName},#{email},#{gender},#{dId})")
    void insertEmp(Employee employee);

    @Update("UPDATE employee SET lastName = #{lastName},email = # {email}, gender = #{gender}, d_id = #{dId}WHERE id = #{id}")
    public void updateEmp(Employee employee);

    @Delete("DELETE FROM employee WHERE id = #{id}")
    public void deleteEmpById(Integer id);
}