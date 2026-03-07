# Before/After Comparison

- Jar: D:\code\lissa\target\lissa-0.2.0-SNAPSHOT-jar-with-dependencies.jar
- Before config: D:\code\lissa\tools\test-configs\before-mock.json
- After config: D:\code\lissa\tools\test-configs\after-mock.json

| Metric | Before | After | Delta (After-Before) |
|---|---:|---:|---:|
| Precision | 0.01984126984126984 | 0.024255395041911897 | 0.00441412520064206 |
| Recall | 0.03676470588235294 | 1.0 | 0.963235294117647 |
| F1 | 0.02577319587628866 | 0.047362005920250735 | 0.0215888100439621 |
| True Positives | 5 | 136 | N/A |
| False Positives | 247 | 5471 | N/A |
| False Negatives | 131 | 0 | N/A |
| #TraceLinks (GS) | 136 | 136 | N/A |
| #Source Artifacts | 63 | 63 | N/A |
| #Target Artifacts | 89 | 89 | N/A |
| Duration (s) | 0.52 | 0.56 | N/A |

## Raw Result Files

- Before: D:\code\lissa\results-before-mock.json_3dd74573-da17-34d0-a822-108c979a6ba2.md
- After: D:\code\lissa\results-after-mock.json_dcea26cd-0aaa-3a10-a371-4c2b02472192.md
