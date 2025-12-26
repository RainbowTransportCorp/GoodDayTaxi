/* ================= 공통 ================= */
const TOKEN = localStorage.getItem("accessToken");
const UUID = localStorage.getItem("userUuid");
const ROLE = localStorage.getItem("role");

const DISPATCH_BASE = "/api/v1/dispatches/driver";

/* ================= 권한 체크 ================= */
function guardDriverPage() {
  if (!TOKEN || !UUID || ROLE !== "DRIVER") {
    alert("기사 전용 페이지입니다.");
    location.href = "/index.html";
    return false;
  }
  return true;
}

/* ================= ⭐ 핵심: 현재 운행 상태 확인 ================= */
async function checkActiveTrip() {
  try {
    const res = await fetch("/api/v1/trips/drivers/active", {
      headers: {
        "Authorization": `Bearer ${TOKEN}`,
        "X-User-UUID": UUID,
        "X-User-Role": "DRIVER"
      }
    });

    if (res.status === 401 || res.status === 403) {
      alert("세션이 만료되었습니다. 다시 로그인해주세요.");
      location.href = "/index.html";
      return;
    }

    if (res.status === 204 || res.status === 404) {
      await loadPending();
      return;
    }

    const { data: trip } = await res.json();
    if (!trip) {
      await loadPending();
      return;
    }

    // ⭐️ 디스패치 상태 확인
    const dispatchRes = await fetch(`${DISPATCH_BASE}/${trip.dispatchId}`, {
      headers: {
        "Authorization": `Bearer ${TOKEN}`,
        "X-User-UUID": UUID,
        "X-User-Role": "DRIVER"
      }
    });

    if (!dispatchRes.ok) {
      console.warn("디스패치 상태 확인 실패");
      await loadPending();
      return;
    }

    const { data: dispatch } = await dispatchRes.json();
    const validDispatchStatuses = ["TRIP_READY", "TRIP_REQUEST"];

    if (!validDispatchStatuses.includes(dispatch.status)) {
      console.warn("디스패치 상태 무효, 대기 콜로 이동");
      await loadPending();
      return;
    }

    const pathname = location.pathname;

    if (trip.status === "READY") {
      if (!pathname.endsWith("ready.html")) {
        location.href = `/driver/trips/ready.html?tripId=${trip.tripId}`;
      }
    } else if (trip.status === "STARTED") {
      if (!pathname.endsWith("active.html")) {
        location.href = `/driver/trips/active.html?tripId=${trip.tripId}`;
      }
    } else {
      await loadPending();
    }

  } catch (e) {
    console.error("운행 상태 확인 예외:", e);
    alert("운행 상태 확인 중 문제가 발생했습니다.");
  }
}


/* ================= 대기 콜 목록 ================= */
async function loadPending() {
  const list = document.getElementById("dispatch-list");
  list.innerHTML = "<div class='empty'>대기 콜 불러오는 중...</div>";

  try {
    const res = await fetch(`${DISPATCH_BASE}/pending`, {
      headers: {
        "Authorization": `Bearer ${TOKEN}`,
        "X-User-UUID": UUID,
        "X-User-Role": "DRIVER"
      }
    });

    const json = await res.json();

    if (!json.success || json.data.length === 0) {
      list.innerHTML = "<div class='empty'>현재 대기 콜이 없습니다.</div>";
      return;
    }

    list.innerHTML = json.data.map(d => `
      <div class="dispatch-card">
        <div class="dispatch-badge">
          <span class="dispatch-badge-dot"></span>
          NEW DISPATCH
        </div>
        <div class="dispatch-title">새로운 콜 요청</div>
        <div class="info"><b>출발:</b> ${d.pickupAddress}</div>
        <div class="info"><b>도착:</b> ${d.destinationAddress}</div>
        <div class="info-meta">ID: ${d.dispatchId}</div>
        <div class="actions">
          <button class="btn btn-accept" onclick="acceptCall('${d.dispatchId}')">수락하기</button>
          <button class="btn btn-reject" onclick="rejectCall('${d.dispatchId}')">거절하기</button>
        </div>
      </div>
    `).join("");

  } catch (e) {
    console.error("콜 목록 로딩 실패:", e);
    list.innerHTML = "<div class='empty'>콜 목록을 불러올 수 없습니다.</div>";
  }
}

/* ================= 콜 수락 ================= */
async function acceptCall(id) {
  try {
    const res = await fetch(`${DISPATCH_BASE}/${id}/accept`, {
      method: "PATCH",
      headers: {
        "Authorization": `Bearer ${TOKEN}`,
        "X-User-UUID": UUID,
        "X-User-Role": ROLE
      }
    });

    if (!res.ok) {
      const errText = await res.text();
      console.error("콜 수락 실패:", res.status, errText);
      alert("콜 수락에 실패했습니다. 이미 수락된 콜일 수 있어요.");
      return;
    }

    const json = await res.json();

    if (!json.success) {
      alert(json.message || "콜 수락에 실패했습니다.");
      return;
    }

    location.href = `/driver/trips/ready.html?tripId=${json.data.tripId}`;
  } catch (e) {
    console.error("콜 수락 중 오류:", e);
    alert("콜 수락 요청 중 문제가 발생했습니다.");
  }
}

/* ================= 콜 거절 ================= */
async function rejectCall(id) {
  try {
    const res = await fetch(`${DISPATCH_BASE}/${id}/reject`, {
      method: "PATCH",
      headers: {
        "Authorization": `Bearer ${TOKEN}`,
        "X-User-UUID": UUID,
        "X-User-Role": ROLE
      }
    });

    if (!res.ok) {
      console.error("콜 거절 실패:", res.status);
      alert("콜 거절에 실패했습니다.");
      return;
    }

    await loadPending();
  } catch (e) {
    console.error("콜 거절 중 오류:", e);
    alert("콜 거절 요청 중 문제가 발생했습니다.");
  }
}

/* ================= 페이지 시작 ================= */
document.addEventListener("DOMContentLoaded", async () => {
  if (!guardDriverPage()) return;
  await checkActiveTrip(); // 핵심 진입점
});
