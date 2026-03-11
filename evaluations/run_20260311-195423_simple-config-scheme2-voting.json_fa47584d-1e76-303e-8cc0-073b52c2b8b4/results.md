## Configuration (2026-03-11_19-54+0800 -- 20260311-195423_simple-config-scheme2-voting.json_fa47584d-1e76-303e-8cc0-073b52c2b8b4)
```json
{
  "cache_dir" : "./cache/dronology-req-design-scheme2-voting",
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
  "candidate_filter_mode" : "voting",
  "candidate_filter_vote_chain" : [ [ {
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
      "temperature" : "0.0",
      "seed" : "133742243",
      "template" : "Question: Here are two parts of software development artifacts.\n\n{source_type}: '''{source_content}'''\n\n{target_type}: '''{target_content}'''\nAre they related?\n\nAnswer with 'yes' or 'no'.\n"
    }
  } ], [ {
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
      "temperature" : "0.0",
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
* True Positives: 19
* False Positives: 170
* False Negatives: 6
* Precision: 0.10052910052910052
* Recall: 0.76
* F1: 0.17757009345794394
