# 方案二 (After) 实现流程说明文档

此文档描述了现代的“多级过滤”追溯链路的 Java 代码实现逻辑。

## 1. 总体逻辑说明
方案二采用类似“漏斗”的结构。先利用向量检索圈定较大范围（如 Top 100），再利用轻量级模型（SLM）进行候选过滤，最后由大模型（LLM）精审。

## 2. 核心执行流程与代码映射

### 第一阶段：流程分支选择
1. **分支判断**: `Evaluation.run()`
   - **代码位置**: `Evaluation.java`（候选过滤分支判断处）
   - **逻辑**: 检测到候选过滤链配置后进入方案二路径。

### 第二阶段：群体过滤 (SLM Filtering with Modes)
方案二新增开关，可切换两种过滤模式，仅影响 SLM 群体过滤与批量判定。

**模式开关**
- `candidate_filter_mode`:
  - `voting`：投票表决式
  - `layered`：逐层过滤式

**模式一：投票表决式（voting）**
1. **预筛选**: 单小模型先过滤一轮。
2. **群体投票**: 三个 SLM 并行投票，达到多数票才保留。
   - **实现入口**: `ChainedLLMEnsembleFilter.filterCandidates()`
   - **逻辑**: 第一个阶段单模型过滤；后续阶段合并为投票阶段。

**模式二：逐层过滤式（layered）**
1. **逐级过滤**: 依次执行 4 个小模型（参数逐级递增），逐层筛掉低置信候选。
   - **实现入口**: `ChainedLLMEnsembleFilter.filterCandidates()`
   - **逻辑**: 各阶段串行执行，阶段内采用多数投票（单模型时即通过/淘汰）。

**批量判定**
- **核心点**: `classifier.classify(tasks)` 批量判定候选。
- **代码位置**: `ChainedLLMEnsembleFilter` 中的 `filterStage()`

### 第三阶段：最终精审 (Final Classification)
1. **精审入口**: `Evaluation.run()`
   - **功能**: 对 SLM 过滤后的高纯度候选集进行最终 LLM 判定。
2. **模型执行**: `SimpleClassifier.java`（同方案一）。

## 3. 性能特点
- **鲁棒性**: 极高。第一级召回范围很大，防止遗漏。
- **成本控制**: 通过中间的 SLM 过滤层，将进入昂贵 LLM 的数据量显著降低。
- **复杂度**: 逻辑分支较多，依赖良好的多级协同配置。
