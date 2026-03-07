import requests

url = "https://openrouter.ai/api/v1/models"
response = requests.get(url)
models = response.json().get("data", [])
free_models = [m["id"] for m in models if "free" in m["id"] or m.get("pricing", {}).get("prompt", "1") == "0"]
for fm in sorted(free_models):
    print(fm)
