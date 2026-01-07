/* ==================================================
 * 공통 유틸
 * ================================================== */
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

/* ==================================================
 * Trip State 관리
 * ================================================== */
function clearDriverTripState() {
    localStorage.removeItem("tripId");
    localStorage.removeItem("tripStatus");
}

function clearPassengerTripState() {
    localStorage.removeItem("tripId");
    localStorage.removeItem("tripStatus");
    localStorage.removeItem("dispatchId");
    localStorage.removeItem("hasUnpaid");
}

/* ==================================================
 * Driver 전용
 * ================================================== */
async function isValidDriverDispatch(dispatchId) {
    try {
        const res = await fetch(`/api/v1/dispatches/${dispatchId}`, {
            headers: {
                Authorization: `Bearer ${getToken()}`,
                "X-User-UUID": getUserUuid(),
                "X-User-Role": "DRIVER",
            },
        });
        if (!res.ok) return false;
        const { data } = await res.json();
        return ["TRIP_REQUEST", "TRIP_READY"].includes(data?.status);
    } catch {
        return false;
    }
}

async function syncDriverTripStatus() {
    if (getRole() !== "DRIVER") return;

    const token = getToken();
    const uuid = getUserUuid();
    if (!token || !uuid) {
        clearDriverTripState();
        return;
    }

    try {
        const res = await fetch("/api/v1/trips/drivers/active", {
            headers: {
                Authorization: `Bearer ${token}`,
                "X-User-UUID": uuid,
                "X-User-Role": "DRIVER",
            },
        });

        if (!res.ok) {
            clearDriverTripState();
            return;
        }

        const { data: trip } = await res.json();
        if (!trip?.tripId || !trip?.dispatchId) {
            clearDriverTripState();
            return;
        }

        const valid = await isValidDriverDispatch(trip.dispatchId);
        if (!valid) {
            clearDriverTripState();
            return;
        }

        if (["READY", "STARTED"].includes(trip.status)) {
            localStorage.setItem("tripId", trip.tripId);
            localStorage.setItem("tripStatus", trip.status);
        } else {
            clearDriverTripState();
        }
    } catch {
        clearDriverTripState();
    }
}

function renderDriverIndicators() {
    if (getRole() !== "DRIVER") return;

    const indicatorBox = document.getElementById("top-indicators");
    if (!indicatorBox) return;

    indicatorBox.innerHTML = "";

    const tripId = localStorage.getItem("tripId");
    const tripStatus = localStorage.getItem("tripStatus");
    if (!tripId || !["READY", "STARTED"].includes(tripStatus)) return;

    const btn = document.createElement("button");
    btn.className = "btn-indicator";
    btn.textContent = "🚕 운행중";
    btn.onclick = goToActiveTrip;

    indicatorBox.appendChild(btn);
}

/* ==================================================
 * Passenger 전용
 * ================================================== */
async function checkUnpaidTripForPassenger() {
    if (getRole() !== "PASSENGER") return;

    const token = getToken();
    const uuid = getUserUuid();
    if (!token || !uuid) return;

    try {
        const res = await fetch(
            `/api/v1/payments/search?status=PENDING&searchPeriod=ALL&page=0&size=1&sortBy=createdAt&sortAscending=false`,
            {
                headers: {
                    Authorization: `Bearer ${token}`,
                    "X-User-UUID": uuid,
                    "X-User-Role": "PASSENGER",
                },
            }
        );
        if (!res.ok) return;

        const json = await res.json();
        const payment = json?.data?.content?.[0];
        if (!payment?.tripId) return;

        const tripRes = await fetch(`/api/v1/trips/${payment.tripId}`, {
            headers: {
                Authorization: `Bearer ${token}`,
                "X-User-UUID": uuid,
                "X-User-Role": "PASSENGER",
            },
        });
        if (!tripRes.ok) return;

        const { data: trip } = await tripRes.json();
        if (trip.status !== "ENDED") return;

        localStorage.setItem("tripId", payment.tripId);
        localStorage.setItem("tripStatus", "ENDED");
        localStorage.setItem("hasUnpaid", "true");

        const indicatorBox = document.getElementById("top-indicators");
        if (!indicatorBox) return;

        const btn = document.createElement("button");
        btn.className = "btn-indicator btn-unpaid";
        btn.textContent = "💳 미결제";
        btn.onclick = goToUnpaidPage;

        indicatorBox.appendChild(btn);
    } catch (e) {
        console.warn("🚨 미결제 체크 실패", e);
    }
}

/* ==================================================
 * Header Init (🔥 중요 수정 구간)
 * ================================================== */
document.addEventListener("DOMContentLoaded", async () => {
    // ❗ header 태그 자체가 아니라 전용 컨테이너 사용
    const headerContainer = document.getElementById("header-container");
    if (!headerContainer) return;

    const res = await fetch("/common/header.html");
    headerContainer.innerHTML = await res.text();

    // 사용자명
    const userNameEl = document.getElementById("user-name");
    if (userNameEl) {
        userNameEl.textContent = `${getDisplayName()}님`;
    }

    // 서브 타이틀
    const subEl = document.getElementById("brand-sub");
    if (subEl) {
        subEl.textContent =
            getRole() === "PASSENGER" ? "승객" :
                getRole() === "DRIVER" ? "기사" :
                    getRole() === "ADMIN" ? "관리자" :
                        getRole() === "MASTER_ADMIN" ? "최고 관리자" : "서비스";
    }

    // 역할별 후처리
    if (getRole() === "DRIVER") {
        await syncDriverTripStatus();
        renderDriverIndicators();
    }

    if (getRole() === "PASSENGER") {
        await checkUnpaidTripForPassenger();
    }
});
