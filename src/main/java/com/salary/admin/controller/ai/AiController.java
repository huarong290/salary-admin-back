package com.salary.admin.controller.ai;


import com.salary.admin.model.dto.ai.ChatRequestDTO;
import com.salary.admin.model.dto.ai.ImportRequestDTO;
import com.salary.admin.service.ai.AiKnowledgeService;
import com.salary.admin.service.ai.SalaryAiAssistant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private SalaryAiAssistant assistant;

    @Autowired
    private AiKnowledgeService aiKnowledgeService;

    // 1. 对话接口
    @PostMapping("/chat")
    public String chat(@RequestBody ChatRequestDTO chatRequestDTO) {
        return assistant.chat(chatRequestDTO.getMessage());
    }

    // 2. 导入制度接口 (示例)
    @PostMapping("/import")
    public String importDoc(@RequestBody ImportRequestDTO request) {
        aiKnowledgeService.importText(request.getText());
        return "知识库更新成功";
    }
}
