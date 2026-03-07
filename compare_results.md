# LiSSA 检索与过滤路径对比分析

在 LiSSA 框架中，根据配置文件（JSON）的不同，存在两条主要的实现路径。以下是它们的详细工作流对比。

---

## 路径 A：基于 IR 的多级 Pipeline
这是传统的“检索 + 分类”模式。通常在配置中使用 `classifiers` 字段触发。

### 1. 工作流流程
1.  **加载与向量化**：加载制品并使用 `EmbeddingCreator` 计算向量（Embeddings）。
2.  **构建 Store**：将向量存入 `SourceElementStore` 和 `TargetElementStore`。
3.  **IR 召回 (Candidate Generation)**：
    *   调用 `targetStore.findSimilar(source)`。
    *   利用向量余弦相似度（Cosine Similarity）进行 K-NN 搜索，找到前 $K$ 个候选者。
4.  **Pipeline 过滤与分类**：
    *   将召回的 $K$ 个候选者送入 [PipelineClassifier](file:///d:/code/lissa/src/main/java/edu/kit/kastel/sdq/lissa/ratlr/classifier/PipelineClassifier.java#24-166)。
    *   **多级阶段**：支持多个阶段的投票（Majority Voting）。
    *   **SLM 作用**：可以在中间阶段加入小模型（SLM）作为过滤器。
5.  **最终判定**：通过所有阶段的候选者被视为最终生成的 Trace Link。

### 2. 特点
- **性能**：高。由于使用了向量索引，搜索空间缩小到了 $K$。
- **瓶颈**：受限于 Embedding 的召回率（Recall）。如果相关项在向量搜索阶段没被找回，后续模型再强也无法弥补。

---

## 路径 B：基于全量笛卡尔积的 Candidate Filter Chain
这是一种“先暴力全搜，再层层过滤”的模式。在配置中使用 `candidate_filter_chain` 字段触发。

### 1. 工作流流程
1.  **全量配对 (Cartesian Product)**：
    *   **不使用向量检索**。
    *   使用 [CartesianCandidateGenerator](file:///d:/code/lissa/src/main/java/edu/kit/kastel/sdq/lissa/ratlr/utils/CartesianCandidateGenerator.java#18-47) 生成源端与目标端的所有组合 ($N \times M$ 对)。
2.  **SLM Ensemble 过滤**：
    *   将全量配对送入 [LLMEnsembleFilter](file:///d:/code/lissa/src/main/java/edu/kit/kastel/sdq/lissa/ratlr/classifier/ChainedLLMEnsembleFilter.java#31-141)（通过 [LLMEnsembleFilterFactory](file:///d:/code/lissa/src/main/java/edu/kit/kastel/sdq/lissa/ratlr/classifier/LLMEnsembleFilterFactory.java#13-35) 创建）。
    *   **SLM 强力筛选**：直接用 SLM（小模型）对全量组合进行初步筛选。
    *   **多数投票/链式过滤**：支持多阶段、多模型的 Majority Voting。
3.  **LLM 终裁**：
    *   经过 SLM 筛选后剩下的少量候选者送入主 `classifier`（通常是大模型 LLM）。
4.  **结果导出**：最终结果进行聚合和统计。

### 2. 特点
- **性能**：低（尤其是数据量大时，组合爆炸）。由 $O(N \times M)$ 次 SLM 调用决定。
- **优势**：**召回率理论最高**。因为它不依赖 Embedding 的空间距离，而是让 SLM 逐个审视。适用于 Embedding 效果较差或数据量较小、对准确率要求极高的场景。

---

## 核心对比摘要

| 特性 | 路径 A (Pipeline) | 路径 B (Candidate Filter Chain) |
| :--- | :--- | :--- |
| **触发方式** | 配置项 `classifiers` | 配置项 `candidate_filter_chain` |
| **主要定位** | 追求在向量检索基础上的多模型投票 | 追求通过 SLM 替代向量检索以提升召回 |
| **搜索空间** | Top-K (向量相似度) | 全部 $N \times M$ 组合 (笛卡尔积) |
| **对 Embedding 依赖** | 极高 (由 IR 决定 Recall) | 极低 (甚至可以 Mock Embedding) |
| **适用场景** | 大规模数据集，追求速度与精度的平衡 | 小规模数据集，或 Embedding 无法准确区分语义的情况 |

> [!NOTE]
> 路径 B 的设计允许我们在不改变核心 [Classifier](file:///d:/code/lissa/src/main/java/edu/kit/kastel/sdq/lissa/ratlr/classifier/Classifier.java#37-287) 逻辑的情况下，通过配置一个由小模型组成的“前置过滤器”来清理搜索空间。
