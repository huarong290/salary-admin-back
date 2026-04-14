package com.salary.admin.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.milvus.MilvusEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LangChain4jConfig {

    @Value("${langchain4j.milvus.uri}")
    private String milvusUri;

    @Value("${langchain4j.milvus.collection-name}")
    private String collectionName;

    // 1. 定义存储引擎：Milvus
    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        return MilvusEmbeddingStore.builder()
                .uri(milvusUri)
                .collectionName(collectionName)
                .dimension(768) // 必须与 nomic-embed-text 一致
                .build();
    }

    // 2. 定义检索器：将 Milvus 和 Embedding 模型结合
    @Bean
    public ContentRetriever contentRetriever(EmbeddingStore<TextSegment> embeddingStore, EmbeddingModel embeddingModel) {
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(3) // 每次检索最相关的 3 条公司制度
                .minScore(0.75) // 相似度阈值
                .build();
    }
}