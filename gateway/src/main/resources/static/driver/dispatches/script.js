/* ================= 공통 ================= */
const TOKEN = localStorage.getItem("accessToken");
const UUID = localStorage.getItem("userUuid");
const ROLE = localStorage.getItem("role");

const DISPATCH_BASE = "/api/v1/dispatches/driver";

/* ================= 권한 체크 ================= */
if (ROLE !== "DRIVER") {
  alert("기사 전용 페이지입니다.");
  location.href = "/index.html";
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

    if (!res.ok) {
      throw new Error();
    }

    const json = await res.json();
    const trip = json.data;

    // 🚕 배차 완료(대기)
    if (trip.status === "READY") {
      location.href = "/driver/trips/ready.html";
      return;
    }

    // 🚕 운행 중
    if (trip.status === "STARTED") {
      location.href = "/driver/trips/active.html";
    }

  } catch {
    // 👉 운행 없음 → 대기 콜 표시
    await loadPending();
  }
}

/* ================= 대기 콜 목록 ================= */
async function loadPending() {
  const list = document.getElementById("dispatch-list");
  list.innerHTML = "<div class='empty'>대기 콜 불러오는 중...</div>";

  const res = await fetch(`${DISPATCH_BASE}/pending`, {
    headers: {
      "Authorization": `Bearer ${TOKEN}`,
      "X-User-UUID": UUID,
      "X-User-Role": ROLE
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
            <div class="info"><b>도착:</b> ${d.destinationAddress}</div>=
            <div class="info-meta">ID: ${d.dispatchId}</div>

            <div class="actions">
                <button class="btn btn-accept" onclick="acceptCall('${d.dispatchId}')">
                    수락하기
                </button>
                <button class="btn btn-reject" onclick="rejectCall('${d.dispatchId}')">
                    거절하기
                </button>
            </div>
        </div>
    `).join("");
}

/* ================= 콜 수락 ================= */
async function acceptCall(id) {
  const res = await fetch(`${DISPATCH_BASE}/${id}/accept`, {
    method: "PATCH",
    headers: {
      "Authorization": `Bearer ${TOKEN}`,
      "X-User-UUID": UUID,
      "X-User-Role": ROLE
    }
  });

  const json = await res.json();
  if (!json.success) {
    alert(json.message);
    return;
  }
  location.href = "/driver/trips/ready.html";
}

/* ================= 콜 거절 ================= */
async function rejectCall(id) {
  await fetch(`${DISPATCH_BASE}/${id}/reject`, {
    method: "PATCH",
    headers: {
      "Authorization": `Bearer ${TOKEN}`,
      "X-User-UUID": UUID
    }
  });

  await loadPending();
}
