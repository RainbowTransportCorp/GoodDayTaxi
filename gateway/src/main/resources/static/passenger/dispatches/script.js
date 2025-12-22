const BASE_URL = "/api/v1/dispatches";
const TRIP_ACTIVE_URL = "/api/v1/trips/passengers/active";

const token = localStorage.getItem("accessToken");
const role = localStorage.getItem("role");
const userUuid = localStorage.getItem("userUuid");

let currentDispatchId = null;
let pollingTimer = null;

/* ================= 화면 전환 ================= */
function show(sectionId) {
    document.querySelectorAll("section")
    .forEach(s => s.classList.add("hidden"));
    document.getElementById(sectionId).classList.remove("hidden");
}

/* ================= 초기 진입 시 상태 판단 ================= */
async function initPassengerPage() {
    if (role !== "PASSENGER") {
        alert("승객 전용 페이지입니다.");
        location.href = "/index.html";
        return;
    }

    // 1️⃣ 이미 운행이 있는지 먼저 확인 (새로고침 대응)
    try {
        const res = await fetch(TRIP_ACTIVE_URL, {
            headers: {
                "Authorization": `Bearer ${token}`,
                "X-User-UUID": userUuid,
                "X-User-Role": "PASSENGER"
            }
        });

        if (res.ok) {
            const json = await res.json();
            const trip = json.data;

            // 운행 대기
            if (trip.status === "READY") {
                location.href = "/passenger/trips/ready.html";
                return;
            }

            // 운행 중
            if (trip.status === "STARTED") {
                location.href = "/passenger/trips/active.html";
                return;
            }

            // 운행 종료
            if (trip.status === "ENDED") {
                location.href =
                    `/passenger/trips/completed.html?tripId=${trip.id}`;
                return;
            }
        }
    } catch (e) {
        // active trip 없음 → 그냥 콜 생성 화면
    }

    show("create-section");
}

/* ================= 콜 생성 ================= */
async function createDispatch() {
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
    startDispatchPolling();
}

/* ================= 배차 상태 폴링 ================= */
function startDispatchPolling() {
    pollingTimer = setInterval(async () => {
        try {
            const res = await fetch(`${BASE_URL}/${currentDispatchId}`, {
                headers: { "Authorization": `Bearer ${token}` }
            });

            if (!res.ok) return;

            const json = await res.json();
            if (!json.success) return;

            const status = json.data.status;

            // 🚕 배차 대기
            if (["REQUESTED", "ASSIGNING", "ASSIGNED"].includes(status)) {
                show("waiting-section");
                return;
            }

            // 🚕 기사 수락 → Trip 생성 대기 / 완료
            if (["ACCEPTED", "TRIP_REQUEST", "TRIP_READY"].includes(status)) {
                clearInterval(pollingTimer);
                location.href = "/passenger/trips/ready.html";
                return;
            }

            // ❌ 종료
            if (["TIMEOUT", "CANCELED"].includes(status)) {
                clearInterval(pollingTimer);
                alert("배차가 종료되었습니다.");
                backToHome();
            }

        } catch (e) {
            clearInterval(pollingTimer);
            location.href = "/common/error.html";
        }
    }, 3000);
}

/* ================= 콜 취소 ================= */
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

/* ================= 홈 ================= */
function backToHome() {
    clearInterval(pollingTimer);
    pollingTimer = null;
    currentDispatchId = null;
    show("create-section");
}

/* ================= 실행 ================= */
initPassengerPage();
