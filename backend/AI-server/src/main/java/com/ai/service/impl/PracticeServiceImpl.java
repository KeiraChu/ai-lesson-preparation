package com.ai.service.impl;

import com.ai.context.UserContext;
import com.ai.entity.LessonPlan;
import com.ai.mapper.LessonPlanMapper;
import com.ai.properties.PracticeProperties;
import com.ai.result.Result;
import com.ai.service.PracticeService;
import com.alibaba.dashscope.app.Application;
import com.alibaba.dashscope.app.ApplicationParam;
import com.alibaba.dashscope.app.ApplicationResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * @author ：褚婧雯
 * @date ：2025/4/11 21:06
 * @description ：PracticeServiceImpl
 */
@Service
public class PracticeServiceImpl implements PracticeService {

    @Autowired
    private LessonPlanMapper lessonPlanMapper;

    @Autowired
    private PracticeProperties practiceProperties;

    @Override
    public Result practiceGenerate(HashMap<String, String> info) {
        String lessonId = info.get("lessonId");
        String userId = info.get("userId");
        LessonPlan lessonPlan = lessonPlanMapper.selectById(lessonId);
        if (lessonPlan == null || !UserContext.getUserId().equals(lessonPlan.getUserid())) {
            throw new IllegalArgumentException("教案不存在或无权访问");
        }
        String content = lessonPlan.getContent();

        // 发起请求生成练习题
        String s = "";
        try {
            s = practiceCall(content);
        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            return Result.error("练习题生成失败，请稍后重试");
        }
        return Result.success(s);
    }

    /**
     * 调用阿里大模型
     * @param prompt
     * @return
     * @throws ApiException
     * @throws NoApiKeyException
     * @throws InputRequiredException
     */
    public String practiceCall(String prompt)
            throws ApiException, NoApiKeyException, InputRequiredException {
        ApplicationParam param = ApplicationParam.builder()
                // 若没有配置环境变量，可用百炼API Key将下行替换为：.apiKey("sk-xxx")。但不建议在生产环境中直接将API Key硬编码到代码中，以减少API Key泄露风险。
                .apiKey(practiceProperties.getApiKey())
                .appId(practiceProperties.getAppId())
                .prompt(prompt)
                .build();

        Application application = new Application();
        ApplicationResult result = application.call(param);

//        System.out.printf("text: %s\n",
//                result.getOutput().getText());

        return result.getOutput().getText();
    }
}
