import requests

url = "https://openrouter.ai/api/v1/models"
response = requests.get(url)
models = response.json().get("data", [])
for m in models:
    if m.get("architecture", {}).get("modality") == "text->vector": # Some have this
        print(f"{m['id']}: {m.get('pricing')}")
    # Or just heuristic
    if "embed" in m["id"] or "vector" in m["id"]:
          print(f"{m['id']}: {m.get('pricing')}")
