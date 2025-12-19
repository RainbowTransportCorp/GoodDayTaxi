const BASE_URL = "/api/v1/dispatches";
const token = localStorage.getItem("accessToken");
const role = localStorage.getItem("role");

let currentDispatchId = null;
let pollingTimer = null;

/* ---------- 화면 전환 ---------- */
function show(sectionId) {
    document.querySelectorAll("section")
        .forEach(s => s.classList.add("hidden"));
    document.getElementById(sectionId).classList.remove("hidden");
}

/* ---------- 콜 생성 ---------- */
async function createDispatch() {
    if (role !== "PASSENGER") {
        alert("승객만 요청 가능합니다.");
        return;
    }

    const pickup = document.getElementById("pickup").value;
    const destination = document.getElementById("destination").value;

    if (!pickup || !destination) {
        alert("출발지와 도착지를 입력해주세요.");
        return;
    }

    const res = await fetch(BASE_URL, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify({
            pickupAddress: pickup,
            destinationAddress: destination
        })
    });

    const json = await res.json();
    if (!json.success) {
        alert(json.message || "콜 요청 실패");
        return;
    }

    currentDispatchId = json.data.dispatchId;
    document.getElementById("waiting-text").textContent =
        `배차 요청 완료 (ID: ${currentDispatchId})`;

    show("waiting-section");
    startPolling();
}

/* ---------- 상태 폴링 ---------- */
function startPolling() {
    pollingTimer = setInterval(async () => {
        const res = await fetch(`${BASE_URL}/${currentDispatchId}`, {
            headers: { "Authorization": `Bearer ${token}` }
        });

        const json = await res.json();
        if (!json.success) return;

        const status = json.data.status;
        if (status === "ASSIGNED" || status === "CONFIRMED") {
            clearInterval(pollingTimer);
            showDetail(json.data);
        }
    }, 3000);
}

/* ---------- 상세 ---------- */
function showDetail(data) {
    document.getElementById("detail-container").innerHTML = `
        <div><b>출발지</b> ${data.pickupAddress}</div>
        <div><b>도착지</b> ${data.destinationAddress}</div>
        <div><b>상태</b> ${data.status}</div>
    `;
    show("detail-section");
}

/* ---------- 취소 ---------- */
async function cancelCurrentDispatch() {
    if (!currentDispatchId) return;
    if (!confirm("콜을 취소하시겠습니까?")) return;

    await fetch(`${BASE_URL}/${currentDispatchId}/cancel`, {
        method: "PATCH",
        headers: { "Authorization": `Bearer ${token}` }
    });

    alert("콜이 취소되었습니다.");
    backToHome();
}

/* ---------- 홈 ---------- */
function backToHome() {
    clearInterval(pollingTimer);
    currentDispatchId = null;
    show("create-section");
}
