import requests

url = "https://openrouter.ai/api/v1/models"
response = requests.get(url)
models = response.json().get("data", [])
for m in models:
    if "embed" in m["id"] or "embedding" in m["id"]:
        pricing = m.get("pricing", {})
        print(f"{m['id']}: {pricing}")
