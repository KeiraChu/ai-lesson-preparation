package com.ai.service.impl;

import com.ai.properties.AnalyzeProperties;
import com.ai.properties.FileProperties;
import com.ai.context.UserContext;
import com.ai.entity.MyFile;
import com.ai.mapper.FileMapper;
import com.ai.service.AnalyzeService;
import com.alibaba.dashscope.app.Application;
import com.alibaba.dashscope.app.ApplicationParam;
import com.alibaba.dashscope.app.ApplicationResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ：褚婧雯
 * @date ：2025/4/13 16:21
 * @description ：
 */
@Service
public class AnalyzeServiceImpl implements AnalyzeService {


    @Autowired
    private AnalyzeProperties analyzeProperties;
    @Autowired
    private FileProperties fileProperties;
    @Autowired
    private FileMapper fileMapper;

    /**
     * 学情分析
     *
     * @param name
     * @param id
     * @return
     */
    @Override
    public ArrayList<HashMap> analyzeTest(String name, String id) {
        StringBuilder sb = new StringBuilder();
        sb.append("目标学生的脱敏成绩:\n");
        try {
            MyFile uploaded = fileMapper.selectById(id);
            if (uploaded == null || !UserContext.getUserId().equals(uploaded.getUserid())) {
                throw new IllegalArgumentException("成绩文件不存在或无权访问");
            }
            Path uploadRoot = Paths.get(fileProperties.getUploadPath()).toAbsolutePath().normalize();
            String storedName = Paths.get(URI.create(uploaded.getUrl()).getPath()).getFileName().toString();
            Path filePath = uploadRoot.resolve(storedName).normalize();
            if (!filePath.startsWith(uploadRoot)) {
                throw new IllegalArgumentException("非法文件路径");
            }
            Map<String, Map<String, String>> scoresMap = readScoresByStudentName(filePath.toString());
            Map<String, String> studentScores = scoresMap.get(name);
            if (studentScores != null) {
                for (Map.Entry<String, String> entry : studentScores.entrySet()) {
                    sb.append(entry.getKey() + ":" + entry.getValue() + " ");
                }
            } else {
                throw new IllegalArgumentException("成绩表中未找到该学生");
            }
        } catch (IOException e) {
            throw new IllegalStateException("成绩文件读取失败", e);
        }


        String result = null;
        try {
            result = Call(sb.toString());
        } catch (NoApiKeyException e) {
            throw new RuntimeException(e);
        } catch (InputRequiredException e) {
            throw new RuntimeException(e);
        }
        ArrayList<HashMap> list = new ArrayList<>();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("value",result);
        list.add(hashMap);
        return list;
    }

    /**
     * 调用阿里大模型
     *
     * @param prompt
     * @return
     * @throws ApiException
     * @throws NoApiKeyException
     * @throws InputRequiredException
     */
    public String Call(String prompt)
            throws ApiException, NoApiKeyException, InputRequiredException {
        ApplicationParam param = ApplicationParam.builder()
                // 若没有配置环境变量，可用百炼API Key将下行替换为：.apiKey("sk-xxx")。但不建议在生产环境中直接将API Key硬编码到代码中，以减少API Key泄露风险。
                .apiKey(analyzeProperties.getApiKey())
                .appId(analyzeProperties.getAppId())
                .prompt(prompt)
                .build();

        Application application = new Application();
        ApplicationResult result = application.call(param);

//        System.out.printf("text: %s\n",
//                result.getOutput().getText());

        return result.getOutput().getText();
    }


//    public static Map<String, String[]> readScoresByStudentName(String filePath) throws IOException {
//        FileInputStream fis = new FileInputStream(new File(filePath));
//        Workbook workbook = new XSSFWorkbook(fis);
//        Sheet sheet = workbook.getSheetAt(0); // 假设数据位于第一个工作表
//
//        Map<String, String[]> studentScores = new HashMap<>();
//
//        for (Row row : sheet) {
//            Cell nameCell = row.getCell(0); // 假设姓名在第一列
//            if (nameCell != null && nameCell.getCellType() == CellType.STRING) {
//                String name = nameCell.getStringCellValue();
//                String[] scores = new String[row.getLastCellNum()];
//                for (int i = 1; i < row.getLastCellNum(); i++) { // 跳过姓名列
//                    Cell cell = row.getCell(i);
//                    if (cell != null) {
//                        scores[i - 1] = cell.toString();
//                    }
//                }
//                studentScores.put(name, scores);
//            }
//        }
//
//        workbook.close();
//        fis.close();
//
//        return studentScores;
//    }


    /**
     * 处理excel
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static Map<String, Map<String, String>> readScoresByStudentName(String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis)) {
        Sheet sheet = workbook.getSheetAt(0); // 假设数据位于第一个工作表

        Map<String, Map<String, String>> studentScores = new HashMap<>();

        boolean firstRow = true; // 用于跳过标题行
        for (Row row : sheet) {
            if (firstRow) { // 跳过第一行（标题）
                firstRow = false;
                continue;
            }
            Cell nameCell = row.getCell(0); // 假设姓名在第一列
            if (nameCell != null && nameCell.getCellType() == CellType.STRING) {
                String name = nameCell.getStringCellValue();
                Map<String, String> scores = new HashMap<>();
                int cellNum = row.getLastCellNum();
                for (int i = 1; i < cellNum; i++) { // 跳过姓名列
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        Cell headerCell = sheet.getRow(0).getCell(i); // 获取对应的标题（如：语文、数学等）
                        if (headerCell != null) {
                            String subject = headerCell.toString();
                            String score = cell.toString();
                            if (!subject.equals("") && !score.equals(""))
                                scores.put(subject, score);
                        }
                    }
                }
                studentScores.put(name, scores);
            }
        }

        return studentScores;
        }
    }
}
