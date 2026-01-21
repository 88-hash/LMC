import requests
import json

def test_create_order():
    url = "http://localhost:8080/order/create"
    headers = {
        "Content-Type": "application/json"
    }
    data = {
        "remark": "Python测试下单"
    }
    
    try:
        print(f"Sending POST to {url}...")
        response = requests.post(url, headers=headers, json=data)
        print(f"Status Code: {response.status_code}")
        print(f"Response: {response.text}")
    except Exception as e:
        print(f"Error: {e}")

if __name__ == "__main__":
    test_create_order()
