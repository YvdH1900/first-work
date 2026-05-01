import os
from typing import Dict, Any
from openai import OpenAI
import logging

logger = logging.getLogger(__name__)

class QwenClient:
    """
    通义千问API客户端
    使用OpenAI兼容接口调用通义千问模型
    """
    
    def __init__(self, api_key: str, model: str = 'qwen-turbo'):
        """
        初始化通义千问客户端
        
        Args:
            api_key: 通义千问API密钥
            model: 使用的模型名称，默认qwen-turbo
        """
        self.api_key = api_key
        self.model = model
        
        # 初始化OpenAI客户端（通义千问兼容OpenAI接口）
        self.client = OpenAI(
            api_key=api_key,
            base_url="https://dashscope.aliyuncs.com/compatible-mode/v1"
        )
        
        logger.info(f"通义千问客户端初始化完成，使用模型: {model}")
    
    def chat(self, message: str, history: list = None) -> str:
        """
        调用通义千问API进行对话
        
        Args:
            message: 用户输入的消息（包含经营数据的提示词）
            history: 对话历史列表（可选），格式：[{role: "user/assistant", content: "..."}]
            
        Returns:
            AI生成的回复内容
        """
        try:
            # 构建消息列表
            messages = []
            
            # 添加系统提示词
            messages.append({
                "role": "system",
                "content": """你是一个专业的餐饮店经营AI助手，名叫"小智"。
你的职责是帮助商家分析经营数据、提供优化建议。
你可以回答关于营业额、订单、菜品销售、用户数据等问题。
回答要简洁明了，数据要准确，建议要实用。
如果用户问的问题超出餐饮经营范围，请礼貌地引导回经营相关话题。"""
            })
            
            # 添加对话历史（如果有）
            if history:
                for msg in history:
                    messages.append({
                        "role": msg.get("role", "user"),
                        "content": msg.get("content", "")
                    })
            
            # 添加当前用户消息（包含经营数据的提示词）
            messages.append({
                "role": "user",
                "content": message
            })
            
            # 调用API
            response = self.client.chat.completions.create(
                model=self.model,
                messages=messages,
                temperature=0.7,
                max_tokens=1000
            )
            
            # 提取回复内容
            answer = response.choices[0].message.content
            
            logger.info(f"通义千问API调用成功，消息数量: {len(messages)}，回复长度: {len(answer)}")
            return answer
            
        except Exception as e:
            logger.error(f"通义千问API调用失败: {str(e)}")
            raise Exception(f"AI服务调用失败: {str(e)}")
