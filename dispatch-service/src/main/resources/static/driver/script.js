const BASE_URL = "http://localhost:8202/api/v1/dispatches/driver";

async function loadPending() {
    const listEl = document.getElementById("dispatch-list");
    listEl.innerHTML = "<div class='empty'>대기 콜을 불러오는 중입니다...</div>";

    const res = await fetch(`${BASE_URL}/pending`);
    const json = await res.json();

    if (!json.success) {
        listEl.innerHTML = `<div class='empty'>오류 발생: ${json.message || "알 수 없는 오류"}</div>`;
        return;
    }

    const data = json.data;

    if (!data || data.length === 0) {
        listEl.innerHTML = `<div class='empty'>현재 대기 중인 콜이 없습니다.</div>`;
        return;
    }

    listEl.innerHTML = data.map(d => `
        <div class="dispatch-card">
            <div class="dispatch-badge">
                <span class="dispatch-badge-dot"></span>
                NEW DISPATCH
            </div>

            <div class="dispatch-title">🔔 새로운 콜 요청</div>

            <div class="info"><b>출발:</b> ${d.pickupAddress}</div>
            <div class="info"><b>도착:</b> ${d.dropoffAddress}</div>
            <div class="info"><b>요금:</b> ${d.estimatedFare}원</div>
            <div class="info"><b>거리:</b> ${d.estimatedDistanceKm}km</div>

            <div class="info-meta">디스패치 ID: ${d.dispatchId}</div>

            <div class="actions">
                <button class="btn btn-accept" onclick="acceptCall('${d.dispatchId}')">수락하기</button>
                <button class="btn btn-reject" onclick="rejectCall('${d.dispatchId}')">거절하기</button>
            </div>
        </div>
    `).join("");
}

async function acceptCall(id) {
    const res = await fetch(`${BASE_URL}/${id}/accept`, {
        method: "PATCH"
    });
    const json = await res.json();
    alert(json.message || "콜을 수락했습니다.");
    loadPending();
}

async function rejectCall(id) {
    const res = await fetch(`${BASE_URL}/${id}/reject`, {
        method: "PATCH"
    });
    const json = await res.json();
    alert(json.message || "콜을 거절했습니다.");
    loadPending();
}

loadPending();
