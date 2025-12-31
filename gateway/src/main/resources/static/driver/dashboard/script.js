/* ===== Driver Dashboard ===== */

const DRIVER_BASE = "/api/v1/dispatches/driver";

// ✅ 기사 전용 페이지 가드
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

// ✅ 기사 요약 정보 로드 (예: 대기 콜 수 등)
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

        // ❗ 추후에 UI에 반영하려면 아래처럼 처리
        const summaryEl = document.getElementById("driver-summary");
        if (summaryEl) {
            summaryEl.textContent = `현재 대기 중인 콜: ${count}건`;
        }

    } catch (e) {
        console.error("대기 콜 정보 조회 실패", e);
    }
}

// ✅ 운행/미결제 인디케이터 렌더링
function renderDriverIndicators() {
    const indicatorBox = document.getElementById("top-indicators");
    if (!indicatorBox) return;

    const tripId = localStorage.getItem("tripId");
    const tripStatus = localStorage.getItem("tripStatus");

    if (tripId && (tripStatus === "READY" || tripStatus === "STARTED")) {
        const tripBtn = document.createElement("button");
        tripBtn.className = "btn-indicator";
        tripBtn.innerHTML = "🚕 운행중";
        tripBtn.onclick = () => {
            const url = `/driver/trips/${tripStatus.toLowerCase()}.html?tripId=${tripId}`;
            location.href = url;
        };
        indicatorBox.appendChild(tripBtn);
    }

    // (선택) 기사 미결제 알림도 필요하면 여기에 추가
}

// ✅ 초기화
function initDriverDashboard() {
    if (!guardDriver()) return;

    renderDriverIndicators();
    loadDriverSummary();
}

// ✅ 문서 로드 시 실행
document.addEventListener("DOMContentLoaded", initDriverDashboard);
