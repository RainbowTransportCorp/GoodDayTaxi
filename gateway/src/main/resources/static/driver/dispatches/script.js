const BASE_URL = "/api/v1/dispatches/driver";

// 사용자 정보 가져오기
function loadDriverInfo() {
    const role = localStorage.getItem("role");
    const uuid = localStorage.getItem("userUuid");

    if (role !== "DRIVER") {
        alert("이 페이지에 접근할 수 있는 권한이 없습니다.");
        window.location.href = "/index.html";
        return;
    }

    const idBox = document.getElementById("driver-id");
    idBox.textContent = `ID: ${uuid.substring(0, 8)}...`;
}


// pending 목록 불러오기
async function loadPending() {
    const listEl = document.getElementById("dispatch-list");
    const token = localStorage.getItem("accessToken");
    const uuid = localStorage.getItem("userUuid");
    const role = localStorage.getItem("role");

    listEl.innerHTML = "<div class='empty'>대기 콜을 불러오는 중...</div>";

    const res = await fetch(`${BASE_URL}/pending`, {
        headers: {
            "Authorization": `Bearer ${token}`,
            "X-User-UUID": uuid,
            "X-User-Role": role
        }
    });

    const json = await res.json();

    if (!json.success) {
        listEl.innerHTML = `<div class='empty'>오류: ${json.message}</div>`;
        return;
    }

    const data = json.data;
    if (data.length === 0) {
        listEl.innerHTML = `<div class='empty'>현재 대기 콜이 없습니다.</div>`;
        return;
    }

    listEl.innerHTML = data.map(d => `
        <div class="dispatch-card">
            <div class="dispatch-badge">
                <span class="dispatch-badge-dot"></span>
                NEW DISPATCH
            </div>

            <div class="dispatch-title">새로운 콜 요청</div>

            <div class="info"><b>출발:</b> ${d.pickupAddress}</div>
            <div class="info"><b>도착:</b> ${d.dropoffAddress}</div>
            <div class="info"><b>예상 요금:</b> ${d.estimatedFare}원</div>
            <div class="info"><b>거리:</b> ${d.estimatedDistanceKm}km</div>

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

async function acceptCall(id) {
    const token = localStorage.getItem("accessToken");
    const uuid = localStorage.getItem("userUuid");
    const role = localStorage.getItem("role");

    const res = await fetch(`${BASE_URL}/${id}/accept`, {
        method: "PATCH",
        headers: {
            "Authorization": `Bearer ${token}`,
            "X-User-UUID": uuid,
            "X-User-Role": role
        }
    });

    const json = await res.json();
    alert(json.message || "콜 수락 완료!");
    loadPending();
}

async function rejectCall(id) {
    const token = localStorage.getItem("accessToken");
    const uuid = localStorage.getItem("userUuid");

    const res = await fetch(`${BASE_URL}/${id}/reject`, {
        method: "PATCH",
        headers: {
            "Authorization": `Bearer ${token}`,
            "X-User-UUID": uuid
        }
    });

    const json = await res.json();
    alert(json.message || "콜 거절 완료!");
    loadPending();
}

// 초기 실행
loadDriverInfo();
loadPending();
