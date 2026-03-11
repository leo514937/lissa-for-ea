# 项目进展文档 (Progress Report)

## 当前状态
- **API 配置**: 已配置 `dmxapi.cn` API 密钥和基础 URL。
- **预算**: 20 RMB (预计可处理 ~1000万 tokens)。
- **推荐模型**:
    - **LLM (大模型)**: `deepseek-chat` (DeepSeek-V3) - 极高性价比。
    - **SLM (小模型/过滤器)**: `qwen-flash-free` - 已替换原有的 gpt-4o-mini/gemini-flash。
    - **Embedding (嵌入模型)**: `text-embedding-3-small` - 解决原 nvidia 模型失效问题。

## 已完成任务
1. 更新 `.env` 文件，接入 `dmxapi.cn` 服务。
2. 制定高性价比模型组合方案。
3. 创建新的配置文件 `cheap-efficient.json`，配置了上述模型组合。
4. **深入解析方案二架构**: 明确了 `voting` 和 `layered` 两种模式的工作原理及切换逻辑。
5. **代码文档化**: 在 `ChainedLLMEnsembleFilter.java` 和 `Evaluation.java` 等处完成了详细的架构与调用链注释。
6. **提取子集黄金标准**: 利用 PowerShell 脚本构建了 `answer-subset.csv` 并更新了配置。
7. **模型全面升级与修复**:
    - 将所有过滤环节的小模型统一替换为 `qwen-flash-free`。
    - 将核心分类器大模型统一替换为 `deepseek-chat`。
    - 修复了嵌入模型 `nvidia/llama-nemotron` 渠道不可用的问题，切换为 `text-embedding-3-small`。
8. **启动全量评估**: 成功在子集数据上启动了自动化运行脚本 `run_dronology_configs.ps1`。
9. **对比分析指标**: 完成了 `simple`, `voting`, `layered` 三种模式在子集上的指标对比。
    - **Layered** 模式 F1 (0.1837) 与 Precision (0.1053) 最优，有效降低误报。
    - **Simple** 模式 Recall (0.9200) 最高，但误报极多。
    - **Voting** 模式性能介于两者之间。

## 下一步计划
1. **优化分层过滤参数**: 调整 `layered` 模式下的阈值或模型组合，进一步提升 Precision。
2. **应用至全量数据集**: 将验证后的 `layered` 架构应用到 `hf` (全集) 数据集进行正式评估。
3. **成本审计**: 精确统计各模式的 Token 消耗与实际花费。
