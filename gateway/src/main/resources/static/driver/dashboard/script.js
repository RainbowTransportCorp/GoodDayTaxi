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

    // 지금은 pending 개수만 요약
    const res = await fetch(`${DRIVER_BASE}/pending`, {
        headers: {
            "Authorization": `Bearer ${token}`,
            "X-User-UUID": uuid,
            "X-User-Role": role
        }
    });

    const json = await res.json();
    if (!json.success) return;

    const count = json.data.length;
    console.log(`현재 대기 콜: ${count}`);
    // 👉 나중에 카드 UI에 박으면 됨
}

function initDriverDashboard() {
    if (!guardDriver()) return;
    loadDriverSummary();
}

document.addEventListener("DOMContentLoaded", initDriverDashboard);
