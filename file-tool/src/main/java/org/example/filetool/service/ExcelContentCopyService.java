package org.example.filetool.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.filetool.entity.ExcelEntity;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PreDestroy;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description 两个excel内容复制；例如：excel1的A列复制到excel2的B列；
 * @Author: lingtong
 * @CreateTime: 2025/1/23 星期四 14:14
 *
 */
@Slf4j
@Service("excelContentCopyService")
public class ExcelContentCopyService {

    // 定义在外面 避免多次重复创建线程池
    private final ExecutorService executorService;

    public ExcelContentCopyService() {
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
    }

    /**
     * @Description: 复制excel内容
     * @Author: lingtong
     * @Date: 2025/1/23 14:15
     * @param sourceFile 前台传入
     * @param targetFile 前台传入
     * @param excelEntity 前台传入
     * @return ResponseEntity
     */
    public ResponseEntity<?> copyContent(MultipartFile sourceFile, MultipartFile targetFile, ExcelEntity excelEntity) throws Exception {
        int sourceColumn = excelEntity.getSourceColumn();
        int targetColumn = excelEntity.getTargetColumn();
        int sourceMatchColumn = excelEntity.getSourceMatchColumn();
        int targetMatchColumn = excelEntity.getTargetMatchColumn();

        int availableProcessors = Runtime.getRuntime().availableProcessors();
        // 并行读取源文件和目标文件
        Future<Map<String, String>> sourceDataFuture = executorService.submit(() -> readExcelData(sourceFile, sourceMatchColumn, sourceColumn));
        Future<List<Map<Integer, String>>> targetDataFuture = executorService.submit(() -> readExcelData(targetFile));

        Map<String, String> sourceData = sourceDataFuture.get();
        List<Map<Integer, String>> targetData = targetDataFuture.get();

        // 检查目标文件数据是否为空
        if (targetData.isEmpty()) {
            throw new RuntimeException("目标文件数据为空");
        }

        // 并行处理目标文件的数据
        List<Callable<Void>> tasks = new ArrayList<>();
        int chunkSize = targetData.size() / (availableProcessors * 2); // 动态分块
        for (int i = 0; i < availableProcessors * 2; i++) {
            int start = i * chunkSize;
            int end = (i == availableProcessors * 2 - 1) ? targetData.size() : (i + 1) * chunkSize;
            tasks.add(() -> {
                for (int j = start; j < end; j++) {
                    Map<Integer, String> rowData = targetData.get(j);
                    String key = rowData.get(targetMatchColumn);
                    if (key != null && sourceData.containsKey(key)) {
                        rowData.put(targetColumn, sourceData.get(key));
                    }
                }
                return null;
            });
        }

        executorService.invokeAll(tasks);

        // 将修改后的数据写入新的 Excel 文件
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            // 写入数据
            EasyExcel.write(outputStream)
                    .sheet("Sheet1") // 设置 Sheet 名称
                    .doWrite(targetData); // 写入数据
        } catch (Exception e) {
            throw new RuntimeException("写入目标文件失败", e);
        }

        // 将字节流转换为 InputStreamResource
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        InputStreamResource resource = new InputStreamResource(inputStream);

        //System.out.println("Output Stream Size: " + outputStream.size());

        // 返回文件给前端
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=modified_target.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(outputStream.size()) // 设置文件大小
                .body(resource);
    }
    @PreDestroy
    public void shutdown() {
        executorService.shutdown(); // 关闭线程池
    }
    private Map<String, String> readExcelData(MultipartFile file, int matchColumn, int dataColumn) throws IOException {
        Map<String, String> data = new HashMap<>();
        try (InputStream inputStream = file.getInputStream()) {
            EasyExcel.read(inputStream, new AnalysisEventListener<Map<Integer, String>>() {
                @Override
                public void invoke(Map<Integer, String> rowData, AnalysisContext context) {
                    // 跳过表头
                    if (context.readRowHolder().getRowIndex() == 0) return;

                    String key = rowData.get(matchColumn); // 匹配列
                    String value = rowData.get(dataColumn); // 数据列
                    if (key != null && value != null) {
                        data.put(key, value);
                    }
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {
                    // 读取完成
                    //System.out.println("Data: " + data);
                }
            }).sheet().doRead();
        } catch (IOException e) {
            throw new RuntimeException("读取文件失败", e);
        }
        return data;
    }

    private List<Map<Integer, String>> readExcelData(MultipartFile file) throws IOException {
        List<Map<Integer, String>> data = new ArrayList<>();
        try (InputStream inputStream = file.getInputStream()) {
            EasyExcel.read(inputStream, new AnalysisEventListener<Map<Integer, String>>() {
                @Override
                public void invoke(Map<Integer, String> rowData, AnalysisContext context) {
                    // 跳过表头
                    if (context.readRowHolder().getRowIndex() == 0) return;

                    // 将每一行数据添加到列表中
                    data.add(new HashMap<>(rowData));
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {
                    // 读取完成
                    //System.out.println("Data: " + data);
                }
            }).sheet().doRead();
        } catch (IOException e) {
            throw new RuntimeException("读取文件失败", e);
        }
        return data;
    }

     /*public static void main(String[] args) {
        ExcelContentCopy excelContentCopy = new ExcelContentCopy();
        ExcelEntity excelEntity = new ExcelEntity();
        excelEntity.setSourceFilePath("D:\\document\\1-需求\\档案管理\\数据整理\\历史已扫描\\five\\档案管理查询-主表1017.xlsx");
        excelEntity.setTargetFilePath("D:\\document\\1-需求\\档案管理\\数据整理\\历史已扫描\\five\\Done\\⑨ 元亨400HYZC202100001-211箱 汇银著录汇总表-211箱 - 副本.xlsx");

        excelEntity.setSourceMatchColumn(0);// 源文件合同号比较列 第1列
        excelEntity.setSourceColumn(2);//源列值 第3列

        excelEntity.setTargetColumn(9);//写入列
        excelEntity.setTargetMatchColumn(4);//第5列

        excelContentCopy.copyContent(excelEntity);
    }*/
}
