/* ===== Driver Dashboard ===== */

const DRIVER_BASE = "/api/v1/dispatches/driver";

function guardDriver() {
    const token = localStorage.getItem("accessToken");
    const role = localStorage.getItem("role");

    if (!token || role !== "DRIVER") {
        alert("기사 전용 페이지입니다.");
        window.location.href = "/index.html";
        return false;
    }
    return true;
}

async function loadDriverSummary() {
    const token = localStorage.getItem("accessToken");
    const uuid = localStorage.getItem("userUuid");
    const role = localStorage.getItem("role");

    try {
        const res = await fetch(`${DRIVER_BASE}/pending`, {
            headers: {
                "Authorization": `Bearer ${token}`,
                "X-User-UUID": uuid,
                "X-User-Role": role,
            },
        });

        const json = await res.json();
        if (!json.success) return;

        const count = json.data.length;
        console.log(`현재 대기 콜: ${count}`);
        // 추후 카드 UI에 반영 예정
    } catch (e) {
        console.error("대기 콜 정보 조회 실패", e);
    }
}

/* ===== 운행중 버튼 헤더에 표시 ===== */
function renderTripStatusButton() {
    const tripStatus = localStorage.getItem("tripStatus");
    const tripId = localStorage.getItem("tripId");

    if (tripId && (tripStatus === "READY" || tripStatus === "STARTED")) {
        const brandBox = document.querySelector(".brand");
        if (!brandBox) return;

        const btn = document.createElement("button");
        btn.textContent = "🟢 운행중";
        btn.className = "btn-trip-status";
        btn.onclick = () => {
            const url = tripStatus === "READY"
                ? `/driver/trips/ready.html?tripId=${tripId}`
                : `/driver/trips/active.html?tripId=${tripId}`;
            location.href = url;
        };

        brandBox.appendChild(btn); // 헤더 왼쪽에 붙임
    }
}

/* ===== 초기화 ===== */
function initDriverDashboard() {
    if (!guardDriver()) return;
    renderTripStatusButton();
    loadDriverSummary();
}

document.addEventListener("DOMContentLoaded", initDriverDashboard);
