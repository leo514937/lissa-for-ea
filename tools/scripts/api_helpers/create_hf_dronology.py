import os
import datasets
from datasets import load_dataset
import pandas as pd
import json

datasets.disable_progress_bar()

def generate_dataset():
    dataset_dir = os.path.join("tools", "datasets", "dronology_req_design_hf")
    req_dir = os.path.join(dataset_dir, "requirements")
    dd_dir = os.path.join(dataset_dir, "designs")
    config_dir = os.path.join(dataset_dir, "configs")
    
    os.makedirs(req_dir, exist_ok=True)
    os.makedirs(dd_dir, exist_ok=True)
    os.makedirs(config_dir, exist_ok=True)
    
    # Load dataset
    print("Loading artifacts...")
    ds_artifacts = load_dataset('thearod5/dronology', name='artifacts', split='train').to_pandas()
    print(f"Loaded {len(ds_artifacts)} artifacts. Columns: {ds_artifacts.columns.tolist()}")

    print("Loading trace links...")
    ds_traces = load_dataset('thearod5/dronology', name='traces', split='train').to_pandas()
    print(f"Loaded {len(ds_traces)} trace links. Columns: {ds_traces.columns.tolist()}")
    
    # Filter REQ and DD by layer or ID pattern
    if 'layer' in ds_artifacts.columns:
        reqs = ds_artifacts[ds_artifacts['layer'].str.contains('Requirement', na=False, case=False)]
        dds = ds_artifacts[ds_artifacts['layer'].str.contains('Design Definition', na=False, case=False)]
        if reqs.empty: # fallback to ID
            reqs = ds_artifacts[ds_artifacts['id'].str.startswith('RE')]
            dds = ds_artifacts[ds_artifacts['id'].str.startswith('DD')]
    else:
        reqs = ds_artifacts[ds_artifacts['id'].str.startswith('RE')]
        dds = ds_artifacts[ds_artifacts['id'].str.startswith('DD')]
        
    print(f"Found {len(reqs)} REQs and {len(dds)} DDs")
    
    req_ids = set()
    for _, row in reqs.iterrows():
        id_str = str(row['id']).strip()
        content = str(row['content']).strip()
        req_ids.add(id_str)
        with open(os.path.join(req_dir, f"{id_str}.txt"), "w", encoding="utf-8") as f:
            f.write(content)
            
    dd_ids = set()
    for _, row in dds.iterrows():
        id_str = str(row['id']).strip()
        content = str(row['content']).strip()
        dd_ids.add(id_str)
        with open(os.path.join(dd_dir, f"{id_str}.txt"), "w", encoding="utf-8") as f:
            f.write(content)

    # Filter positive traces
    positive_traces = ds_traces[ds_traces['label'] == 1]
    
    csv_lines = ["source,target"]
    trace_count = 0
    
    for _, row in positive_traces.iterrows():
        s = str(row['s_id']).strip()
        t = str(row['t_id']).strip()
        
        # We want source=REQ, target=DD
        if s in req_ids and t in dd_ids:
            csv_lines.append(f"{s}.txt,{t}.txt")
            trace_count += 1
        elif t in req_ids and s in dd_ids:
            csv_lines.append(f"{t}.txt,{s}.txt")
            trace_count += 1
            
    print(f"Found {trace_count} valid REQ-DD trace links.")
    
    ans_path = os.path.join(dataset_dir, "answer-req-design-hf.csv")
    with open(ans_path, "w", encoding="utf-8") as f:
        f.write("\n".join(csv_lines))

    # Generate JSON config files
    base_config = {
        "cache_dir": "./cache",
        "gold_standard_configuration": {
            "path": "./tools/datasets/dronology_req_design_hf/answer-req-design-hf.csv",
            "hasHeader": "true"
        },
        "source_artifact_provider": {
            "name": "text",
            "args": {
                "artifact_type": "requirement",
                "path": "./tools/datasets/dronology_req_design_hf/requirements"
            }
        },
        "target_artifact_provider": {
            "name": "text",
            "args": {
                "artifact_type": "design definition",
                "path": "./tools/datasets/dronology_req_design_hf/designs"
            }
        },
        "source_preprocessor": {
            "name": "artifact",
            "args": {}
        },
        "target_preprocessor": {
            "name": "artifact",
            "args": {}
        },
        "embedding_creator": {
            "name": "openwebui",
            "args": {
                "model": "nomic-ai/nomic-embed-text-v1.5:free"
            }
        },
        "source_store": {
            "name": "custom",
            "args": {}
        },
        "target_store": {
            "name": "custom",
            "args": {
                "max_results": "4"
            }
        },
        "classifier": {
            "name": "simple_openwebui",
            "args": {
                "model": "mistralai/mistral-small-3.1-24b-instruct:free"
            }
        },
        "result_aggregator": {
            "name": "any_connection",
            "args": {}
        },
        "tracelinkid_postprocessor": {
            "name": "identity",
            "args": {}
        }
    }
    
    before_config = dict(base_config)
    
    after_config = dict(base_config)
    after_config["classifier"] = {
        "name": "simple_openwebui",
        "args": {
            "model": "mistralai/mistral-small-3.1-24b-instruct:free"
        }
    }
    after_config["candidate_filter_chain"] = [
        [
            {
                "name": "simple_openwebui",
                "args": {
                    "model": "google/gemma-2-9b-it:free"
                }
            }
        ]
    ]

    with open(os.path.join(config_dir, "before-req-design-hf.json"), "w", encoding="utf-8") as f:
        json.dump(before_config, f, indent=4)
        
    with open(os.path.join(config_dir, "after-req-design-hf.json"), "w", encoding="utf-8") as f:
        json.dump(after_config, f, indent=4)
        
    print(f"Dataset completely generated at: {dataset_dir}")

if __name__ == "__main__":
    generate_dataset()
