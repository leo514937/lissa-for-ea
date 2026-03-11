# 方案一 (Before) 实现流程说明文档

此文档描述了传统的“检索 -> 判定”追溯链路的 Java 代码实现逻辑。

## 1. 总体逻辑说明
方案一通过向量相似度计算初步筛选出一小部分候选项，然后直接交给大语言模型（LLM）进行最终判定。

## 2. 核心执行流程与代码映射

### 第一阶段：初始化与检索 (Retrieval)
1. **入口点**: `Evaluation.run()`
   - **代码位置**: `Evaluation.java:L277`
   - **功能**: 调用 `classifier.classify(sourceStore, targetStore)`。
2. **生成候选项**: `Classifier.createClassificationTasks()`
   - **代码位置**: `Classifier.java:L215`
   - **功能**: 遍历所有需求，从 TargetStore 中查找相似项。
3. **向量相似度计算**: `CosineSimilarity.java`
   - **核心函数**: `findSimilarElements()` (L42) 和 `cosineSimilarity()` (L54)。
   - **功能**: 执行数学运算，计算余弦相似度并按得分排序。
4. **截断策略**: `CosineSimilarity.java:L51`
   - **功能**: 根据配置文件的 `max_results` (方案一中设为 15) 返回 Top-N 候选项。

### 第二阶段：模型判定 (Classification)
1. **调用模型**: `SimpleClassifier.java`
   - **核心函数**: `classifyIntern()` (L159)。
   - **功能**: 组装 Prompt（模板在 L32），调用 LLM 接口。
2. **逻辑处理**: `SimpleClassifier.java:L143`
   - **功能**: 解析 LLM 的文本响应，检查是否包含 "yes" 关键词。

## 3. 性能特点
- **资源消耗**: 较低。每个需求只需调用 15 次 LLM。
- **瓶颈**: 高度依赖第一阶段 Embedding 的召回率。如果真值不在前 15 名中，后续无法找回。
