/* ================= 상수 ================= */
const DISPATCH_URL = "/api/v1/dispatches";
const TRIP_ACTIVE_URL = "/api/v1/trips/passengers/active";

const token = localStorage.getItem("accessToken");
const role = localStorage.getItem("role");
const userUuid = localStorage.getItem("userUuid");

let currentDispatchId = null;
let pollingTimer = null;

/* ================= 화면 전환 ================= */
function show(sectionId) {
    document.querySelectorAll("section").forEach(s => s.classList.add("hidden"));
    document.getElementById(sectionId)?.classList.remove("hidden");
}

/* ================= 공통 fetch ================= */
async function fetchTripActive() {
    const res = await fetch(TRIP_ACTIVE_URL, {
        headers: {
            "Authorization": `Bearer ${token}`,
            "X-User-UUID": userUuid,
            "X-User-Role": "PASSENGER"
        }
    });

    if (res.status === 401 || res.status === 403) {
        alert("로그인이 만료되었습니다. 다시 로그인해주세요.");
        location.href = "/index.html";
        return null;
    }

    if (res.status === 204 || res.status === 404) return null;

    if (!res.ok) {
        console.error("ACTIVE TRIP ERROR", res.status, await res.text());
        alert("운행 상태를 확인할 수 없습니다.");
        location.href = "/passenger/dashboard/index.html";
        return null;
    }

    const json = await res.json();
    return json?.data ?? null;
}

/* ================= 초기 진입 ================= */
async function initPassengerPage() {
    if (role !== "PASSENGER") {
        alert("승객 전용 페이지입니다.");
        location.href = "/index.html";
        return;
    }

    try {
        const trip = await fetchTripActive();

        if (!trip) {
            show("create-section");
            return;
        }

        switch (trip.status) {
            case "READY":
                location.href = "/passenger/trips/ready.html";
                return;
            case "STARTED":
                location.href = "/passenger/trips/active.html";
                return;
            case "ENDED":
                location.href = "/passenger/trips/ended.html";
                return;
            default:
                show("create-section");
        }
    } catch (e) {
        console.error("initPassengerPage error", e);
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

    const res = await fetch(DISPATCH_URL, {
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
    document.getElementById("waiting-text").textContent = "기사님을 찾고 있습니다…";

    show("waiting-section");
    startTripPolling();
}

/* ================= 운행 폴링 ================= */
function startTripPolling() {
    clearInterval(pollingTimer);

    pollingTimer = setInterval(async () => {
        try {
            const trip = await fetchTripActive();
            if (!trip) return;

            if (trip.status === "READY") {
                clearInterval(pollingTimer);
                location.href = "/passenger/trips/ready.html";
            }
        } catch (e) {
            console.error("polling error", e);
        }
    }, 3000);
}

/* ================= 콜 취소 ================= */
async function cancelCurrentDispatch() {
    if (!currentDispatchId) return;
    if (!confirm("콜을 취소하시겠습니까?")) return;

    await fetch(`${DISPATCH_URL}/${currentDispatchId}/cancel`, {
        method: "PATCH",
        headers: {
            "Authorization": `Bearer ${token}`
        }
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
