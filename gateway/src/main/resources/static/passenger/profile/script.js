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

// 페이지 로드 시 프로필 조회
window.addEventListener("DOMContentLoaded", loadProfile);

async function loadProfile() {
    const uuid = getUserUuid();
    const role = getRole();

    if (!uuid || role !== "PASSENGER") {
        alert("접근 권한이 없습니다.");
        window.location.href = "/login/index.html";
        return;
    }

    try {
        const res = await fetch(PROFILE_API, {
            headers: {
                "Authorization": `Bearer ${getToken()}`,
                "X-User-UUID": uuid,
                "X-User-Role": role
            }
        });

        const json = await res.json();

        if (!json.success) {
            alert(json.message || "프로필을 불러오지 못했습니다.");
            return;
        }

        const d = json.data;
        document.getElementById("name").value = d.name || "";
        document.getElementById("phoneNumber").value = d.phoneNumber || "";
        document.getElementById("email").value = d.email || "";
    } catch (e) {
        console.error(e);
        alert("서버 통신 중 오류가 발생했습니다.");
    }
}

async function updateProfile() {
    const uuid = getUserUuid();
    const role = getRole();

    const body = {
        name: document.getElementById("name").value,
        phoneNumber: document.getElementById("phoneNumber").value
    };

    try {
        const res = await fetch(PROFILE_API, {
            method: "PATCH",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${getToken()}`,
                "X-User-UUID": uuid,
                "X-User-Role": role
            },
            body: JSON.stringify(body)
        });

        const json = await res.json();

        if (!json.success) {
            alert(json.message || "수정에 실패했습니다.");
            return;
        }

        alert(json.message || "프로필이 수정되었습니다.");
    } catch (e) {
        console.error(e);
        alert("서버 통신 중 오류가 발생했습니다.");
    }
}
