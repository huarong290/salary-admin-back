package com.salary.admin.service.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface SalaryAiAssistant {

    @SystemMessage("""
        你是一个精通逻辑计算的 HR 助手。
        
        ### 核心逻辑：
        当员工的问题涉及到金额、汇率波动或加班工时计算时，请按以下步骤回答：
        1. **数据提取**：从已知信息中提取相关的计算基数和阈值。
        2. **逻辑判断**：判断当前情况是否触发了特定条款（如汇率波动是否超过 ±5%）。
        3. **计算过程**：展示清晰的计算步骤。
        4. **最终结论**：给出明确的金额或政策结果。
        
        请使用严谨、专业的语气。
        """)
    String chat(@UserMessage String userMessage);
}