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

async function loadProfile() {
    const uuid = getUserUuid();
    const role = getRole();
    const token = getToken();

    if (role !== "DRIVER") {
        alert("이 페이지에 접근할 수 있는 권한이 없습니다.");
        window.location.href = "/login/index.html";
        return;
    }

    const res = await fetch(`${DRIVER_API}/${uuid}`, {
        headers: {
            "Authorization": `Bearer ${token}`,
            "X-User-UUID": uuid,
            "X-User-Role": role
        }
    });

    const json = await res.json();
    if (!json.success) {
        alert(json.message || "프로필 불러오기 실패");
        return;
    }

    const d = json.data;

    document.getElementById("name").value = d.name;
    document.getElementById("vehicleNumber").value = d.vehicleNumber;
    document.getElementById("vehicleColor").value = d.vehicleColor;
    document.getElementById("vehicleType").value = d.vehicleType;

    document.getElementById("status-text").textContent =
        d.onlineStatus === "ONLINE" ? "🟢 온라인" : "⚪ 오프라인";
}

// ================================
// 프로필 수정 (PATCH /drivers/me)
// ================================
async function updateProfile() {
    const uuid = getUserUuid();
    const role = getRole();
    const token = getToken();

    const body = {
        vehicleNumber: document.getElementById("vehicleNumber").value,
        vehicleColor: document.getElementById("vehicleColor").value,
        vehicleType: document.getElementById("vehicleType").value
    };

    const res = await fetch(`${DRIVER_API}/me`, {
        method: "PATCH",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`,
            "X-User-UUID": uuid,
            "X-User-Role": role
        },
        body: JSON.stringify(body)
    });

    const json = await res.json();
    alert(json.message || "프로필이 수정되었습니다.");
}

// =====================================
// 온라인 / 오프라인 변경 (PATCH /me/status)
// =====================================
async function changeStatus(status) {
    const uuid = getUserUuid();
    const role = getRole();
    const token = getToken();

    const res = await fetch(`${DRIVER_API}/me/status`, {
        method: "PATCH",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`,
            "X-User-UUID": uuid,
            "X-User-Role": role
        },
        body: JSON.stringify({ onlineStatus: status })
    });

    const json = await res.json();

    alert(json.message || "상태가 변경되었습니다.");
    document.getElementById("status-text").textContent =
        status === "ONLINE" ? "🟢 온라인" : "⚪ 오프라인";
}
