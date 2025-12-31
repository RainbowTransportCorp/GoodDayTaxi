const DRIVER_API = "/api/v1/drivers";

function getToken() {
    return localStorage.getItem("accessToken");
}

function getUserUuid() {
    return localStorage.getItem("userUuid");
}

function getRole() {
    return localStorage.getItem("role");
}

window.addEventListener("DOMContentLoaded", loadProfile);

// ================================
// 기사 프로필 조회 (GET /drivers/{id})
// ================================
async function loadProfile() {
    const uuid = getUserUuid();
    const role = getRole();
    const token = getToken();

    if (role !== "DRIVER") {
        alert("이 페이지에 접근할 수 있는 권한이 없습니다.");
        window.location.href = "/index.html";
        return;
    }

    const res = await fetch(`${DRIVER_API}/${uuid}`, {
        headers: {
            "Authorization": `Bearer ${token}`
        }
    });

    const json = await res.json();

    if (!json.success) {
        alert(json.message || "프로필 불러오기 실패");
        return;
    }

    const d = json.data;

    document.getElementById("name").value = d.name ?? "";
    document.getElementById("vehicleNumber").value = d.vehicleNumber ?? "";
    document.getElementById("vehicleType").value = d.vehicleType ?? "";
    document.getElementById("vehicleColor").value = d.vehicleColor ?? "";

    document.getElementById("status-text").textContent =
        d.onlineStatus === "ONLINE" ? "🟢 온라인" : "⚪ 오프라인";
}

// ================================
// 기사 프로필 수정 (PATCH /drivers/me)
// ================================
async function updateProfile() {
    const uuid = getUserUuid();
    const role = getRole();
    const token = getToken();

    if (role !== "DRIVER") {
        alert("권한이 없습니다.");
        return;
    }

    const body = {
        vehicle_number: document.getElementById("vehicleNumber").value,
        vehicle_type: document.getElementById("vehicleType").value,
        vehicle_color: document.getElementById("vehicleColor").value
    };

    const res = await fetch(`${DRIVER_API}/me`, {
        method: "PATCH",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`,
            "X-User-UUID": uuid
        },
        body: JSON.stringify(body)
    });

    const json = await res.json();

    if (!json.success) {
        alert(json.message || "프로필 수정 실패");
        return;
    }

    alert("프로필이 수정되었습니다.");
    loadProfile();
}

// =====================================
// 기사 상태 변경 (PATCH /drivers/me/status)
// =====================================
async function changeStatus(status) {
    const uuid = getUserUuid();
    const role = getRole();
    const token = getToken();

    if (role !== "DRIVER") {
        alert("권한이 없습니다.");
        return;
    }

    const res = await fetch(`${DRIVER_API}/me/status`, {
        method: "PATCH",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`,
            "X-User-UUID": uuid
        },
        body: JSON.stringify({
            online_status: status   // ✅ 백엔드 DTO와 정확히 일치
        })
    });

    const json = await res.json();

    if (!json.success) {
        alert(json.message || "상태 변경 실패");
        return;
    }

    document.getElementById("status-text").textContent =
        status === "ONLINE" ? "🟢 온라인" : "⚪ 오프라인";

    alert("상태가 변경되었습니다.");
}
