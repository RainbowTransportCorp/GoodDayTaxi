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

        // ✅ 운행 없으면 그냥 콜 화면 유지
        if (res.status === 404 || res.status === 204) {
            show("create-section");
            return;
        }

        if (!res.ok) {
            const errorText = await res.text();
            console.error("운행 상태 조회 실패:", res.status, errorText);

            // 토큰 만료면 로그인 페이지로
            if (res.status === 401 || res.status === 403) {
                alert("로그인이 만료되었습니다. 다시 로그인해주세요.");
                location.href = "/index.html";
                return;
            }

            // 서버 오류는 사용자에게 안내 후 홈으로
            alert("운행 상태를 확인할 수 없습니다. 잠시 후 다시 시도해주세요.");
            location.href = "/passenger/dashboard/index.html";
            return;
        }

        const { data: trip } = await res.json();

        if (trip.status === "READY") {
            location.href = "/passenger/trips/ready.html";
        } else if (trip.status === "STARTED") {
            location.href = "/passenger/trips/active.html";
        } else if (trip.status === "ENDED") {
            location.href =
                `/passenger/trips/completed.html?tripId=${trip.tripId}`;
        }

    } catch {
        show("create-section");
    }
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
    startTripPolling();
}

function startTripPolling() {
    pollingTimer = setInterval(async () => {
        const res = await fetch(TRIP_ACTIVE_URL, {
            headers: {
                "Authorization": `Bearer ${token}`,
                "X-User-UUID": userUuid,
                "X-User-Role": "PASSENGER"
            }
        });

        if (res.ok) {
            const { data: trip } = await res.json();

            if (trip.status === "READY") {
                clearInterval(pollingTimer);
                location.href = "/passenger/trips/ready.html";
            }
        }
    }, 3000); // 3초마다 체크
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
