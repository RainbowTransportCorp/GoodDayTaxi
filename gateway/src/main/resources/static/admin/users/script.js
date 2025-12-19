const role = localStorage.getItem("role");
const token = localStorage.getItem("accessToken");

if (role !== "ADMIN" && role !== "MASTER_ADMIN") {
    alert("관리자만 접근할 수 있습니다.");
    window.location.href = "/index.html";
}

async function loadUsers() {
    try {
        const res = await fetch("/api/v1/admin/users", {
            headers: {
                "Authorization": `Bearer ${token}`,
                "X-User-Role": role
            }
        });

        if (res.status === 401) {
            alert("로그인이 만료되었습니다.");
            window.location.href = "/index.html";
            return;
        }

        const json = await res.json();
        const tbody = document.querySelector("#userTable tbody");
        tbody.innerHTML = "";

        json.data.forEach(u => {
            tbody.insertAdjacentHTML("beforeend", `
                <tr onclick="goDetail('${u.userUuid}')">
                    <td>${u.userUuid}</td>
                    <td>${u.name}</td>
                    <td>${u.role}</td>
                    <td>${u.status}</td>
                </tr>
            `);
        });
    } catch (e) {
        console.error(e);
        alert("사용자 목록을 불러오는 중 오류가 발생했습니다.");
    }
}

function goDetail(id) {
    window.location.href = `/admin/users/detail.html?id=${id}`;
}

loadUsers();
