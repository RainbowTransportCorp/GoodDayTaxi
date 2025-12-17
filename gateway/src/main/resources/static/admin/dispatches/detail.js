const API_BASE = "/api/v1/admin/dispatches";

const params = new URLSearchParams(location.search);
const dispatchId = params.get("id");

if (!dispatchId) {
    alert("잘못된 접근입니다.");
    history.back();
}

const role = localStorage.getItem("role");
const token = localStorage.getItem("accessToken");

if (role !== "ADMIN" && role !== "MASTER_ADMIN") {
    alert("관리자만 접근할 수 있습니다.");
    location.href = "/index.html";
}

async function loadDetail() {
    const res = await fetch(`${API_BASE}/${dispatchId}`, {
        headers: {
            "Authorization": `Bearer ${token}`,
            "X-User-Role": role
        }
    });

    const json = await res.json();
    if (!json.success) {
        alert(json.message || "조회 실패");
        return;
    }

    const d = json.data;

    document.getElementById("dispatchId").textContent = d.dispatchId;
    document.getElementById("status").textContent = d.status;
    document.getElementById("passenger").textContent = d.passengerId ?? "-";
    document.getElementById("driver").textContent = d.driverId ?? "-";
    document.getElementById("pickup").textContent = d.pickupAddress;
    document.getElementById("dropoff").textContent = d.dropoffAddress;
    document.getElementById("fare").textContent = d.fare ? `${d.fare}원` : "-";
    document.getElementById("createdAt").textContent = d.createdAt;

    // MASTER_ADMIN만 버튼 표시
    if (role !== "MASTER_ADMIN" || d.status === "TIMEOUT") {
        document.getElementById("adminActions").style.display = "none";
    }
}

async function forceTimeout() {
    if (!confirm("강제로 TIMEOUT 처리하시겠습니까?")) return;

    const res = await fetch(`${API_BASE}/${dispatchId}/force-timeout`, {
        method: "PATCH",
        headers: {
            "Authorization": `Bearer ${token}`,
            "X-User-Role": role
        }
    });

    const json = await res.json();
    if (!json.success) {
        alert(json.message || "처리 실패");
        return;
    }

    alert("강제 TIMEOUT 처리 완료");
    loadDetail();
}

loadDetail();
