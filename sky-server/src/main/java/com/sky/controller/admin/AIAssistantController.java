package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.AIAssistantService;
import com.sky.vo.SessionVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * AI助手控制器
 */
@RestController
@RequestMapping("/admin/ai")
@Slf4j
@Api(tags = "AI助手相关接口")
public class AIAssistantController {

    @Autowired
    private AIAssistantService aiAssistantService;

    /**
     * AI助手对话接口
     * @param request 包含用户问题、对话历史、会话ID的请求体
     * @return AI回复
     */
    @PostMapping("/chat")
    @ApiOperation("AI助手对话")
    public Result<Map<String, String>> chat(@RequestBody Map<String, Object> request) {
        String question = (String) request.get("question");
        Object historyObj = request.get("history");
        Long userId = Long.valueOf(request.getOrDefault("userId", 0).toString());
        String sessionId = (String) request.getOrDefault("sessionId", "");
        log.info("收到AI助手问题: {}", question);
        
        try {
            String answer = aiAssistantService.processQuestion(question, historyObj, userId, sessionId);
            return Result.success(Map.of("answer", answer));
        } catch (Exception e) {
            log.error("AI助手处理问题失败", e);
            return Result.error("AI助手暂时无法回答，请稍后再试");
        }
    }

    /**
     * 获取指定会话的对话历史
     * @param sessionId 会话ID
     * @param userId 员工ID（从请求头或参数获取）
     * @return 对话历史列表
     */
    @GetMapping("/history")
    @ApiOperation("获取对话历史")
    public Result<List<Map<String, Object>>> getHistory(
            @RequestParam String sessionId,
            @RequestParam Long userId) {
        try {
            List<Map<String, Object>> history = aiAssistantService.getChatHistory(userId, sessionId);
            return Result.success(history);
        } catch (Exception e) {
            log.error("获取对话历史失败", e);
            return Result.error("获取对话历史失败");
        }
    }

    /**
     * 获取用户的所有会话列表
     * @param userId 员工ID
     * @return 会话信息列表
     */
    @GetMapping("/sessions")
    @ApiOperation("获取会话列表")
    public Result<List<SessionVO>> getSessions(@RequestParam Long userId) {
        try {
            List<SessionVO> sessions = aiAssistantService.getSessionList(userId);
            return Result.success(sessions);
        } catch (Exception e) {
            log.error("获取会话列表失败", e);
            return Result.error("获取会话列表失败");
        }
    }

    /**
     * 删除指定会话
     * @param sessionId 会话ID
     * @param userId 员工ID
     * @return 删除结果
     */
    @DeleteMapping("/session")
    @ApiOperation("删除会话")
    public Result<String> deleteSession(
            @RequestParam String sessionId,
            @RequestParam Long userId) {
        try {
            aiAssistantService.deleteSession(userId, sessionId);
            return Result.success("删除成功");
        } catch (Exception e) {
            log.error("删除会话失败", e);
            return Result.error("删除会话失败");
        }
    }
}
