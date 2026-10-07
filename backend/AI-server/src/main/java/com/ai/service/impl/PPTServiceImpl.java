package com.ai.service.impl;

import com.ai.entity.Outline;
import com.ai.entity.PPT;
import com.ai.entity.TemplateData;
import com.ai.mapper.OutlineMapper;
import com.ai.mapper.PPTMapper;
import com.ai.pptutils.ApiAuthAlgorithm;
import com.ai.pptutils.ApiClient;
import com.ai.pptutils.CreateResponse;
import com.ai.properties.PPTProperties;
import com.ai.utils.IdWorker;
import com.ai.vo.reqvo.PPTVo;
import com.ai.vo.respvo.OutlineVo;
import com.ai.result.Result;
import com.ai.service.PPTService;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;


/**
 * @author ：褚婧雯
 * @date ：2025/4/8 19:24
 * @description ：ppt生成接口实现类
 */
@Service
public class PPTServiceImpl implements PPTService {

    @Autowired
    private IdWorker idWorker;

//    public static final String APPID="e6133f78";
    @Autowired
    private PPTProperties pptProperties;

    @Autowired
    private PPTMapper pptMapper;

    @Autowired
    private OutlineMapper outlineMapper;

    /**
     * 查询ppt模板
     * @return
     */
    @Override
    public Result getTemplate(HashMap<String,String> info) {
        // 获取请求头中需要携带的参数 appId（控制台获取）, timestamp（时间戳，单位：秒，与服务端时间相差五分钟之内）, signature（签名）
        long timestamp = System.currentTimeMillis()/1000;
        String ts = String.valueOf(timestamp);

        ApiAuthAlgorithm auth = new ApiAuthAlgorithm();
        String signature = auth.getSignature(pptProperties.getAPPID(), pptProperties.getAPISecret(), timestamp);

        // 建立链接
        ApiClient client = new ApiClient("https://zwapi.xfyun.cn/api/ppt/v2");

        // PPT主题列表查询【所有模板均免费使用】
        String templateResult = null;
        try {
            templateResult = client.getTemplateList(pptProperties.getAPPID(), ts, signature,info);
        } catch (IOException e) {
//            throw new RuntimeException(e);
            return Result.error("查询失败");
        }
//        templateResult = templateResult.replaceAll("\\\\","");
//        System.out.println(templateResult);
        Object data = JSON.parseObject(templateResult).get("data");
        // 字符串处理
        String s = data.toString().replaceAll("\\\\", "")
                .replaceAll("\"\\{","{")
                .replaceAll("}\"","}");
        TemplateData templateData = JSON.parseObject(s).toJavaObject(TemplateData.class);

        return Result.success(templateData);
    }

    /**
     * 根据简短提示词生成大纲
     * @param info
     * @return
     */
    @Override
    public Result genOutline(HashMap<String,String> info) {
        // 获取请求头中需要携带的参数 appId（控制台获取）, timestamp（时间戳，单位：秒，与服务端时间相差五分钟之内）, signature（签名）
        long timestamp = System.currentTimeMillis()/1000;
        String ts = String.valueOf(timestamp);

        ApiAuthAlgorithm auth = new ApiAuthAlgorithm();
        String signature = auth.getSignature(pptProperties.getAPPID(), pptProperties.getAPISecret(), timestamp);

        // 建立链接
        ApiClient client = new ApiClient("https://zwapi.xfyun.cn/api/ppt/v2");
        String outlineResp = null;
        try {
            outlineResp = client.createOutline(pptProperties.getAPPID(), ts, signature,info);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        String outlineResp = "{\"flag\":true,\"code\":0,\"desc\":\"成功\",\"count\":null,\"data\":{\"sid\":\"6c50b3205ff14fd9962d0068e91bf67a\",\"outline\":{\"title\":\"沁园春雪的文学鉴赏\",\"subTitle\":\"统编版必修上册深度解析\",\"chapters\":[{\"chapterTitle\":\"沁园春·雪简介\",\"chapterContents\":[{\"chapterTitle\":\"作者简介\"},{\"chapterTitle\":\"作品背景\"}]},{\"chapterTitle\":\"诗歌内容分析\",\"chapterContents\":[{\"chapterTitle\":\"主题思想\"},{\"chapterTitle\":\"艺术特色\"}]},{\"chapterTitle\":\"教学目标设定\",\"chapterContents\":[{\"chapterTitle\":\"知识与技能\"},{\"chapterTitle\":\"情感态度价值观\"}]},{\"chapterTitle\":\"教学方法探讨\",\"chapterContents\":[{\"chapterTitle\":\"启发式教学\"},{\"chapterTitle\":\"讨论交流法\"}]},{\"chapterTitle\":\"课堂活动设计\",\"chapterContents\":[{\"chapterTitle\":\"朗读比赛\"},{\"chapterTitle\":\"创作分享会\"}]}]}},\"sid\":null}";
//        System.out.println(outlineResp);
//        System.out.println("--------------------------------------------------");
        Object data = JSON.parseObject(outlineResp).get("data");
        Object outline = JSON.parseObject(data.toString()).get("outline");

        OutlineVo outlineVo = JSON.parseObject(outline.toString()).toJavaObject(OutlineVo.class);

        return Result.success(outlineVo);
    }

    /**
     * 根据大纲生成ppt
     * @param pptVo
     * @return
     */
    @Override
    public Result genPPT(PPTVo pptVo){
        // 获取请求头中需要携带的参数 appId（控制台获取）, timestamp（时间戳，单位：秒，与服务端时间相差五分钟之内）, signature（签名）
        long timestamp = System.currentTimeMillis()/1000;
        String ts = String.valueOf(timestamp);

        ApiAuthAlgorithm auth = new ApiAuthAlgorithm();
        String signature = auth.getSignature(pptProperties.getAPPID(), pptProperties.getAPISecret(), timestamp);

        // 建立链接
        ApiClient client = new ApiClient("https://zwapi.xfyun.cn/api/ppt/v2");

        String pptByOutline = null;
//        String pptByOutline = "{\"flag\":true,\"code\":0,\"desc\":\"成功\",\"count\":null,\"data\":{\"sid\":\"7f3ec14ffbd7451296ddf37095debf44\",\"coverImgSrc\":\"https://bjcdn.openstorage.cn/xinghuo-privatedata/zhiwen/2025-04-10/7a68e9b7-e2cb-46ea-b606-2284a2591625/7984f885d768466c8d8bb9f8e885b7bf.png\",\"title\":\"沁园春雪的艺术魅力\",\"subTitle\":\"深度解读古典诗词的美学价值\"},\"sid\":\"7f3ec14ffbd7451296ddf37095debf44\"}";
        try {
            pptByOutline = client.createPptByOutline(pptProperties.getAPPID(), ts, signature, pptVo);
        } catch (IOException e) {
            return Result.error("生成失败");
        }
//        System.out.println(pptByOutline);
        Object data = JSON.parseObject(pptByOutline).get("data");
//        System.out.println(data.toString());
        HashMap<String,String> hashMap = JSON.parseObject(data.toString()).toJavaObject(HashMap.class);

        // 保存生成的ppt
        PPT ppt = JSON.parseObject(data.toString()).toJavaObject(PPT.class);
        ppt.setId(idWorker.nextId());
        ppt.setUserid(pptVo.getId());
        ppt.setCreateTime(LocalDateTime.now().toDate());
        ppt.setUpdateTime(LocalDateTime.now().toDate());
        pptMapper.insert(ppt);

        // 保存大纲
        ObjectMapper mapper = new ObjectMapper();
        String jsonStr = null;
        try {
            jsonStr = mapper.writeValueAsString(pptVo.getOutline());
        } catch (JsonProcessingException e) {
            return Result.error("系统错误");
        }
        Outline outline = new Outline();
        outline.setOutline(jsonStr);
        outline.setUserid(pptVo.getId());
        outline.setPptid(ppt.getId());
        outline.setUpdateTime(LocalDateTime.now().toDate());
        outline.setUpdateTime(LocalDateTime.now().toDate());
        outlineMapper.insert(outline);
        return Result.success(hashMap);
    }

    /**
     * 查询ppt生成进度
     * @param sid
     * @return
     */
    @Override
    public Result getPPTBySid(String sid) {

        // 获取请求头中需要携带的参数 appId（控制台获取）, timestamp（时间戳，单位：秒，与服务端时间相差五分钟之内）, signature（签名）
        long timestamp = System.currentTimeMillis()/1000;
        String ts = String.valueOf(timestamp);

        ApiAuthAlgorithm auth = new ApiAuthAlgorithm();
        String signature = auth.getSignature(pptProperties.getAPPID(), pptProperties.getAPISecret(), timestamp);

        // 建立链接
        ApiClient client = new ApiClient("https://zwapi.xfyun.cn/api/ppt/v2");

        String progressResult = null;
        try {
            progressResult = client.checkProgress(pptProperties.getAPPID(), ts, signature, sid);
        } catch (IOException e) {
            return Result.error("查询失败");
        }
        Object data = JSON.parseObject(progressResult).get("data");
        HashMap hashMap = JSON.parseObject(data.toString()).toJavaObject(HashMap.class);
        return Result.success(hashMap);
    }
}
