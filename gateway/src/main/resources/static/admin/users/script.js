const role = localStorage.getItem("role");

async function loadUsers() {
    const res = await fetch("/api/v1/admin/users", {
        headers: { "X-User-Role": role }
    });
    const json = await res.json();

    const tbody = document.querySelector("#userTable tbody");
    tbody.innerHTML = "";

    json.data.forEach(u => {
        tbody.innerHTML += `
            <tr onclick="goDetail('${u.userUuid}')">
                <td>${u.userUuid}</td>
                <td>${u.name}</td>
                <td>${u.role}</td>
                <td>${u.status}</td>
            </tr>
        `;
    });
}

function goDetail(id) {
    window.location.href = `/admin/users/detail.html?id=${id}`;
}

loadUsers();
