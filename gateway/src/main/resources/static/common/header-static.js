function getToken() {
    return localStorage.getItem("accessToken");
}

function getRole() {
    return localStorage.getItem("role");
}

function getEmail() {
    return localStorage.getItem("email");
}

function getDisplayName() {
    const email = getEmail();
    if (!email) return "사용자";
    return email.split("@")[0];
}

function logout() {
    localStorage.clear();
    location.href = "/index.html";
}

document.addEventListener("DOMContentLoaded", async () => {
    const headerContainer = document.querySelector("header");
    if (!headerContainer) return;

    try {
        const res = await fetch("/common/header-static.html");
        headerContainer.innerHTML = await res.text();
    } catch (e) {
        console.error("❌ header-static.html 로드 실패", e);
        return;
    }

    const nameEl = document.getElementById("user-name");
    if (nameEl) nameEl.textContent = `${getDisplayName()}님`;

    const subEl = document.getElementById("brand-sub");
    if (subEl) {
        const role = getRole();
        subEl.textContent =
            role === "PASSENGER" ? "승객" :
            role === "DRIVER" ? "기사" :
            role === "ADMIN" ? "관리자" :
            role === "MASTER_ADMIN" ? "최고 관리자" : "서비스";
    }
});