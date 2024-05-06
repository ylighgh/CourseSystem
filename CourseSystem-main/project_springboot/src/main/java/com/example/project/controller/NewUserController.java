package com.example.project.controller;


import com.example.project.common.Result;
import com.example.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/v2/users")
public class NewUserController {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private UserService userService;

    @GetMapping(value = "/list")
    public Result List(@RequestParam(value = "pageSize", required = false) Integer pageSize,
                       @RequestParam(value = "pageNum", required = false) Integer pageNum,
                       @RequestParam(value = "order", required = false) boolean order) {

        List<Map<String, Object>> Users;


        try {
            StringBuilder listString = listStringBuilder(pageSize, pageNum, order);
            Users = jdbcTemplate.queryForList(String.valueOf(listString));

            return Result.suc(Users, Users.size());
        } catch (Exception e) {
            return Result.fail();
        }

    }

    @GetMapping(value = "/get")
    public Result Get(@RequestParam(value = "id", required = false) Integer id,
                      @RequestParam(value = "name", required = false) String name) {

        List<Map<String, Object>> Users;

        try {
            StringBuilder getString = getStringBuilder(id, name);
            Users = jdbcTemplate.queryForList(String.valueOf(getString));

            return Result.suc(Users, Users.size());
        } catch (Exception e) {
            return Result.fail();
        }

    }

    private static StringBuilder getStringBuilder(Integer id, String name) {
        String sql = "SELECT id, name, role\n" +
                "FROM (\n" +
                "    SELECT teachers.id AS id, teachers.name AS name, 'teacher' AS role\n" +
                "    FROM teachers\n" +
                "    UNION ALL\n" +
                "    SELECT students.id AS id, students.name AS name, 'student' AS role\n" +
                "    FROM students\n" +
                ") AS combined_users\n";

        StringBuilder getString = new StringBuilder(sql);

        if (id != null) {
            getString.append(String.format(" where id = %d", id));
        }

        if (name != null) {
            if (id != null){
                getString.append(String.format(" and name = '%s'", name));
            }else {
                getString.append(String.format(" where name = '%s'", name));
            }

        }

        return getString;
    }

    private static StringBuilder listStringBuilder(Integer pageSize, Integer pageNum, boolean order) {
        String sql = "SELECT id, name, role\n" +
                "FROM (\n" +
                "    SELECT teachers.id AS id, teachers.name AS name, 'teacher' AS role\n" +
                "    FROM teachers\n" +
                "    UNION ALL\n" +
                "    SELECT students.id AS id, students.name AS name, 'student' AS role\n" +
                "    FROM students\n" +
                ") AS combined_users\n";

        StringBuilder listString = new StringBuilder(sql);

        listString.append(String.format(" order by id %s limit %d offset %d", order ? "asc" : "desc", pageSize, (pageNum - 1) * pageSize));
        return listString;
    }

}
