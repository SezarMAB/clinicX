#!/bin/bash

# Cache Verification Script
# This script helps verify that caching is working correctly

echo "========================================="
echo "Cache Verification Script"
echo "========================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuration
API_URL="http://localhost:8080"
LOG_FILE="cache-verification.log"

echo -e "${YELLOW}Prerequisites:${NC}"
echo "1. Application should be running on port 8080"
echo "2. You should have a valid JWT token"
echo "3. Cache TTL should be set to a known value (e.g., 100 seconds)"
echo ""

# Check if application is running
echo -n "Checking if application is running... "
if curl -s -o /dev/null -w "%{http_code}" "$API_URL/actuator/health" | grep -q "200\|503"; then
    echo -e "${GREEN}✓${NC}"
else
    echo -e "${RED}✗${NC}"
    echo "Application not responding. Please start it first."
    exit 1
fi

echo ""
echo "Please provide the following information:"
read -p "JWT Token: " TOKEN
read -p "Tenant ID to test: " TENANT_ID
read -p "User ID (from token): " USER_ID

echo ""
echo "========================================="
echo "Test 1: Verify Cache Population"
echo "========================================="

echo "Making first request (should hit database)..."
echo ""

# Start monitoring logs in background
echo "Starting log monitoring..."
tail -f logs/spring.log 2>/dev/null | grep -E "CACHE|Database|GRANTED|DENIED" > "$LOG_FILE" &
LOG_PID=$!

sleep 2

# First request - should hit database
echo "Request 1: Switching to tenant (expect CACHE MISS)..."
START=$(date +%s%3N)
RESPONSE=$(curl -s -X POST "$API_URL/api/auth/switch-tenant?tenantId=$TENANT_ID" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\n%{http_code}")
END=$(date +%s%3N)
TIME1=$((END-START))

HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
echo "Response: HTTP $HTTP_CODE (Time: ${TIME1}ms)"

sleep 2

# Check logs for cache miss
if grep -q "CACHE MISS" "$LOG_FILE"; then
    echo -e "${GREEN}✓ Cache miss detected (database was queried)${NC}"
else
    echo -e "${YELLOW}⚠ No cache miss log found${NC}"
fi

if grep -q "Database ACCESS" "$LOG_FILE"; then
    echo -e "${GREEN}✓ Database access confirmed${NC}"
else
    echo -e "${YELLOW}⚠ No database access log found${NC}"
fi

echo ""
echo "========================================="
echo "Test 2: Verify Cache Hit"
echo "========================================="

# Clear log file for next test
> "$LOG_FILE"

echo "Request 2: Making same request (expect cache hit)..."
START=$(date +%s%3N)
RESPONSE=$(curl -s -X GET "$API_URL/api/v1/patients" \
  -H "Authorization: Bearer $TOKEN" \
  -H "X-Tenant-Id: $TENANT_ID" \
  -w "\n%{http_code}")
END=$(date +%s%3N)
TIME2=$((END-START))

HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
echo "Response: HTTP $HTTP_CODE (Time: ${TIME2}ms)"

sleep 2

# Check if request was faster (cache hit)
if [ $TIME2 -lt $((TIME1/2)) ]; then
    echo -e "${GREEN}✓ Request was faster (likely cache hit)${NC}"
    echo "  First request: ${TIME1}ms"
    echo "  Second request: ${TIME2}ms"
else
    echo -e "${YELLOW}⚠ Request time similar (may not be using cache)${NC}"
    echo "  First request: ${TIME1}ms"
    echo "  Second request: ${TIME2}ms"
fi

# Check logs for absence of cache miss
if ! grep -q "CACHE MISS" "$LOG_FILE"; then
    echo -e "${GREEN}✓ No cache miss (using cached value)${NC}"
else
    echo -e "${RED}✗ Cache miss detected (not using cache)${NC}"
fi

echo ""
echo "========================================="
echo "Test 3: Cache Metrics (if available)"
echo "========================================="

echo "Checking cache metrics..."
METRICS=$(curl -s "$API_URL/actuator/metrics/cache.gets?tag=name:tenantAccess" 2>/dev/null)

if [ ! -z "$METRICS" ]; then
    echo -e "${GREEN}Cache metrics available:${NC}"
    echo "$METRICS" | jq '.' 2>/dev/null || echo "$METRICS"
else
    echo -e "${YELLOW}Cache metrics not available (actuator may not be configured)${NC}"
fi

# Cleanup
kill $LOG_PID 2>/dev/null

echo ""
echo "========================================="
echo "Summary"
echo "========================================="

if [ $TIME2 -lt $((TIME1/2)) ]; then
    echo -e "${GREEN}✓ Cache appears to be working!${NC}"
    echo "  - First request hit database (${TIME1}ms)"
    echo "  - Second request used cache (${TIME2}ms)"
    echo "  - Performance improvement: $((100 - (TIME2*100/TIME1)))%"
else
    echo -e "${YELLOW}⚠ Cache may not be working properly${NC}"
    echo "  - Both requests took similar time"
    echo "  - Check your cache configuration"
fi

echo ""
echo "Log file saved to: $LOG_FILE"
echo ""
echo "To manually verify, check your application logs for:"
echo "  - 'CACHE MISS' on first request"
echo "  - No 'CACHE MISS' on subsequent requests"
echo "  - '🗑️ CACHE EVICTED' when access is revoked"