package com.ai.service.impl;

import com.ai.context.UserContext;
import com.ai.mapper.LessonPlanMapper;
import com.ai.properties.TextProperties;
import com.ai.result.PageResult;
import com.ai.result.Result;
import com.ai.service.TextService;
import com.ai.utils.IdWorker;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.reactivex.Flowable;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.dashscope.app.*;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.ai.entity.LessonPlan;
import reactor.core.publisher.Flux;


import java.util.Date;
import java.util.HashMap;

/**
 * @author ：褚婧雯
 * @date ：2025/4/7 15:31
 * @description ：教案生成
 */
@Service
public class TextServiceImpl implements TextService {

    @Autowired
    private TextProperties textProperties;

    @Autowired
    private LessonPlanMapper lessonPlanMapper;

    @Autowired
    private IdWorker idWorker;


    @Override
    public Result textGen(String prompt) {
        String s = "";
        try {
            s = Call(prompt);
        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            return Result.error("教案生成失败，请稍后重试");
        }
        return Result.success(s);
    }

    /**
     * 保存教案
     * @param info
     * @return
     */
    @Override
    public Result lessonSave(HashMap<String, String> info) {
        LessonPlan lessonPLan = new LessonPlan();
        lessonPLan.setId(idWorker.nextId());
        lessonPLan.setUserid(UserContext.getUserId());
        lessonPLan.setContent(info.get("text"));
        lessonPLan.setTitle(info.get("title"));
        lessonPLan.setCreateTime(LocalDateTime.now().toDate());
        lessonPLan.setUpdateTime(LocalDateTime.now().toDate());
        lessonPlanMapper.insert(lessonPLan);
        return Result.success("保存成功");
    }

    /**
     * 删除教案
     * @param info
     * @return
     */
    @Override
    public Result lessonDelete(HashMap<String, String> info) {
        String id = info.get("id");
        LessonPlan lessonPlan = lessonPlanMapper.selectById(id);
        requireOwner(lessonPlan);
        lessonPlan.setDeleted(0);
        lessonPlanMapper.updateById(lessonPlan);
        return Result.success("删除成功");
    }

    /**
     * 分页查询教案
     * @param info
     * @return
     */
    @Override
    public PageResult pageQuery(HashMap<String, String> info) {
        int pageNum = Integer.parseInt(info.getOrDefault("pageNum","1"));
        int pageSize = Integer.parseInt(info.getOrDefault("pageSize", "10"));
        PageHelper.startPage(pageNum,pageSize);
        String userid = String.valueOf(UserContext.getUserId());
        Page<LessonPlan> page = lessonPlanMapper.pageQuery(userid);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 修改教案
     * @return
     */
    @Override
    public Result updateLesson(HashMap<String, String> info) {
        String id = info.get("id");
        String title = info.get("title");
        String content = info.get("content");
        LessonPlan lessonPlan = lessonPlanMapper.selectById(id);
        requireOwner(lessonPlan);
        lessonPlan.setTitle(title);
        lessonPlan.setContent(content);
        lessonPlan.setUpdateTime(LocalDateTime.now().toDate());
        lessonPlanMapper.updateById(lessonPlan);
        return Result.success("修改成功");
    }

    /**
     * 根据教案id查询教案
     * @param id
     * @return
     */
    @Override
    public Result<LessonPlan> getById(String id) {
        LessonPlan lessonPlan = lessonPlanMapper.selectById(id);
        requireOwner(lessonPlan);
        return Result.success(lessonPlan);
    }

    private void requireOwner(LessonPlan lessonPlan) {
        if (lessonPlan == null || !UserContext.getUserId().equals(lessonPlan.getUserid()) || Integer.valueOf(0).equals(lessonPlan.getDeleted())) {
            throw new IllegalArgumentException("教案不存在或无权访问");
        }
    }

    /**
     * 教案生成，流式响应
     * @param prompt
     * @return
     */
    @Override
    public Flux<Result> textGenStream(String prompt) {

        return Flux.create(sink -> {
            try {
                ApplicationParam param = ApplicationParam.builder()
                        .apiKey(textProperties.getApiKey())
                        .appId(textProperties.getAppId())
                        .prompt(prompt)
                        .incrementalOutput(true)
                        .build();

                Application application = new Application();
                Flowable<?> result = application.streamCall(param);

                result.blockingForEach(data -> {
                    if (data instanceof com.alibaba.dashscope.app.ApplicationResult) {
                        com.alibaba.dashscope.app.ApplicationResult appResult =
                                (com.alibaba.dashscope.app.ApplicationResult) data;
                        String text = appResult.getOutput().getText();
//                        System.out.println(text);
                        sink.next(Result.success(text)); // 将每次的结果推送给前端
                    }
                });

                sink.complete(); // 流结束
            } catch (ApiException | NoApiKeyException | InputRequiredException e) {
                sink.error(e); // 处理异常
            }
        });
    }


    /**
     * 调用阿里大模型
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
                .apiKey(textProperties.getApiKey())
                .appId(textProperties.getAppId())
                .prompt(prompt)
                .build();

        Application application = new Application();
        ApplicationResult result = application.call(param);

//        System.out.printf("text: %s\n",
//                result.getOutput().getText());

        return result.getOutput().getText();
    }

//    public void streamCall(String prompt) throws NoApiKeyException, InputRequiredException {
//        ApplicationParam param = ApplicationParam.builder()
//                // 若没有配置环境变量，可用百炼API Key将下行替换为：.apiKey("sk-xxx")。但不建议在生产环境中直接将API Key硬编码到代码中，以减少API Key泄露风险。
//                .apiKey(textProperties.getApiKey())
//                // 替换为实际的应用 ID
//                .appId(textProperties.getAppId())
//                .prompt(prompt)
//                // 增量输出
//                .incrementalOutput(true)
//                .build();
//        Application application = new Application();
//        // .streamCall（）：流式输出内容
//        Flowable<ApplicationResult> result = application.streamCall(param);
//        result.blockingForEach(data -> {
//            System.out.printf("%s\n",
//                    data.getOutput().getText());
//        });
//    }
}
