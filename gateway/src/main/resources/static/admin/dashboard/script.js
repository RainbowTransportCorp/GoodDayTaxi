/* ===== Admin Dashboard ===== */

const ADMIN_USERS_BASE = "/api/v1/admin/users";
const ADMIN_DISPATCH_BASE = "/api/v1/admin/dispatches";

function guardAdmin() {
    const token = localStorage.getItem("accessToken");
    const role = localStorage.getItem("role");

    if (!token || (role !== "ADMIN" && role !== "MASTER_ADMIN")) {
        alert("관리자 전용 페이지입니다.");
        window.location.href = "/login/index.html";
        return false;
    }
    return true;
}

async function loadAdminSummary() {
    const token = localStorage.getItem("accessToken");
    const role = localStorage.getItem("role");

    // 1. 전체 사용자 수
    const userRes = await fetch(ADMIN_USERS_BASE, {
        headers: {
            "Authorization": `Bearer ${token}`,
            "X-User-Role": role
        }
    });

    const userJson = await userRes.json();
    if (userJson.success) {
        console.log(`전체 사용자 수: ${userJson.data.length}`);
    }

    // 2. 전체 배차 수
    const dispatchRes = await fetch(ADMIN_DISPATCH_BASE, {
        headers: {
            "Authorization": `Bearer ${token}`,
            "X-User-Role": role
        }
    });

    const dispatchJson = await dispatchRes.json();
    if (dispatchJson.success) {
        console.log(`전체 배차 수: ${dispatchJson.data.length}`);
    }
}

function initAdminDashboard() {
    if (!guardAdmin()) return;
    loadAdminSummary();
}

document.addEventListener("DOMContentLoaded", initAdminDashboard);
