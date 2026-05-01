package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.AiChatHistory;
import com.sky.mapper.AiChatHistoryMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.AIAssistantService;
import com.sky.vo.SessionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AI助手服务实现类
 * 负责处理用户通过前端悬浮球提出的经营数据查询问题
 * Java端负责业务逻辑：查询数据库获取经营数据
 * Python端负责AI分析：调用通义千问API进行智能分析
 */
@Service
@Slf4j
public class AIAssistantServiceImpl implements AIAssistantService {

    /**
     * Python AI分析服务地址
     * 从配置文件中读取，默认http://localhost:5000
     */
    @Value("${sky.ai.python-service-url:http://localhost:5000}")
    private String pythonServiceUrl;

    /**
     * 对话历史数据访问层
     */
    @Autowired
    private AiChatHistoryMapper aiChatHistoryMapper;

    /**
     * 订单数据访问层，用于查询订单相关数据
     */
    @Autowired
    private OrderMapper orderMapper;
    
    /**
     * 菜品数据访问层，用于查询菜品相关数据
     */
    @Autowired
    private DishMapper dishMapper;

    /**
     * 用户数据访问层，用于查询用户相关数据
     */
    @Autowired
    private UserMapper userMapper;

    /**
     * 处理用户提问的核心方法
     * 1. Java端：查询数据库获取相关经营数据
     * 2. 将经营数据、用户问题和对话历史一起发送给Python AI服务
     * 3. Python端：调用通义千问API进行智能分析（支持多轮对话）
     * 4. 保存对话历史到数据库
     * 5. 返回AI分析结果
     * 
     * @param question 用户输入的问题，例如"今天营业额多少？"
     * @param history 对话历史（可选），格式：[{role: "user/assistant", content: "..."}]
     * @param userId 员工ID
     * @param sessionId 会话ID
     * @return 返回AI助手的回答内容
     */
    @Override
    public String processQuestion(String question, Object history, Long userId, String sessionId) {
        // 记录用户提问日志，便于后续分析和优化
        log.info("AI助手收到问题: {}", question);
        
        try {
            // 1. Java端：查询数据库获取经营数据
            Map<String, Object> businessData = queryBusinessData();
            
            // 2. 构建请求参数（包含用户问题、经营数据和对话历史）
            Map<String, Object> requestParams = new HashMap<>();
            requestParams.put("question", question);
            requestParams.put("context", businessData);
            // 添加对话历史（如果有）
            if (history != null) {
                requestParams.put("history", history);
            }
            
            // 3. 调用Python AI分析服务
            String aiResponse = callPythonAIService(requestParams);
            
            // 4. 保存对话历史到数据库
            saveChatHistory(userId, sessionId, question, aiResponse);
            
            // 5. 返回AI分析结果
            log.info("AI助手回复成功");
            return aiResponse;
            
        } catch (Exception e) {
            // 记录错误日志
            log.error("调用AI服务失败", e);
            
            // 返回友好的错误提示
            return "抱歉，AI服务暂时不可用，请稍后再试。";
        }
    }

    /**
     * 保存对话历史到数据库
     * @param userId 员工ID
     * @param sessionId 会话ID
     * @param userMessage 用户消息
     * @param aiMessage AI回复消息
     */
    private void saveChatHistory(Long userId, String sessionId, String userMessage, String aiMessage) {
        try {
            LocalDateTime now = LocalDateTime.now();
            
            // 保存用户消息
            AiChatHistory userHistory = AiChatHistory.builder()
                    .userId(userId)
                    .sessionId(sessionId)
                    .role("user")
                    .content(userMessage)
                    .createTime(now)
                    .build();
            aiChatHistoryMapper.insert(userHistory);
            
            // 保存AI回复
            AiChatHistory aiHistory = AiChatHistory.builder()
                    .userId(userId)
                    .sessionId(sessionId)
                    .role("assistant")
                    .content(aiMessage)
                    .createTime(now)
                    .build();
            aiChatHistoryMapper.insert(aiHistory);
            
            log.info("保存对话历史成功，会话ID: {}", sessionId);
        } catch (Exception e) {
            // 记录错误日志，但不影响主流程
            log.error("保存对话历史失败", e);
        }
    }

    /**
     * 获取指定会话的对话历史
     * @param userId 员工ID
     * @param sessionId 会话ID
     * @return 对话历史列表
     */
    @Override
    public List<Map<String, Object>> getChatHistory(Long userId, String sessionId) {
        List<AiChatHistory> histories = aiChatHistoryMapper.getBySessionId(userId, sessionId);
        return histories.stream().map(h -> {
            Map<String, Object> map = new HashMap<>();
            map.put("role", h.getRole());
            map.put("content", h.getContent());
            return map;
        }).collect(Collectors.toList());
    }

    /**
     * 获取用户的所有会话列表
     * @param userId 员工ID
     * @return 会话信息列表
     */
    @Override
    public List<SessionVO> getSessionList(Long userId) {
        return aiChatHistoryMapper.getSessionsByUserId(userId);
    }

    /**
     * 删除指定会话
     * @param userId 员工ID
     * @param sessionId 会话ID
     */
    @Override
    public void deleteSession(Long userId, String sessionId) {
        aiChatHistoryMapper.deleteBySessionId(userId, sessionId);
        log.info("删除会话成功，会话ID: {}", sessionId);
    }

    /**
     * 查询经营数据
     * 获取今日营业额、订单数、新增用户、菜品销量、订单趋势等数据
     * 这些数据将作为上下文提供给AI进行分析
     * 
     * @return 经营数据Map
     */
    private Map<String, Object> queryBusinessData() {
        Map<String, Object> data = new HashMap<>();
        
        // 获取当天的开始时间和结束时间
        LocalDateTime begin = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime end = LocalDateTime.now().with(LocalTime.MAX);
        
        // 1. 今日数据
        // 查询今日营业额
        Map turnoverMap = new HashMap();
        turnoverMap.put("beginTime", begin);
        turnoverMap.put("endTime", end);
        turnoverMap.put("status", 5); // 5表示已完成订单状态
        Double turnover = orderMapper.sumByMap(turnoverMap);
        data.put("today_turnover", turnover == null ? 0.0 : turnover);
        
        // 查询今日订单数
        Map orderMap = new HashMap();
        orderMap.put("beginTime", begin);
        orderMap.put("endTime", end);
        Integer totalCount = orderMapper.countByMap(orderMap);
        data.put("today_order_count", totalCount == null ? 0 : totalCount);
        
        // 查询今日新增用户数
        Map userMap = new HashMap();
        userMap.put("begin", begin);
        userMap.put("end", end);
        Integer newUsers = userMapper.countByMap(userMap);
        data.put("today_new_users", newUsers == null ? 0 : newUsers);
        
        // 2. 菜品销量TOP10
        List<GoodsSalesDTO> topDishes = orderMapper.getSalesStatistics(begin, end);
        data.put("top_dishes", topDishes);
        
        // 3. 本月数据
        LocalDateTime monthBegin = LocalDateTime.now().withDayOfMonth(1).with(LocalTime.MIN);
        Map monthTurnoverMap = new HashMap();
        monthTurnoverMap.put("beginTime", monthBegin);
        monthTurnoverMap.put("endTime", end);
        monthTurnoverMap.put("status", 5);
        Double monthTurnover = orderMapper.sumByMap(monthTurnoverMap);
        data.put("month_turnover", monthTurnover == null ? 0.0 : monthTurnover);
        
        Map monthOrderMap = new HashMap();
        monthOrderMap.put("beginTime", monthBegin);
        monthOrderMap.put("endTime", end);
        Integer monthOrderCount = orderMapper.countByMap(monthOrderMap);
        data.put("month_order_count", monthOrderCount == null ? 0 : monthOrderCount);
        
        // 4. 昨日数据（用于对比）
        LocalDateTime yesterdayBegin = LocalDateTime.now().minusDays(1).with(LocalTime.MIN);
        LocalDateTime yesterdayEnd = LocalDateTime.now().minusDays(1).with(LocalTime.MAX);
        Map yesterdayTurnoverMap = new HashMap();
        yesterdayTurnoverMap.put("beginTime", yesterdayBegin);
        yesterdayTurnoverMap.put("endTime", yesterdayEnd);
        yesterdayTurnoverMap.put("status", 5);
        Double yesterdayTurnover = orderMapper.sumByMap(yesterdayTurnoverMap);
        data.put("yesterday_turnover", yesterdayTurnover == null ? 0.0 : yesterdayTurnover);
        
        Map yesterdayOrderMap = new HashMap();
        yesterdayOrderMap.put("beginTime", yesterdayBegin);
        yesterdayOrderMap.put("endTime", yesterdayEnd);
        Integer yesterdayOrderCount = orderMapper.countByMap(yesterdayOrderMap);
        data.put("yesterday_order_count", yesterdayOrderCount == null ? 0 : yesterdayOrderCount);
        
        // 5. 订单状态分布
        Integer pendingCount = orderMapper.countStatus(2); // 待接单
        Integer preparingCount = orderMapper.countStatus(3); // 制作中
        Integer deliveringCount = orderMapper.countStatus(4); // 配送中
        Integer completedCount = orderMapper.countStatus(5); // 已完成
        Integer cancelledCount = orderMapper.countStatus(6); // 已取消
        Map<String, Integer> statusMap = new HashMap<>();
        statusMap.put("pending", pendingCount == null ? 0 : pendingCount);
        statusMap.put("preparing", preparingCount == null ? 0 : preparingCount);
        statusMap.put("delivering", deliveringCount == null ? 0 : deliveringCount);
        statusMap.put("completed", completedCount == null ? 0 : completedCount);
        statusMap.put("cancelled", cancelledCount == null ? 0 : cancelledCount);
        data.put("order_status_distribution", statusMap);
        
        log.info("查询经营数据完成: {}", data);
        return data;
    }

    /**
     * 调用Python AI分析服务
     * 通过HTTP POST请求将用户问题和经营数据发送给Python服务
     * Python服务会调用通义千问API进行智能分析
     * 
     * @param requestParams 请求参数（包含用户问题和经营数据）
     * @return AI分析结果
     * @throws Exception 当调用失败时抛出异常
     */
    private String callPythonAIService(Map<String, Object> requestParams) throws Exception {
        // 构建Python服务API地址
        String apiUrl = pythonServiceUrl + "/api/ai/analyze";
        
        // 将请求参数转换为JSON字符串
        String requestBody = JSON.toJSONString(requestParams);
        log.info("调用Python AI服务，请求参数: {}", requestBody);
        
        // 创建HTTP连接
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        try {
            // 设置请求方法为POST
            connection.setRequestMethod("POST");
            // 设置请求头
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            // 允许输入输出
            connection.setDoOutput(true);
            connection.setDoInput(true);
            // 设置超时时间
            connection.setConnectTimeout(10000); // 连接超时10秒
            connection.setReadTimeout(30000);    // 读取超时30秒（AI生成可能需要较长时间）
            
            // 发送请求体
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            // 获取响应码
            int responseCode = connection.getResponseCode();
            log.info("Python AI服务响应码: {}", responseCode);
            
            // 读取响应内容
            BufferedReader reader;
            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
            }
            
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            
            String responseBody = response.toString();
            log.info("Python AI服务响应: {}", responseBody);
            
            // 解析响应JSON
            JSONObject responseJson = JSON.parseObject(responseBody);
            
            // 检查响应状态
            if (responseJson.getInteger("code") == 1) {
                // 成功，提取AI回复内容
                JSONObject data = responseJson.getJSONObject("data");
                return data.getString("answer");
            } else {
                // 失败，返回错误信息
                String errorMessage = responseJson.getString("message");
                throw new Exception("AI分析失败: " + errorMessage);
            }
            
        } finally {
            // 关闭连接
            connection.disconnect();
        }
    }
}
