package com.salary.admin.service.ai;

public interface AiKnowledgeService {


    /**
     * 将原始文本导入 Milvus
     */
    public void importText(String content);
}
