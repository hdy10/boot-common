package com.github.hdy.common.entity;

import lombok.Data;

import java.util.Date;

@Data
public class TableInfo {
    private String tableName;
    private String engine;
    private String tableComment;
    private Date createTime;
}
