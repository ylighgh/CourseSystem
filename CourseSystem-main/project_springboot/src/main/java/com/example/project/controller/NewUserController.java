package com.example.project.controller;


import com.example.project.common.Result;
import com.example.project.entity.NewUser;
import com.example.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.security.MessageDigest;
import java.util.Base64;
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

    @CrossOrigin
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

    @CrossOrigin
    @PostMapping("/upsertUser")
    public Result addUser(@RequestBody NewUser userDTO) {
        try {
            // 更新逻辑
            if (userDTO.getRoleId() == null || userDTO.getRoleId().isEmpty()) {
                String password = userDTO.getId() + userDTO.getPassword(); // 假设用户名在UserDTO中
                String md5Password = md5(password);

                // 更新用户信息
                String updateQuery = "UPDATE `user` SET password = ?, role_id = ? WHERE id = ?";
                jdbcTemplate.update(updateQuery, md5Password, userDTO.getRoleId(), userDTO.getId());

                // 更新完毕
                return Result.suc();
                // 插入逻辑
            } else {
                // 插入用户信息到user表中
                int maxId = jdbcTemplate.queryForObject("SELECT MAX(id) FROM `user`", Integer.class);
                int userId = maxId + 1;

                // 插入用户信息到user表中
                String password = userId + userDTO.getPassword(); // 假设用户名在UserDTO中
                String md5Password = md5(password);

                String insertUserQuery = "INSERT INTO `user` (id, password, role_id) VALUES (?, ?, ?)";
                jdbcTemplate.update(insertUserQuery, userId, md5Password, userDTO.getRoleId());

                // 根据角色插入相应的用户信息到对应的表中
                if (userDTO.getRoleId().equals("1")) {
                    String insertStudentQuery = "INSERT INTO students (id, name, yxh) VALUES (?, ?, ?)";
                    jdbcTemplate.update(insertStudentQuery, userId, userDTO.getName(), userDTO.getYxh());
                } else if (userDTO.getRoleId().equals("2")) {
                    String insertTeacherQuery = "INSERT INTO teachers (id, name, yxh) VALUES (?, ?, ?)";
                    jdbcTemplate.update(insertTeacherQuery, userId, userDTO.getName(), userDTO.getYxh());
                }

                return Result.suc();
            }
        } catch (Exception e) {
            System.out.println(e);
            return Result.fail();
        }
    }

    @CrossOrigin
    @GetMapping("/deleteUser/{id}")
    public Result deleteUser(@PathVariable int id) {
        try {
            // 删除用户表中的对应记录
            String deleteUserQuery1 = "DELETE FROM `selected_courses` WHERE student_id = ?";
            jdbcTemplate.update(deleteUserQuery1, id);

            String deleteUserQuery = "DELETE FROM `user` WHERE id = ?";
            jdbcTemplate.update(deleteUserQuery, id);

            // 删除学生表中的对应记录
            String deleteStudentQuery = "DELETE FROM students WHERE id = ?";
            jdbcTemplate.update(deleteStudentQuery, id);

            // 删除教师表中的对应记录
            String deleteTeacherQuery = "DELETE FROM teachers WHERE id = ?";
            jdbcTemplate.update(deleteTeacherQuery, id);

            return Result.suc();
        } catch (Exception e) {
            System.out.println(e);
            return Result.fail();
        }
    }

    @CrossOrigin
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
                "    SELECT teachers.id AS id, teachers.name AS name, '老师' AS role\n" +
                "    FROM teachers\n" +
                "    UNION ALL\n" +
                "    SELECT students.id AS id, students.name AS name, '学生' AS role\n" +
                "    FROM students\n" +
                ") AS combined_users\n";

        StringBuilder getString = new StringBuilder(sql);

        if (id != null) {
            getString.append(String.format(" where id = %d", id));
        }

        if (name != null) {
            if (id != null) {
                getString.append(String.format(" and name = '%s'", name));
            } else {
                getString.append(String.format(" where name = '%s'", name));
            }

        }

        return getString;
    }

    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("MD5 calculation error", e);
        }
    }

    private static StringBuilder listStringBuilder(Integer pageSize, Integer pageNum, boolean order) {
        String sql = "SELECT id, name, role\n" +
                "FROM (\n" +
                "    SELECT teachers.id AS id, teachers.name AS name, '老师' AS role\n" +
                "    FROM teachers\n" +
                "    UNION ALL\n" +
                "    SELECT students.id AS id, students.name AS name, '学生' AS role\n" +
                "    FROM students\n" +
                ") AS combined_users\n";

        StringBuilder listString = new StringBuilder(sql);

        listString.append(String.format(" order by id %s limit %d offset %d", order ? "asc" : "desc", pageSize, (pageNum - 1) * pageSize));
        return listString;
    }

}
