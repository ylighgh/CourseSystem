package com.example.project.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewUser {
    public String id;
    public String password;
    public String roleId; // 角色ID，可以是1 2
    public String name;
    public String yxh; // 学院号
}

