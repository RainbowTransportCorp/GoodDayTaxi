#!/bin/bash

set -e

echo "==============================="
echo "🚀 Starting GoodDayTaxi common modules auto build"
echo "==============================="

COMMON_CORE="common-core"
COMMON_JPA="common-jpa"

#######################################
# Version bump (find SNAPSHOT or not, save as SNAPSHOT)
#######################################
get_next_version() {
    MODULE_PATH=$1
    FILE="$MODULE_PATH/build.gradle"

    # version = '0.0.5' or version = '0.0.5-SNAPSHOT' 둘 다 허용
    RAW_VERSION=$(grep -oE "version\s*=\s*'[0-9]+\.[0-9]+\.[0-9]+(-SNAPSHOT)?'" "$FILE")

    if [[ -z "$RAW_VERSION" ]]; then
        echo "❌ version not found in $FILE"
        exit 1
    fi

    # 숫자 부분만 뽑기
    VERSION=$(echo "$RAW_VERSION" | grep -oE "[0-9]+\.[0-9]+\.[0-9]+")
    IFS='.' read -r major minor patch <<< "$VERSION"

    patch=$((patch + 1))
    NEW_VERSION="${major}.${minor}.${patch}-SNAPSHOT"

    # version 라인을 SNAPSHOT 포함한 새 버전으로 교체
    if [[ "$OSTYPE" == "darwin"* ]]; then
        sed -i '' "s/version\s*=\s*'.*'/version = '$NEW_VERSION'/" "$FILE"
    else
        sed -i "s/version\s*=\s*'.*'/version = '$NEW_VERSION'/" "$FILE"
    fi

    echo "$NEW_VERSION"
}


###############################################
# 1) Update core version
###############################################
echo ""
echo "📦 Updating common-core version"
CORE_VERSION=$(get_next_version "$COMMON_CORE")

#######################################
# STEP: After core version bump → update jpa dependency on core
#######################################
echo ""
echo "🔧 Updating common-jpa core dependency → $CORE_VERSION"

JPA_BUILD_FILE="$COMMON_JPA/build.gradle"

# 안전한 패턴: implementation 'com.gooddaytaxi:common-core:ANYTHING'
perl -i -pe "s#implementation 'com\.gooddaytaxi:common-core:[^']+'#implementation 'com.gooddaytaxi:common-core:$CORE_VERSION'#" "$JPA_BUILD_FILE"

echo "✔ common-jpa now depends on: common-core:$CORE_VERSION"


###############################################
# 2) Update jpa version
###############################################
echo ""
echo "📦 Updating common-jpa version"
JPA_VERSION=$(get_next_version "$COMMON_JPA")

###############################################
# 3) Publish both modules to MavenLocal
###############################################
echo ""
echo "🔨 Building and publishing common-core"
( cd "$COMMON_CORE" && ./gradlew clean publishToMavenLocal )

echo ""
echo "🔨 Building and publishing common-jpa"
( cd "$COMMON_JPA" && ./gradlew clean publishToMavenLocal )

###############################################
# 4) Update service build.gradle dependencies
###############################################
SERVICES=(
    "account-service"
    "dispatch-service"
    "trip-service"
    "payment-service"
    "support-service"
)

echo ""
echo "🔧 Updating dependency versions in services..."

for svc in "${SERVICES[@]}"; do
    FILE="$svc/build.gradle"

    if [[ -f "$FILE" ]]; then

        if [[ "$OSTYPE" == "darwin"* ]]; then
            sed -i '' "s/common-core:[0-9]\+\.[0-9]\+\.[0-9]\+\(-SNAPSHOT\)\?/common-core:$CORE_VERSION/" "$FILE"
            sed -i '' "s/common-jpa:[0-9]\+\.[0-9]\+\.[0-9]\+\(-SNAPSHOT\)\?/common-jpa:$JPA_VERSION/" "$FILE"
        else
            sed -i "s/common-core:[0-9]\+\.[0-9]\+\.[0-9]\+\(-SNAPSHOT\)\?/common-core:$CORE_VERSION/" "$FILE"
            sed -i "s/common-jpa:[0-9]\+\.[0-9]\+\.[0-9]\+\(-SNAPSHOT\)\?/common-jpa:$JPA_VERSION/" "$FILE"
        fi

        echo "✔ Updated $svc"
    fi
done

###############################################
# 5) Rebuild services with refresh
###############################################
for svc in "${SERVICES[@]}"; do
    echo ""
    echo "🚀 Refreshing dependencies for $svc"
    ( cd "$svc" && ./gradlew clean build --refresh-dependencies )
done

echo ""
echo "🎉 Done! All modules updated and rebuilt successfully."
