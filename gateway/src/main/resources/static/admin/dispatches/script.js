const API_BASE = "/api/v1/admin/dispatches";

(function authCheck() {
    const role = localStorage.getItem("role");
    if (role !== "ADMIN" && role !== "MASTER_ADMIN") {
        alert("관리자만 접근할 수 있습니다.");
        location.href = "/index.html";
    }
})();

async function loadDispatches() {
    const status = document.getElementById("statusFilter").value;
    const role = localStorage.getItem("role");
    const token = localStorage.getItem("accessToken");

    let url = API_BASE;
    if (status) {
        url += `?status=${status}`;
    }

    const res = await fetch(url, {
        headers: {
            "Authorization": `Bearer ${token}`,
            "X-User-Role": role
        }
    });

    const json = await res.json();
    const body = document.getElementById("dispatchBody");
    body.innerHTML = "";

    if (!json.success || json.data.length === 0) {
        body.innerHTML = `
            <tr>
                <td colspan="6">조회된 배차가 없습니다.</td>
            </tr>`;
        return;
    }

    json.data.forEach(d => {
        body.insertAdjacentHTML("beforeend", `
            <tr onclick="goDetail('${d.dispatchId}')">
                <td>${d.dispatchId}</td>
                <td>${d.passengerId ?? "-"}</td>
                <td>${d.driverId ?? "-"}</td>
                <td class="status ${d.status}">${d.status}</td>
                <td>-</td>
                <td>${formatDate(d.createdAt)}</td>
            </tr>
        `);
    });
}

function goDetail(id) {
    location.href = `/admin/dispatches/detail.html?id=${id}`;
}

function formatDate(dateStr) {
    if (!dateStr) return "-";
    return dateStr.replace("T", " ").substring(0, 19);
}

loadDispatches();
