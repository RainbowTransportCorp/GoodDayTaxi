/* ===== Common Header Logic ===== */

function getToken() {
    return localStorage.getItem("accessToken");
}
function getRole() {
    return localStorage.getItem("role");
}
function getEmail() {
    return localStorage.getItem("email");
}
function getUserUuid() {
    return localStorage.getItem("userUuid");
}
function getDisplayName() {
    const email = getEmail();
    return email ? email.split("@")[0] : "사용자";
}
function logout() {
    localStorage.clear();
    location.href = "/index.html";
}
function removeTripState() {
    localStorage.removeItem("tripId");
    localStorage.removeItem("tripStatus");
}

/* ================= 공통: 디스패치 유효성 검사 ================= */
async function isValidDriverDispatch(dispatchId) {
    try {
        const res = await fetch(`/api/v1/dispatches/${dispatchId}`, {
            headers: {
                "Authorization": `Bearer ${getToken()}`,
                "X-User-UUID": getUserUuid(),
                "X-User-Role": "DRIVER"
            }
        });

        if (!res.ok) return false;
        const { data } = await res.json();
        return data && ["TRIP_REQUEST", "TRIP_READY"].includes(data.status);
    } catch (e) {
        console.warn("🚨 디스패치 상태 확인 실패", e);
        return false;
    }
}

/* ================= 서버 기준 운행 상태 동기화 (기사) ================= */
async function syncDriverTripStatus() {
    const token = getToken();
    const uuid = getUserUuid();
    if (!token || !uuid) {
        removeTripState();
        return;
    }

    try {
        const res = await fetch("/api/v1/trips/drivers/active", {
            headers: {
                "Authorization": `Bearer ${token}`,
                "X-User-UUID": uuid,
                "X-User-Role": "DRIVER"
            }
        });

        if (!res.ok) {
            removeTripState();
            return;
        }

        const { data: trip } = await res.json();
        if (!trip?.tripId || !trip?.dispatchId) {
            removeTripState();
            return;
        }

        const valid = await isValidDriverDispatch(trip.dispatchId);
        if (!valid) {
            removeTripState();
            return;
        }

        if (["READY", "STARTED"].includes(trip.status)) {
            localStorage.setItem("tripId", trip.tripId);
            localStorage.setItem("tripStatus", trip.status);
        } else {
            removeTripState();
        }

    } catch (e) {
        console.warn("🚨 기사 운행 상태 동기화 실패", e);
        removeTripState();
    }
}

/* ================= 미결제 건 확인 (승객) ================= */
/* ================= 미결제 건 확인 (승객) ================= */
async function checkUnpaidTripForPassenger() {
    const token = getToken();
    const uuid = getUserUuid();
    if (!token || !uuid || getRole() !== "PASSENGER") return;

    try {
        const res = await fetch(
            `/api/v1/payments/search?status=PENDING&searchPeriod=ALL&page=0&size=1&sortBy=createdAt&sortAscending=false`,
            {
                headers: {
                    "Authorization": `Bearer ${token}`,
                    "X-User-UUID": uuid,
                    "X-User-Role": "PASSENGER"
                }
            }
        );

        if (!res.ok) return;

        const json = await res.json();
        const content = json?.data?.content;
        if (!content || content.length === 0) return;

        const payment = content[0];
        const tripId = payment.tripId;
        const method = payment.method;

        // ✅ TOSS_PAY만 인디케이터 표시
        if (!tripId || method !== "TOSS_PAY") return;

        localStorage.setItem("tripId", tripId);
        localStorage.setItem("tripStatus", "ENDED");

        const indicatorBox = document.getElementById("top-indicators") || document.getElementById("header-indicators");
        if (!indicatorBox) return;

        const btn = document.createElement("button");
        btn.className = "btn-indicator";
        btn.innerHTML = "💳 미결제";
        btn.onclick = () => {
            location.href = `/passenger/trips/ended.html?tripId=${tripId}`;
        };
        indicatorBox.appendChild(btn);

    } catch (e) {
        console.warn("🚨 승객 미결제 확인 실패", e);
    }
}


/* ================= Indicator Rendering ================= */
function renderDriverIndicators() {
    const indicatorBox =
        document.getElementById("top-indicators") ||
        document.getElementById("header-indicators");

    if (!indicatorBox) return;
    indicatorBox.innerHTML = "";

    const tripId = localStorage.getItem("tripId");
    const tripStatus = localStorage.getItem("tripStatus");

    if (!tripId || !["READY", "STARTED"].includes(tripStatus)) return;

    const btn = document.createElement("button");
    btn.className = "btn-indicator";
    btn.innerHTML = "🚕 운행중";
    btn.onclick = () => {
        location.href = `/driver/trips/${tripStatus.toLowerCase()}.html?tripId=${tripId}`;
    };

    indicatorBox.appendChild(btn);
}

/* ================= Init ================= */
document.addEventListener("DOMContentLoaded", async () => {
    const headerContainer = document.querySelector("header");
    if (!headerContainer) return;

    try {
        const res = await fetch("/common/header.html");
        headerContainer.innerHTML = await res.text();
    } catch (e) {
        console.error("❌ header.html 로드 실패", e);
        return;
    }

    const nameEl = document.getElementById("user-name");
    if (nameEl) nameEl.textContent = `${getDisplayName()}님`;

    const subEl = document.getElementById("brand-sub");
    if (subEl) {
        const role = getRole();
        subEl.textContent =
            role === "PASSENGER" ? "승객" :
                role === "DRIVER" ? "기사" :
                    role === "ADMIN" ? "관리자" :
                        role === "MASTER_ADMIN" ? "최고 관리자" : "서비스";
    }

    if (getRole() === "DRIVER") {
        await syncDriverTripStatus();
        renderDriverIndicators();
    } else if (getRole() === "PASSENGER") {
        await checkUnpaidTripForPassenger();
    }
});
