package com.github.hdy.common.util;

import lombok.Data;

/**
 * @author hdy
 * @since 2020/2/27
 */
@Data
public class Files {
    private String originalFileName;    // 原文件名称
    private String fileName;            // 储存文件名称
    private long size;                  // 文件大小
    private String suffix;              // 文件类型
    private int index;                  // 第几个文件
}
