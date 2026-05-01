package com.sky.service;

import com.sky.vo.SessionVO;

import java.util.List;
import java.util.Map;

public interface AIAssistantService {

    /**
     * 处理用户问题并返回答案
     * @param question 用户问题
     * @param history 对话历史（可选）
     * @param userId 员工ID
     * @param sessionId 会话ID
     * @return AI回复
     */
    String processQuestion(String question, Object history, Long userId, String sessionId);

    /**
     * 获取指定会话的对话历史
     * @param userId 员工ID
     * @param sessionId 会话ID
     * @return 对话历史列表
     */
    List<Map<String, Object>> getChatHistory(Long userId, String sessionId);

    /**
     * 获取用户的所有会话列表
     * @param userId 员工ID
     * @return 会话信息列表
     */
    List<SessionVO> getSessionList(Long userId);

    /**
     * 删除指定会话
     * @param userId 员工ID
     * @param sessionId 会话ID
     */
    void deleteSession(Long userId, String sessionId);
}
