package org.example.filetool.TooUtil;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class TargetData {
    @ExcelProperty("目标列1")
    private String column1;

    @ExcelProperty("目标列2")
    private String column2;

    // Getter 和 Setter 方法
    // ...
}