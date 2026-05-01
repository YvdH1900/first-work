package com.sky.mapper;

import com.sky.entity.AiChatHistory;
import com.sky.vo.SessionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * AI对话历史数据访问层
 */
@Mapper
public interface AiChatHistoryMapper {

    /**
     * 插入对话记录
     * @param history 对话记录
     */
    void insert(AiChatHistory history);

    /**
     * 批量插入对话记录
     * @param histories 对话记录列表
     */
    void insertBatch(List<AiChatHistory> histories);

    /**
     * 根据会话ID查询对话历史
     * @param userId 员工ID
     * @param sessionId 会话ID
     * @return 对话历史列表
     */
    List<AiChatHistory> getBySessionId(@Param("userId") Long userId, @Param("sessionId") String sessionId);

    /**
     * 查询用户的所有会话信息列表（包含会话名称）
     * @param userId 员工ID
     * @return 会话信息列表
     */
    List<SessionVO> getSessionsByUserId(Long userId);

    /**
     * 删除指定会话的所有记录
     * @param userId 员工ID
     * @param sessionId 会话ID
     */
    void deleteBySessionId(@Param("userId") Long userId, @Param("sessionId") String sessionId);

    /**
     * 删除用户的所有对话记录
     * @param userId 员工ID
     */
    void deleteByUserId(Long userId);
}
