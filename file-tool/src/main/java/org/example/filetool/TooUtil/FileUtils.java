package org.example.filetool.TooUtil;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.parser.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class FileUtils {
    //System.setProperty("itext.licensekey.disableagplwarning", "true"); // 禁用itext的警告信息

    private static int count = 0;
    private static final List<String> IMAGE_EXTENSIONS = Arrays.asList(".tif", ".jpg", ".jpeg", ".png", ".gif", ".bmp"); // 支持的图片格式
    private static final List<String> EXCEL_EXTENSIONS = Arrays.asList(".xls", ".xlsx"); // 支持的excel格式
    private static final int WAIT_TIME = 60;

    /**
     * 获取指定路径下的所有文件
     * @param filePath 路径
     * @param fileType 文件类型
     * @return
     */
    public static List<String> getPathAllFiles(String filePath, String fileType) {
        List<String> filePaths = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("该目录不存在");
            System.exit(0);
        }
        File[] files = file.listFiles(((dir, name) -> name.toLowerCase().endsWith(fileType)));
        if (files != null) {
            for (File f : files) {
                String excelFilePath = f.getAbsolutePath();
                filePaths.add(excelFilePath);
            }
            System.out.println("该目录下" + fileType + "文件数量：" + filePaths.size() + "个");
        }else {
            System.out.println("该目录下不存在" + fileType + "文件");
            System.exit(0);
        }
        return filePaths;
    }

    // 将图片转换为PDF文件
    private static boolean generatePdfFile(File file) throws Exception {
        try {
            String fileName = file.getAbsolutePath();
            String pdfFileName = fileName.substring(0, fileName.lastIndexOf(".")) + ".pdf";
            Document doc = new Document(PageSize.A4, 20, 20, 20, 20);
            PdfWriter.getInstance(doc, new FileOutputStream(pdfFileName));
            doc.open();
            doc.newPage();
            Image image = Image.getInstance(file.getPath());
            float height = image.getHeight();
            float width = image.getWidth();
            int percent = getPercent(height, width);
            image.setAlignment(Image.MIDDLE);
            image.scalePercent(percent);
            doc.add(image);
            doc.close();
            File pdfFile = new File(pdfFileName);
            count++;
            System.out.println("File created: " + pdfFile.getAbsolutePath() + "，已转" + count + "个");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    private static int getPercent(float height, float weight) {
        float percent = 0.0F;
        if (height > weight) {
            percent = PageSize.A4.getHeight() / height * 100;
        } else {
            percent = PageSize.A4.getWidth() / weight * 100;
        }
        return Math.round(percent);
    }

    /**
     * 生成txt文件记录
     * @param tableName 文件名
     * @param content 内容
     */
    public static void setFileRecord(String tableName, String content) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            // 创建FileWriter对象，第二个参数true表示追加模式
            FileWriter writer = new FileWriter(tableName + "-" + sdf.format(new Date()) + ".txt", true);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //图像文件没有后文件更名
    public static void renameImgEndsWith(File file) {
        String absolutePath = file.getAbsolutePath();
        // 处理新路径
        String newPath = "";
        if (!absolutePath.endsWith(".jpg")) {
            if (!absolutePath.contains(".")) {
                newPath = absolutePath + ".jpg";
            }
        } else {
            newPath = absolutePath;
        }
        // 创建新的文件对象用于重命名
        File newFile = new File(newPath);
        // 重命名操作
        if (file.renameTo(newFile)) {
            System.out.println("文件重命名:" + file.getName() + "\tTo\t" + newFile.getName());
        } else {
            System.out.println("文件重命名失败:" + file.getAbsolutePath());
        }
    }

    //多线程
    public static void threadPool(List<File> excelFilePaths, ExecutorService executor) {
        AtomicInteger count = new AtomicInteger(0);
        AtomicInteger count2 = new AtomicInteger(0);
        AtomicInteger count3 = new AtomicInteger(0);
        List<Callable<Void>> tasks = new ArrayList<>();
        // 将每个Excel文件的转换任务添加到列表中
        for (File file : excelFilePaths) {
            tasks.add(() -> {
                // 在这里实现img到PDF的转换逻辑
                boolean result = generatePdfFile(file);
                int currentCount = count.incrementAndGet(); // 线程安全地增加计数
                if (result) {
                    int currentCount2 = count2.incrementAndGet();
                    setFileRecord("imgToPdf-success", "\n\t文件已转换成功：" + file.getAbsolutePath() + "，已转" + currentCount2 + "个");
                    System.out.println("\n\t转换成功：" + file.getAbsolutePath() + "，累计" + currentCount2 + "个");
                } else {
                    int currentCount3 = count3.incrementAndGet();
                    setFileRecord("imgToPdf-error", "\n\t文件已转换失败：" + file.getAbsolutePath() + "，已转" + currentCount3 + "个");
                    System.out.println("\n\t转换失败：" + file.getAbsolutePath() + "，累计" + currentCount3 + "个");
                }
                System.out.println("总共" + currentCount + "个文件");

                return null;
            });
        }
        try {
            // 提交所有任务到线程池，并等待它们完成
            executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            System.out.println("在等待任务完成时被中断");
            Thread.currentThread().interrupt(); //重新设置中断标志
        } finally {
            shutdownExecutor(executor);
        }
    }

    private static void shutdownExecutor(ExecutorService executor) {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(WAIT_TIME, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                if (!executor.awaitTermination(WAIT_TIME, TimeUnit.SECONDS))
                    System.out.println("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            executor.shutdownNow();
            Thread.currentThread().interrupt(); // 重新设置中断标志
        }
    }

    //设置中文
    public static Font setTextFont() {
        // 创建支持中文的字体实例
        BaseFont baseFont = null;
        try {
            baseFont = BaseFont.createFont("C:\\Windows\\Fonts\\simsun.ttc,1", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Font font = new Font(baseFont, 6, Font.NORMAL); // 使用支持中文的字体
        return font;
    }

    //移动子目录名为15位的到目标目录
    public static void moveSubDirectories(String sourceDirectoryPath, String destinationDirectoryPath) {
        Path sourceDir = Paths.get(sourceDirectoryPath);
        Path targetDir = Paths.get(destinationDirectoryPath);

        try (Stream<Path> paths = Files.walk(sourceDir)) {
            paths.filter(Files::isDirectory)
                    .filter(p -> p.getFileName().toString().length() == 15)
                    .forEach(p -> {
                        Path relative = sourceDir.relativize(p);
                        Path target = targetDir.resolve(relative);
                        try {
                            Files.createDirectories(target);
                            Files.walk(p)
                                    .forEach(sourcePath -> {
                                        Path targetPath = target.resolve(sourceDir.relativize(sourcePath));
                                        try {
                                            if (Files.isDirectory(sourcePath)) {
                                                Files.createDirectory(targetPath);
                                            } else {
                                                Files.move(sourcePath, targetPath);
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    });
                            Files.delete(p);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //简单多线程
    public static void performTask() {
        final AtomicInteger currentCount = new AtomicInteger(0); //可放在类全局变量中
        int coreCount = Runtime.getRuntime().availableProcessors();
        int executeCount =  coreCount*2; // 根据实际情况调整
        // 创建线程池
        ExecutorService executor = Executors.newFixedThreadPool(executeCount);

        // 提交一个异步任务
        executor.submit(new Runnable() {
            @Override
            public void run() {
                // 在这里执行异步任务
                //zhixing();
                //方法里加入计数 currentCount.incrementAndGet();
                //总次数：currentCount.get();


                // 模拟耗时操作
                /*try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                System.out.println("异步任务完成");
            }
        });

        // 原始方法继续执行，不会等待异步任务完成
        //System.out.println("原始方法继续执行...");

        // 关闭线程池
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取content中第一个startStr、endStr之间出现的内容
     * @param content 内容
     * @param startStr 前缀
     * @param endStr 后缀
     */
    public static String getContentBetweenStrings(String content, String startStr, String endStr) {
        Pattern pattern = Pattern.compile(startStr + "(.*?)" + endStr);  //例：尊敬的XX客户  str1="尊敬的",str2="客户" 可以读取到"XX"
        Matcher matcher = pattern.matcher(content);
        String result = "";
        if (matcher.find()) {
            result = matcher.group(1);
            System.out.println("读取到内容："+result);
        } else {
            System.out.println("未找到符合条件的子串");
        }
        return result;
    }

    /**
     * 从文本中指定内容，例：提取“尊敬的XX客户”中的“XX”
     */
    public static String extractTargetContent(String text, String startStr, String endStr) {
        int startIndex = text.indexOf(startStr);
        if (startIndex == -1) {
            System.out.println("未找到前缀");
            return null; // 未找到前缀
        }
        startIndex += startStr.length();

        int endIndex = text.indexOf(endStr, startIndex);
        if (endIndex == -1) {
            System.out.println("未找到后缀");
            return null; // 未找到后缀
        }

        // 提取“XX”部分
        String targetContent = text.substring(startIndex, endIndex).trim();
        return targetContent.isEmpty() ? null : targetContent;
    }

    /**
     * 重命名文件
     * @param old_filePath 原文件路径
     * @param new_filePath 新文件路径
     */
    public static void renameFile_RenameTo(String old_filePath, String new_filePath) throws IOException {
        File oldFile = new File(old_filePath);
        File newFile = new File(new_filePath);
        boolean renamed = false;
        if (oldFile.exists()) {
            renamed = oldFile.renameTo(newFile);
            if (renamed) {
                System.out.println("目录 " + old_filePath + " 重命名为 " + new_filePath + " 成功");
            } else {
                System.out.println("目录 " + old_filePath + " 重命名为 " + new_filePath + "失败");
            }


        } else {
            System.out.println("文件不存在!");
        }
        //return renamed;
    }
    //重命名文件
    public static void renameFile_Move(String old_filePath, String new_filePath) throws IOException {
        Path old = Paths.get(old_filePath);
        Path new_l = Paths.get(new_filePath);
        Files.move(old, new_l, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("文件重命名成功!");
    }

    //移动文件
    public static void moveFile(String sourceFilePath, String destinationFilePath) {
        File sourceFile = new File(sourceFilePath);
        File destinationFile = new File(destinationFilePath);

        try {
            Files.move(sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println(sourceFilePath + " \tTo\t " + destinationFilePath +" 文件移动成功！");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("文件移动失败！");
        }
    }

    //读取PDF文件内容
    public static String getPDFContent(String filePath) throws IOException {
        PdfReader reader = new PdfReader(filePath);
        int numPages = reader.getNumberOfPages();

        // 定义一个矩形区域，左下角坐标为 (x, y)，宽度为 width，高度为 height; 该大小会导致影响content值，可理解为想要读取的区域
        Rectangle rect = new Rectangle(100, 750, 300, 50);
        RegionTextRenderFilter regionFilter = new RegionTextRenderFilter(rect);
        TextExtractionStrategy strategy = new FilteredTextRenderListener(new LocationTextExtractionStrategy(), regionFilter);

        String content = "";
        for (int i = 1; i <= numPages; i++) {
            String regionContent = PdfTextExtractor.getTextFromPage(reader, i, strategy);
            //System.out.println("regionContent: " + regionContent);
            content = regionContent;
        }
        reader.close();
        return content;
    }

    /**
     * 根据单元格的数据类型，获取单元格的值并转换为字符串
     */
    public static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                // 如果是数字，判断是否为整数
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    double numericValue = cell.getNumericCellValue();
                    // 判断是否为整数
                    if (numericValue == (long) numericValue) {
                        return String.valueOf((long) numericValue); // 返回整数部分
                    } else {
                        return String.valueOf(numericValue); // 返回原始值
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return "";
        }
    }


    //将MultipartFile转换为File
    public static File convertMultipartFileToFile(MultipartFile multipartFile) {
        File file = new File(multipartFile.getOriginalFilename());
        try (InputStream inputStream = multipartFile.getInputStream();
             OutputStream outputStream = new FileOutputStream(file)) {
            // 将 MultipartFile 的内容写入到 File 中
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }
}
