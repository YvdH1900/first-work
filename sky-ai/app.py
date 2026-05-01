from flask import Flask, request, jsonify
import os
from dotenv import load_dotenv
from qwen_client import QwenClient
import logging

# 加载环境变量
load_dotenv()

# 配置日志
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

app = Flask(__name__)

# 初始化通义千问客户端
qwen_client = QwenClient(
    api_key=os.getenv('QWEN_API_KEY'),
    model=os.getenv('QWEN_MODEL', 'qwen-turbo')
)

@app.route('/api/ai/analyze', methods=['POST'])
def analyze():
    """
    AI分析接口
    接收用户问题、经营数据和对话历史，调用通义千问进行智能分析
    """
    try:
        # 获取请求数据
        data = request.get_json()
        question = data.get('question', '')
        context_data = data.get('context', {})
        # 获取对话历史（可选）
        history = data.get('history', [])
        
        logger.info(f"收到AI分析请求: {question}")
        
        # 构建完整的提示词（包含上下文数据）
        prompt = build_prompt(question, context_data)
        
        # 调用通义千问API（传入对话历史）
        response = qwen_client.chat(prompt, history)
        
        logger.info("AI分析完成")
        
        return jsonify({
            'code': 1,
            'message': 'success',
            'data': {
                'answer': response
            }
        })
        
    except Exception as e:
        logger.error(f"AI分析失败: {str(e)}")
        return jsonify({
            'code': 0,
            'message': f'AI分析失败: {str(e)}',
            'data': None
        }), 500

def build_prompt(question, context_data):
    """
    构建提示词
    将用户问题和经营数据组合成完整的提示词
    """
    prompt = f"""你是一个专业的餐饮店经营AI助手，名叫"小智"。
你的职责是帮助商家分析经营数据、提供优化建议。

【当前经营数据】
"""
    
    # 添加上下文数据到提示词
    if context_data:
        # 今日数据
        if 'today_turnover' in context_data:
            prompt += f"- 今日营业额：{context_data['today_turnover']}元\n"
        if 'today_order_count' in context_data:
            prompt += f"- 今日订单数：{context_data['today_order_count']}单\n"
        if 'today_new_users' in context_data:
            prompt += f"- 今日新增用户：{context_data['today_new_users']}人\n"
        
        # 菜品销量
        if 'top_dishes' in context_data and context_data['top_dishes']:
            prompt += "- 今日热销菜品TOP10：\n"
            for i, dish in enumerate(context_data['top_dishes'], 1):
                prompt += f"  {i}. {dish['name']} - 销量{dish['number']}份\n"
        
        # 本月数据
        if 'month_turnover' in context_data:
            prompt += f"- 本月营业额：{context_data['month_turnover']}元\n"
        if 'month_order_count' in context_data:
            prompt += f"- 本月订单数：{context_data['month_order_count']}单\n"
        
        # 昨日数据（用于对比）
        if 'yesterday_turnover' in context_data:
            prompt += f"- 昨日营业额：{context_data['yesterday_turnover']}元\n"
        if 'yesterday_order_count' in context_data:
            prompt += f"- 昨日订单数：{context_data['yesterday_order_count']}单\n"
        
        # 订单状态分布
        if 'order_status_distribution' in context_data:
            status = context_data['order_status_distribution']
            prompt += "- 订单状态分布：\n"
            prompt += f"  待接单：{status.get('pending', 0)}单\n"
            prompt += f"  制作中：{status.get('preparing', 0)}单\n"
            prompt += f"  配送中：{status.get('delivering', 0)}单\n"
            prompt += f"  已完成：{status.get('completed', 0)}单\n"
            prompt += f"  已取消：{status.get('cancelled', 0)}单\n"
    else:
        prompt += "- 暂无实时数据\n"
    
    prompt += f"""
【用户问题】
{question}

请基于以上数据回答用户问题。回答要求：
1. 数据要准确，直接引用提供的数据
2. 如果是趋势分析问题，可以对比今日和昨日数据给出趋势判断
3. 如果是菜品分析问题，可以基于热销菜品数据给出建议
4. 回答要简洁明了，建议要实用
5. 如果用户问的问题超出餐饮经营范围，请礼貌地引导回经营相关话题
"""
    
    return prompt

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
