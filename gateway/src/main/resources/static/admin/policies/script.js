const API_BASE = "/api/v1/policies";

(function guardMaster() {
  const role = localStorage.getItem("role");
  if (role !== "MASTER_ADMIN") {
    alert("MASTER 관리자 전용 페이지입니다.");
    location.href = "/admin/index.html";
  }
})();

function numOrNull(id) {
  const v = document.getElementById(id).value;
  return v === "" ? null : Number(v);
}

async function createPolicy() {
  const token = localStorage.getItem("accessToken");
  const role = localStorage.getItem("role");

  const body = {
    policyType: document.getElementById("policyType").value || null,
    baseDistance: numOrNull("baseDistance"),
    baseFare: numOrNull("baseFare"),
    distRateKm: numOrNull("distRateKm"),
    timeRate: numOrNull("timeRate")
  };

  const res = await fetch(API_BASE, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "Authorization": `Bearer ${token}`,
      "X-User-Role": role
    },
    body: JSON.stringify(body)
  });

  const json = await res.json();
  if (!json.success) {
    alert(json.message || "정책 생성 실패");
    return;
  }

  alert("요금 정책이 생성되었습니다.");
  loadPolicies();
}

async function loadPolicies() {
  const token = localStorage.getItem("accessToken");
  const role = localStorage.getItem("role");

  const res = await fetch(API_BASE, {
    headers: {
      "Authorization": `Bearer ${token}`,
      "X-User-Role": role
    }
  });

  const json = await res.json();
  const body = document.getElementById("policyBody");
  body.innerHTML = "";

  if (!json.success || json.data.length === 0) {
    body.innerHTML = `<tr><td colspan="6">등록된 정책이 없습니다.</td></tr>`;
    return;
  }

  json.data.forEach(p => {
    body.insertAdjacentHTML("beforeend", `
      <tr>
        <td>${p.policyId}</td>
        <td>${p.policyType}</td>
        <td>${p.baseDistance}</td>
        <td>${p.baseFare}</td>
        <td>${p.distRateKm}</td>
        <td>${p.timeRate}</td>
      </tr>
    `);
  });
}

document.addEventListener("DOMContentLoaded", loadPolicies);
