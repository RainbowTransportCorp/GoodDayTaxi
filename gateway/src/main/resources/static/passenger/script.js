const BASE_URL = "/api/v1/dispatches";
let pollingTimer = null;

// 섹션 보이기/숨기기
function show(sectionId) {
    document.querySelectorAll("section").forEach(sec => sec.classList.add("hidden"));
    document.getElementById(sectionId).classList.remove("hidden");
}

// 콜 생성
async function createDispatch() {
    const role = localStorage.getItem("role");
    if (role !== "PASSENGER") {
        alert("승객만 콜을 요청할 수 있습니다.");
        return;
    }

    const pickup = document.getElementById("pickup").value;
    const destination = document.getElementById("destination").value;

    if (!pickup || !destination) {
        alert("출발지와 도착지를 모두 입력해주세요.");
        return;
    }

    const token = localStorage.getItem("accessToken");

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

    const dispatchId = json.data.dispatchId;
    document.getElementById("waiting-text").textContent =
        `배차를 요청했습니다. (ID: ${dispatchId})`;

    show("waiting-section");
    startPolling(dispatchId);
}

// 배차 상태 폴링
function startPolling(dispatchId) {
    if (pollingTimer) clearInterval(pollingTimer);

    pollingTimer = setInterval(async () => {
        const token = localStorage.getItem("accessToken");

        const res = await fetch(`${BASE_URL}/${dispatchId}`, {
            headers: { "Authorization": `Bearer ${token}` }
        });

        if (res.status === 401) {
            clearInterval(pollingTimer);
            alert("로그인이 만료되었습니다.");
            window.location.href = "/login/index.html";
            return;
        }

        const json = await res.json();

        if (!json.success) {
            clearInterval(pollingTimer);
            alert(json.message || "배차 상태 확인 실패");
            backToHome();
            return;
        }

        const status = json.data.status;

        if (status === "ASSIGNED" || status === "CONFIRMED") {
            clearInterval(pollingTimer);
            showDetail(json.data);
        }
    }, 3000);
}


// 상세 화면 표시
function showDetail(data) {
    const detailEl = document.getElementById("detail-container");

    detailEl.innerHTML = `
        <div><b>출발지:</b> ${data.pickupAddress}</div>
        <div><b>도착지:</b> ${data.destinationAddress}</div>
        <div><b>상태:</b> ${data.status}</div>
    `;

    show("detail-section");
}

// 홈으로
function backToHome() {
    if (pollingTimer) clearInterval(pollingTimer);
    show("create-section");
}
