/* ===== Common Header Logic ===== */

function getToken() {
    return localStorage.getItem("accessToken");
}

function getRole() {
    return localStorage.getItem("role");
}

function getEmail() {
    return localStorage.getItem("email");
}

function logout() {
    localStorage.clear();
    window.location.href = "/index.html";
}

function getDisplayName() {
    const email = getEmail();
    if (!email) return "사용자";
    return email.split("@")[0];
}

function renderHeader() {
    const nameEl = document.getElementById("user-name");
    const subEl = document.getElementById("brand-sub");

    if (nameEl) {
        nameEl.textContent = `${getDisplayName()}님`;
    }

    if (!subEl) return;

    switch (getRole()) {
        case "PASSENGER":
            subEl.textContent = "승객";
            break;
        case "DRIVER":
            subEl.textContent = "기사";
            break;
        case "ADMIN":
            subEl.textContent = "관리자";
            break;
        case "MASTER_ADMIN":
            subEl.textContent = "최고 관리자";
            break;
        default:
            subEl.textContent = "서비스";
    }
}

document.addEventListener("DOMContentLoaded", renderHeader);
