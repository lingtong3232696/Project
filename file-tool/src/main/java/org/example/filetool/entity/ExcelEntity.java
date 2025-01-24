package org.example.filetool.entity;

import lombok.Data;

import java.io.File;

/**
 * @Description
 * @Author: lingtong
 * @CreateTime: 2025/1/23 星期四 15:06
 */
@Data
public class ExcelEntity {
    public File sourceFile;
    public File targetFile;
    public String sourceFilePath;
    public String targetFilePath;
    public int sourceColumn;
    public int targetColumn;
    public int sourceMatchColumn;
    public int targetMatchColumn;

}
