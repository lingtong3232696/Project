package org.example.filetool.demos.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.filetool.entity.ExcelEntity;
import org.example.filetool.service.ExcelContentCopyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
