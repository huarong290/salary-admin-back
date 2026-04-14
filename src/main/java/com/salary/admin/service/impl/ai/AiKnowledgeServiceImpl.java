package com.salary.admin.service.impl.ai;

import com.salary.admin.service.ai.AiKnowledgeService;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AiKnowledgeServiceImpl implements AiKnowledgeService {

    @Autowired
    private EmbeddingStore<TextSegment> embeddingStore;

    @Autowired
    private EmbeddingModel embeddingModel;

    /**
     * 将原始文本导入 Milvus
     */
    public void importText(String content) {
        Document document = Document.from(content);

        // 定义切分策略：每段 300 字，重叠 30 字（保证上下文连贯）
        DocumentSplitter splitter = DocumentSplitters.recursive(300, 30);

        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(splitter)
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();

        ingestor.ingest(document);
    }
}
