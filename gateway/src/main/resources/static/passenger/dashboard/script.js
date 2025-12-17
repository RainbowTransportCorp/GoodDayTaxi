/* ===== Passenger Dashboard ===== */

const PASSENGER_DISPATCH_BASE = "/api/v1/dispatches";

function guardPassenger() {
    const token = localStorage.getItem("accessToken");
    const role = localStorage.getItem("role");

    if (!token || role !== "PASSENGER") {
        alert("승객 전용 페이지입니다.");
        window.location.href = "/login/index.html";
        return false;
    }
    return true;
}

async function loadPassengerSummary() {
    const token = localStorage.getItem("accessToken");
    const uuid = localStorage.getItem("userUuid");
    const role = localStorage.getItem("role");

    const res = await fetch(PASSENGER_DISPATCH_BASE, {
        headers: {
            "Authorization": `Bearer ${token}`,
            "X-User-UUID": uuid,
            "X-User-Role": role
        }
    });

    const json = await res.json();
    if (!json.success) return;

    const list = json.data;
    console.log(`내 콜 내역 수: ${list.length}`);

    // 확장 포인트
    // - 최근 콜 상태
    // - 진행 중 콜 여부
}

function initPassengerDashboard() {
    if (!guardPassenger()) return;
    loadPassengerSummary();
}

document.addEventListener("DOMContentLoaded", initPassengerDashboard);
