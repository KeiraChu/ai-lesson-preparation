package com.ai.controller;

import com.ai.context.UserContext;
import com.ai.result.Result;
import com.ai.service.PPTService;
import com.ai.vo.reqvo.PPTVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * @author ：褚婧雯
 * @date ：2025/4/7 18:37
 * @description ：PPT生成
 */
@RestController
@RequestMapping("/ppt")
public class PPTController {
    @Autowired
    private PPTService pptService;

    /**
     * 查询ppt模板
     * @return
     */
    @PostMapping("/template")
    public Result getTemplate(@RequestBody HashMap<String,String> info) {
        return pptService.getTemplate(info);
    }

    /**
     * 根据简短提示词生成大纲
     * @param info
     * @return
     */
    @PostMapping("/outline")
    public Result genOutline(@RequestBody HashMap<String,String> info) {
        return pptService.genOutline(info);
    }


    /**
     * 根据大纲生成ppt
     * @param pptVo
     * @return
     */
    @PostMapping
    public Result genPPT(@RequestBody PPTVo pptVo){
        pptVo.setId(UserContext.getUserId());
        return pptService.genPPT(pptVo);
    }

    /**
     * 查询ppt生成进度
     * @param sid
     * @return
     */
    @GetMapping("/progress")
    public Result getPPTBySid(@RequestParam String sid) {
        return pptService.getPPTBySid(sid);
    }

}
