## Configuration (2026-03-11_17-33+0800 -- 20260311-173304_simple-config.json_6e2a4f27-3092-3da7-a74e-f6bf2380ef92)
```json
{
  "cache_dir" : "./cache/dronology-req-design-scheme1",
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
      "max_results" : "40"
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
  }
}
```

## Stats
* #TraceLinks (GS): 25
* #Source Artifacts: 30
* #Target Artifacts: 30
## Results
* True Positives: 23
* False Positives: 256
* False Negatives: 2
* Precision: 0.08243727598566308
* Recall: 0.92
* F1: 0.15131578947368418
