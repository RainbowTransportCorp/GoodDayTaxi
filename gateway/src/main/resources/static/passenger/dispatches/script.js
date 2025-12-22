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
        "기사님을 찾고 있습니다…";

    show("waiting-section");
    startPolling();
}

/* ---------- 배차 상태 폴링 ---------- */
function startPolling() {
    pollingTimer = setInterval(async () => {
        try {
            const res = await fetch(`${BASE_URL}/${currentDispatchId}`, {
                headers: { "Authorization": `Bearer ${token}` }
            });

            if (!res.ok) return;

            const json = await res.json();
            if (!json.success) return;

            const status = json.data.status;

            // 1️⃣ 배차 대기
            if (["REQUESTED", "ASSIGNING", "ASSIGNED"].includes(status)) {
                show("waiting-section");
                return;
            }

            // 2️⃣ 기사 수락 → 운행 대기
            if (["ACCEPTED", "TRIP_REQUEST", "TRIP_READY"].includes(status)) {
                clearInterval(pollingTimer);
                window.location.href = "/passenger/trips/ready.html";
                return;
            }

            // 3️⃣ 종료 상태
            if (["TIMEOUT", "CANCELED"].includes(status)) {
                clearInterval(pollingTimer);
                alert("배차가 종료되었습니다.");
                backToHome();
            }

        } catch (e) {
            clearInterval(pollingTimer);
            window.location.href = "/common/error.html";
        }
    }, 3000);
}

/* ---------- 콜 취소 ---------- */
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

/* ---------- 홈 복귀 ---------- */
function backToHome() {
    clearInterval(pollingTimer);
    currentDispatchId = null;
    show("create-section");
}

/* ---------- 초기 ---------- */
show("create-section");
