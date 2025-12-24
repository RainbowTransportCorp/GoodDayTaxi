/* ===== Passenger Dashboard ===== */

const PASSENGER_DISPATCH_BASE = "/api/v1/dispatches";

/**
 * 승객 전용 페이지 보호
 */
function guardPassenger() {
    const token = localStorage.getItem("accessToken");
    const role = localStorage.getItem("role");
    if (!token || role !== "PASSENGER") {
        alert("승객 전용 페이지입니다.");
        window.location.href = "/index.html";
        return false;
    }
    return true;
}

/**
 * 운행 상태 정리 (운행 종료 시 인디케이터 제거용)
 */
function clearTripStatusIfEnded() {
    const tripStatus = localStorage.getItem("tripStatus");
    if (tripStatus === "ENDED") {
        ["tripId", "tripStatus", "activeTrip"].forEach((key) =>
            localStorage.removeItem(key)
        );
    }
}

/**
 * 대시보드 요약 정보 로딩
 */
async function loadPassengerSummary() {
    const token = localStorage.getItem("accessToken");
    const uuid = localStorage.getItem("userUuid");
    const role = localStorage.getItem("role");

    const res = await fetch(PASSENGER_DISPATCH_BASE, {
        headers: {
            Authorization: `Bearer ${token}`,
            "X-User-UUID": uuid,
            "X-User-Role": role,
        },
    });

    const json = await res.json();
    if (!json.success) return;

    const list = json.data;
    console.log(`내 콜 내역 수: ${list.length}`);

    // 여기에 추가 정보 출력하고 싶으면 확장 가능
}

/**
 * 대시보드 초기화 진입점
 */
function initPassengerDashboard() {
    if (!guardPassenger()) return;

    clearTripStatusIfEnded(); // ✅ 진입 시 운행 종료 상태라면 정리
    loadPassengerSummary();
}

document.addEventListener("DOMContentLoaded", initPassengerDashboard);

/* ===== 인디케이터 이동 버튼 함수 ===== */

function goToActiveTrip() {
    const tripId = localStorage.getItem("tripId");
    const status = localStorage.getItem("tripStatus");
    if (!tripId || !status) return;

    location.href =
        status === "READY"
            ? `/passenger/trips/ready.html?tripId=${tripId}`
            : `/passenger/trips/active.html?tripId=${tripId}`;
}

function goToUnpaidPage() {
    location.href = "/passenger/payments/index.html?filter=PENDING";
}
