package org.example.filetool.TooUtil;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class SourceData {
    @ExcelProperty("源列1")
    private String column1;

    @ExcelProperty("源列2")
    private String column2;

    // Getter 和 Setter 方法
    // ...
}