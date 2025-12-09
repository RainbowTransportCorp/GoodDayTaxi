const API = "/api/v1/drivers/";

function getDriverId() {
    return localStorage.getItem("userUuid");
}

async function loadProfile() {
    const uuid = getDriverId();
    const role = localStorage.getItem("role");
    const token = localStorage.getItem("accessToken");

    if (role !== "DRIVER") {
        alert("이 페이지에 접근할 수 있는 권한이 없습니다.");
        window.location.href = "/login/index.html";
        return;
    }

    const res = await fetch(`${API}${uuid}`, {
        headers: {
            "Authorization": `Bearer ${token}`,
            "X-User-UUID": uuid,
            "X-User-Role": role
        }
    });

    const json = await res.json();
    const d = json.data;

    document.getElementById("name").value = d.name;
    document.getElementById("vehicleNumber").value = d.vehicleNumber;
    document.getElementById("vehicleColor").value = d.vehicleColor;
    document.getElementById("vehicleType").value = d.vehicleType;

    document.getElementById("status-text").textContent =
        d.onlineStatus === "ONLINE" ? "🟢 온라인" : "⚪ 오프라인";
}

async function updateProfile() {
    const uuid = getDriverId();
    const token = localStorage.getItem("accessToken");
    const role = localStorage.getItem("role");

    const body = {
        name: document.getElementById("name").value,
        vehicleNumber: document.getElementById("vehicleNumber").value,
        vehicleColor: document.getElementById("vehicleColor").value,
        vehicleType: document.getElementById("vehicleType").value
    };

    const res = await fetch(`${API}${uuid}`, {
        method: "PATCH",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`,
            "X-User-UUID": uuid
        },
        body: JSON.stringify(body)
    });

    const json = await res.json();
    alert(json.message || "프로필이 수정되었습니다.");
}

async function changeStatus(status) {
    const uuid = getDriverId();
    const token = localStorage.getItem("accessToken");

    const res = await fetch(`${API}${uuid}/status`, {
        method: "PATCH",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`,
            "X-User-UUID": uuid
        },
        body: JSON.stringify({ onlineStatus: status })
    });

    const json = await res.json();

    alert(json.message || "상태가 변경되었습니다.");
    document.getElementById("status-text").textContent =
        status === "ONLINE" ? "🟢 온라인" : "⚪ 오프라인";
}

loadProfile();
