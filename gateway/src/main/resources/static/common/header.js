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
    if (!email) return "사용자";
    return email.split("@")[0];
}

function logout() {
    localStorage.clear();
    location.href = "/index.html";
}

function removeTripState() {
    localStorage.removeItem("tripId");
    localStorage.removeItem("tripStatus");
}

/* ===== 서버 기준 운행 상태 동기화 ===== */

async function syncPassengerTripStatus() {
    const token = getToken();
    const uuid = getUserUuid();
    if (!token || !uuid) return;

    try {
        const res = await fetch("/api/v1/trips/passengers/active", {
            headers: {
                "Authorization": `Bearer ${token}`,
                "X-User-UUID": uuid,
                "X-User-Role": "PASSENGER"
            }
        });

        if (!res.ok) {
            removeTripState();
            return;
        }

        const json = await res.json();
        const trip = json?.data;

        if (!trip || trip.status === "ENDED" || trip.status === "CANCELLED") {
            removeTripState();
            return;
        }

        localStorage.setItem("tripId", trip.tripId);
        localStorage.setItem("tripStatus", trip.status);

    } catch (e) {
        console.warn("🚨 승객 운행 상태 동기화 실패", e);
        removeTripState();
    }
}

async function syncDriverTripStatus() {
    const token = getToken();
    const uuid = getUserUuid();
    if (!token || !uuid) return;

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

        const json = await res.json();
        const trip = json?.data;

        if (!trip || trip.status === "ENDED" || trip.status === "CANCELLED") {
            removeTripState();
            return;
        }

        localStorage.setItem("tripId", trip.tripId);
        localStorage.setItem("tripStatus", trip.status);

    } catch (e) {
        console.warn("🚨 기사 운행 상태 동기화 실패", e);
        removeTripState();
    }
}

/* ===== 서버 기준 미결제 상태 동기화 ===== */

async function syncUnpaidPayment() {
    const token = getToken();
    const uuid = getUserUuid();
    if (!token || !uuid) return;

    const searchParams = new URLSearchParams({
        page: "0",
        size: "1",
        status: "REQUESTED",
        searchPeriod: "ALL",
        sortBy: "createdAt",
        sortAscending: "false" // ✅ 문자열로 반드시!
    });

    try {
        const res = await fetch(`/api/v1/payments/search?${searchParams.toString()}`, {
            headers: {
                "Authorization": `Bearer ${token}`,
                "X-User-UUID": uuid,
                "X-User-Role": "PASSENGER"
            }
        });

        if (!res.ok) {
            localStorage.removeItem("unpaidTrip");
            return;
        }

        const json = await res.json();
        const list = json?.data?.content ?? [];

        if (list.length === 0) {
            localStorage.removeItem("unpaidTrip");
            return;
        }

        localStorage.setItem("unpaidTrip", JSON.stringify(list[0]));

    } catch (e) {
        console.warn("🚨 미결제 상태 동기화 실패", e);
        localStorage.removeItem("unpaidTrip");
    }
}

/* ===== Indicator Rendering ===== */

function renderPassengerIndicators() {
    const indicatorBox = document.getElementById("top-indicators") || document.getElementById("header-indicators");
    if (!indicatorBox) return;

    indicatorBox.innerHTML = "";

    const tripId = localStorage.getItem("tripId");
    const tripStatus = localStorage.getItem("tripStatus");

    if (tripId && (tripStatus === "READY" || tripStatus === "STARTED")) {
        const tripBtn = document.createElement("button");
        tripBtn.className = "btn-indicator";
        tripBtn.innerHTML = "🚕 운행중";
        tripBtn.onclick = () => {
            location.href = `/passenger/trips/${tripStatus.toLowerCase()}.html?tripId=${tripId}`;
        };
        indicatorBox.appendChild(tripBtn);
    }

    const unpaid = localStorage.getItem("unpaidTrip");
    if (unpaid) {
        const payBtn = document.createElement("button");
        payBtn.className = "btn-indicator warning";
        payBtn.innerHTML = "💳 미결제";
        payBtn.onclick = () => {
            location.href = "/passenger/payments/index.html?filter=UNPAID";
        };
        indicatorBox.appendChild(payBtn);
    }
}

function renderDriverIndicators() {
    const indicatorBox = document.getElementById("top-indicators") || document.getElementById("header-indicators");
    if (!indicatorBox) return;

    indicatorBox.innerHTML = "";

    const tripId = localStorage.getItem("tripId");
    const tripStatus = localStorage.getItem("tripStatus");

    if (tripId && (tripStatus === "READY" || tripStatus === "STARTED")) {
        const tripBtn = document.createElement("button");
        tripBtn.className = "btn-indicator";
        tripBtn.innerHTML = "🚕 운행중";
        tripBtn.onclick = () => {
            location.href = `/driver/trips/${tripStatus.toLowerCase()}.html?tripId=${tripId}`;
        };
        indicatorBox.appendChild(tripBtn);
    }
}

/* ===== Init ===== */

document.addEventListener("DOMContentLoaded", async () => {
    const headerContainer = document.querySelector("header");
    if (!headerContainer) return;

    try {
        const res = await fetch("/common/header.html");
        headerContainer.innerHTML = await res.text();
    } catch (e) {
        console.error("header.html 로드 실패", e);
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

    const role = getRole();

    if (role === "PASSENGER") {
        await syncPassengerTripStatus();
        await syncUnpaidPayment();
        renderPassengerIndicators();
    }

    if (role === "DRIVER") {
        await syncDriverTripStatus();
        renderDriverIndicators();
    }
});
