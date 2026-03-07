import requests

url = "https://openrouter.ai/api/v1/credits"
key = "your_api_key_here"

headers = {
    "Authorization": f"Bearer {key}",
}

response = requests.get(url, headers=headers)
print(f"Status: {response.status_code}")
print(f"Response: {response.text}")
