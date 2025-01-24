package org.example.filetool.demos.web;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.example.filetool.TooUtil.SourceData;
import org.example.filetool.TooUtil.SourceDataListener;
import org.example.filetool.TooUtil.TargetData;
import org.example.filetool.TooUtil.TargetDataListener;
import org.example.filetool.entity.ExcelEntity;
import org.example.filetool.service.ExcelContentCopyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description 文件处理逻辑类
 * @Author: lingtong
 * @CreateTime: 2025/1/23 星期四 14:54
 */
@RestController
@RequestMapping("/logicFile/v1")
public class LogicFileController {

    @Autowired
    private ExcelContentCopyService excelContentCopyService;

    @PostMapping("/excelContentCopy")
    public ResponseEntity<?> excelContentCopy(
            @RequestPart("sourceFile") MultipartFile sourceFile,
            @RequestPart("targetFile") MultipartFile targetFile,
            @RequestPart("excelEntity") String excelEntityJson) throws Exception {
        // 将 JSON 字符串转换为对象
        ExcelEntity excelEntity = new ObjectMapper().readValue(excelEntityJson, ExcelEntity.class);
        return excelContentCopyService.copyContent(sourceFile, targetFile, excelEntity);
    }
}
