const PROFILE_API = "/api/v1/users/me";

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
// 내 프로필 조회
// ================================
async function loadProfile() {
    const uuid = getUserUuid();
    const role = getRole();

    if (!uuid || role !== "PASSENGER") {
        alert("접근 권한이 없습니다.");
        window.location.href = "/index.html";
        return;
    }

    const res = await fetch(PROFILE_API, {
        headers: {
            "Authorization": `Bearer ${getToken()}`,
            "X-User-UUID": uuid
        }
    });

    const json = await res.json();
    if (!json.success) {
        alert(json.message || "프로필 조회 실패");
        return;
    }

    const d = json.data;
    document.getElementById("name").value = d.name ?? "";
    document.getElementById("phoneNumber").value = d.phoneNumber ?? "";
    document.getElementById("email").value = d.email ?? "";
}

// ================================
// 내 프로필 수정
// ================================
async function updateProfile() {
    const uuid = getUserUuid();

    const body = {
        name: document.getElementById("name").value,
        phoneNumber: document.getElementById("phoneNumber").value // ✅ camelCase
    };

    const res = await fetch(PROFILE_API, {
        method: "PATCH",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${getToken()}`,
            "X-User-UUID": uuid
        },
        body: JSON.stringify(body)
    });

    const json = await res.json();
    if (!json.success) {
        alert(json.message || "수정 실패");
        return;
    }

    alert("프로필이 수정되었습니다.");
}
