import requests

url = "https://openrouter.ai/api/v1/models"
response = requests.get(url)
models = response.json().get("data", [])
for m in models:
    print(m["id"])
