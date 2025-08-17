#!/bin/bash

echo "=== Testing OAuth2 Resource Server ==="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

AUTH_SERVER="http://localhost:9000"
RESOURCE_SERVER="http://localhost:8081"

echo -e "${YELLOW}1. Testing Public Endpoints (không cần token)${NC}"
echo "GET $RESOURCE_SERVER/api/public/health"
curl -s "$RESOURCE_SERVER/api/public/health" | jq .
echo ""

echo "GET $RESOURCE_SERVER/api/public/info"
curl -s "$RESOURCE_SERVER/api/public/info" | jq .
echo -e "\n"

echo -e "${YELLOW}2. Testing Protected Endpoints WITHOUT token (should fail)${NC}"
echo "GET $RESOURCE_SERVER/api/users (without token)"
curl -s -w "HTTP Status: %{http_code}\n" "$RESOURCE_SERVER/api/users" | head -1
echo -e "\n"

echo -e "${YELLOW}3. Getting token từ Authorization Server${NC}"
echo "Sử dụng Client Credentials flow với readonly-client..."

TOKEN_RESPONSE=$(curl -s -X POST "$AUTH_SERVER/oauth2/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -u "my-client:my-secret" \
  -d "grant_type=client_credentials&scope=read write")

echo "Token response:"
echo $TOKEN_RESPONSE | jq .

ACCESS_TOKEN=$(echo $TOKEN_RESPONSE | jq -r '.access_token')

if [ "$ACCESS_TOKEN" = "null" ] || [ -z "$ACCESS_TOKEN" ]; then
    echo -e "${RED}❌ Không thể lấy được access token!${NC}"
    echo "Kiểm tra lại Authorization Server có đang chạy tại port 9000?"
    exit 1
fi

echo -e "${GREEN}✅ Access token obtained successfully${NC}"
echo -e "\n"

echo -e "${YELLOW}4. Testing với Access Token${NC}"

echo "GET $RESOURCE_SERVER/api/token/info (kiểm tra token info)"
curl -s -H "Authorization: Bearer $ACCESS_TOKEN" "$RESOURCE_SERVER/api/token/info" | jq .
echo -e "\n"

echo "GET $RESOURCE_SERVER/api/users (scope: read)"
curl -s -H "Authorization: Bearer $ACCESS_TOKEN" "$RESOURCE_SERVER/api/users" | jq .
echo -e "\n"

echo "GET $RESOURCE_SERVER/api/posts (scope: read)"
curl -s -H "Authorization: Bearer $ACCESS_TOKEN" "$RESOURCE_SERVER/api/posts" | jq .
echo -e "\n"

echo "GET $RESOURCE_SERVER/api/products (scope: read)"
curl -s -H "Authorization: Bearer $ACCESS_TOKEN" "$RESOURCE_SERVER/api/products" | jq .
echo -e "\n"

echo -e "${YELLOW}5. Testing POST request (scope: write)${NC}"
echo "POST $RESOURCE_SERVER/api/posts (tạo bài viết mới)"
curl -s -X POST "$RESOURCE_SERVER/api/posts" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Test Post from OAuth2 Client",
    "content": "Đây là bài viết test được tạo thông qua OAuth2 flow"
  }' | jq .
echo -e "\n"

echo "POST $RESOURCE_SERVER/api/products (tạo sản phẩm mới)"
curl -s -X POST "$RESOURCE_SERVER/api/products" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Product",
    "description": "Sản phẩm test từ OAuth2",
    "price": 199.99,
    "stock": 100,
    "category": "Test"
  }' | jq .
echo -e "\n"

echo -e "${YELLOW}6. Testing Authorization Code Flow (manual)${NC}"
echo "Để test Authorization Code flow, mở browser và truy cập:"
echo "${AUTH_SERVER}/oauth2/authorize?response_type=code&client_id=my-client&redirect_uri=http://localhost:8080/callback&scope=read+write"
echo ""
echo "Sau khi đăng nhập (admin/password) và đồng ý, bạn sẽ được redirect với authorization code"
echo "Sau đó exchange code để lấy token:"
echo "curl -X POST '${AUTH_SERVER}/oauth2/token' \\"
echo "  -H 'Content-Type: application/x-www-form-urlencoded' \\"
echo "  -u 'my-client:my-secret' \\"
echo "  -d 'grant_type=authorization_code&code=YOUR_CODE&redirect_uri=http://localhost:8080/callback'"

echo -e "\n${GREEN}=== Test completed ===${NC}"