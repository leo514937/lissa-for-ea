## Configuration (2026-03-11_20-30+0800 -- 20260311-203014_simple-config-scheme2-layered.json_5e9ffba9-9aad-359c-80fa-85aa4d736378)
```json
{
  "cache_dir" : "./cache/dronology-req-design-scheme2-layered",
  "gold_standard_configuration" : {
    "path" : "./tools/datasets/dronology_req_design/answer-subset.csv",
    "hasHeader" : true,
    "swap_columns" : false
  },
  "source_artifact_provider" : {
    "name" : "text",
    "args" : {
      "artifact_type" : "requirement",
      "path" : "./tools/datasets/dronology_req_design/requirements"
    }
  },
  "target_artifact_provider" : {
    "name" : "text",
    "args" : {
      "artifact_type" : "software architecture documentation",
      "path" : "./tools/datasets/dronology_req_design/designs"
    }
  },
  "source_preprocessor" : {
    "name" : "artifact",
    "args" : { }
  },
  "target_preprocessor" : {
    "name" : "artifact",
    "args" : { }
  },
  "embedding_creator" : {
    "name" : "openwebui",
    "args" : {
      "model" : "text-embedding-3-small"
    }
  },
  "source_store" : {
    "name" : "custom",
    "args" : { }
  },
  "target_store" : {
    "name" : "cosine_similarity",
    "args" : {
      "max_results" : "100"
    }
  },
  "classifier" : {
    "name" : "simple_openwebui",
    "args" : {
      "model" : "deepseek-chat",
      "temperature" : "0.0",
      "seed" : "133742243",
      "template" : "Question: Here are two parts of software development artifacts.\n\n{source_type}: '''{source_content}'''\n\n{target_type}: '''{target_content}'''\nAre they related?\n\nAnswer with 'yes' or 'no'.\n"
    }
  },
  "result_aggregator" : {
    "name" : "any_connection",
    "args" : {
      "source_granularity" : "0",
      "target_granularity" : "0"
    }
  },
  "tracelinkid_postprocessor" : {
    "name" : "identity",
    "args" : { }
  },
  "candidate_filter_mode" : "layered",
  "candidate_filter_layered_chain" : [ [ {
    "name" : "simple_openwebui",
    "args" : {
      "model" : "qwen3.5-flash",
      "temperature" : "0.0",
      "seed" : "133742243",
      "template" : "Question: Here are two parts of software development artifacts.\n\n{source_type}: '''{source_content}'''\n\n{target_type}: '''{target_content}'''\nAre they related?\n\nAnswer with 'yes' or 'no'.\n"
    }
  } ], [ {
    "name" : "simple_openwebui",
    "args" : {
      "model" : "qwen3.5-flash",
      "temperature" : "0.1",
      "seed" : "133742243",
      "template" : "Question: Here are two parts of software development artifacts.\n\n{source_type}: '''{source_content}'''\n\n{target_type}: '''{target_content}'''\nAre they related?\n\nAnswer with 'yes' or 'no'.\n"
    }
  } ], [ {
    "name" : "simple_openwebui",
    "args" : {
      "model" : "qwen3.5-flash",
      "temperature" : "0.2",
      "seed" : "133742243",
      "template" : "Question: Here are two parts of software development artifacts.\n\n{source_type}: '''{source_content}'''\n\n{target_type}: '''{target_content}'''\nAre they related?\n\nAnswer with 'yes' or 'no'.\n"
    }
  } ], [ {
    "name" : "simple_openwebui",
    "args" : {
      "model" : "qwen3.5-flash",
      "temperature" : "0.3",
      "seed" : "133742243",
      "template" : "Question: Here are two parts of software development artifacts.\n\n{source_type}: '''{source_content}'''\n\n{target_type}: '''{target_content}'''\nAre they related?\n\nAnswer with 'yes' or 'no'.\n"
    }
  } ] ]
}
```

## Stats
* #TraceLinks (GS): 25
* #Source Artifacts: 30
* #Target Artifacts: 30
## Results
* True Positives: 18
* False Positives: 142
* False Negatives: 7
* Precision: 0.1125
* Recall: 0.72
* F1: 0.1945945945945946
