/* ===== Passenger Dashboard ===== */

const PASSENGER_DISPATCH_BASE = "/api/v1/dispatches";

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


function goToActiveTrip() {
  const tripId = localStorage.getItem("tripId");
  const status = localStorage.getItem("tripStatus");
  if (!tripId || !status) return;

  location.href = status === "READY"
    ? `/passenger/trips/ready.html?tripId=${tripId}`
    : `/passenger/trips/active.html?tripId=${tripId}`;
}

function goToUnpaidPage() {
  location.href = "/passenger/payments/index.html?filter=UNPAID";
}

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

async function renderPassengerIndicators() {
  const tripId = localStorage.getItem("tripId") ?? "";
  const status = localStorage.getItem("tripStatus") ?? "";
  const tripCard = document.getElementById("trip-active-card");
  const unpaidCard = document.getElementById("unpaid-card");

  if (tripCard) tripCard.classList.add("hidden");
  if (unpaidCard) unpaidCard.classList.add("hidden");

  if (tripId && (status === "READY" || status === "STARTED")) {
    tripCard?.classList.remove("hidden");
  }

  try {
    const res = await fetch("/api/v1/payments/unpaid", {
      headers: {
        "Authorization": `Bearer ${localStorage.getItem("accessToken")}`,
        "X-User-UUID": localStorage.getItem("userUuid"),
        "X-User-Role": "PASSENGER"
      }
    });

    if (res.ok) {
      const json = await res.json();
      if (json?.success && Array.isArray(json.data) && json.data.length > 0) {
        unpaidCard?.classList.remove("hidden");
      }
    }
  } catch (e) {
    console.warn("미결제 확인 실패", e);
  }
}

document.addEventListener("DOMContentLoaded", () => {
  if (!guardPassenger()) return;
  renderPassengerIndicators();
});
