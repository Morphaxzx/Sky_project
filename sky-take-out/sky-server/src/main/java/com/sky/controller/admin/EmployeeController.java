package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关接口")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */

    @ApiOperation("员工登录方法")
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }


    @ApiOperation("员工退出方法")
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }



    @ApiOperation("新增员工方法")
    @PostMapping
    public Result AddEmp(@RequestBody EmployeeDTO employeeDTO)
    {
        employeeService.add(employeeDTO);
        return Result.success();
    }

    @ApiOperation("分页查询员工")
    @GetMapping("/page")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO)
    {
        PageResult  re = employeeService.page(employeePageQueryDTO);
        return Result.success(re);
    }

    @ApiOperation("启用/禁用员工账号")
    @PostMapping("/status/{status}")
    public Result StartOrStop(@PathVariable Integer status,Long id)
    {
        employeeService.ChangeStatus(status,id);
        return Result.success();
    }


    @ApiOperation("根据id查询员工")
    @GetMapping ("/{id}")
    public Result<Employee> GetByID(@PathVariable Long id)
    {
        Employee e = employeeService.getByID(id);
        return Result.success(e);
    }

    @ApiOperation("修改员工信息")
    @PutMapping
    public Result update(@RequestBody EmployeeDTO employeeDTO)
    {
        employeeService.update(employeeDTO);
        return Result.success();
    }


}
