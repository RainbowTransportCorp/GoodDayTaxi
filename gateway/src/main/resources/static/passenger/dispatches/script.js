/* ================= 상수 ================= */
const DISPATCH_URL = "/api/v1/dispatches";
const TRIP_ACTIVE_URL = "/api/v1/trips/passengers/active";

/* ================= 인증 정보 ================= */
const token = localStorage.getItem("accessToken");
const role = (localStorage.getItem("role") ?? "").trim().toUpperCase();
const userUuid = localStorage.getItem("userUuid");

/* ================= 상태 ================= */
let currentDispatchId = localStorage.getItem("dispatchId") ?? null;
let pollingTimer = null;

/* ================= 화면 전환 ================= */
function show(sectionId) {
    document.querySelectorAll("section").forEach(s => s.classList.add("hidden"));
    document.getElementById(sectionId)?.classList.remove("hidden");
}

/* ================= ACTIVE 조회 ================= */
async function fetchTripActive() {
    const res = await fetch(TRIP_ACTIVE_URL, {
        headers: {
            "Authorization": `Bearer ${token}`,
            "X-User-UUID": userUuid,
            "X-User-Role": "PASSENGER"
        }
    });

    if (res.status === 401 || res.status === 403) {
        alert("로그인이 만료되었습니다.");
        location.href = "/index.html";
        return null;
    }

    if (res.status === 204 || res.status === 404) {
        return null;
    }

    if (!res.ok) {
        console.error("ACTIVE API ERROR", res.status);
        return null;
    }

    const json = await res.json();
    return json?.data ?? null;
}

/* ================= 초기 진입 ================= */
async function initPassengerPage() {
    if (!token || !userUuid || role !== "PASSENGER") {
        alert("승객 전용 페이지입니다.");
        location.href = "/index.html";
        return;
    }

    try {
        const trip = await fetchTripActive();

        // 🔥 active 없음 → 무조건 대기 or 생성
        if (!trip) {
            if (currentDispatchId) {
                show("waiting-section");
                startTripPolling();
            } else {
                show("create-section");
            }
            return;
        }

        // 상태 저장
        localStorage.setItem("tripStatus", trip.status);

        if (trip.tripId) {
            localStorage.setItem("tripId", trip.tripId);
        }

        // 🔥 상태 분기
        switch (trip.status) {
            case "READY":
                show("waiting-section");
                startTripPolling();
                return;

            case "STARTED":
                if (!trip.tripId) {
                    console.log("STARTED but tripId not ready → waiting");
                    show("waiting-section");
                    startTripPolling();
                    return;
                }
                location.href = "/passenger/trips/active.html";
                return;

            case "ENDED":
                if (!trip.tripId) {
                    console.error("ENDED but tripId missing → blocked");
                    show("waiting-section");
                    return;
                }
                location.href = `/passenger/trips/ended.html?tripId=${trip.tripId}`;
                return;

            default:
                show("waiting-section");
                startTripPolling();
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
        body: JSON.stringify({ pickupAddress: pickup, destinationAddress: destination })
    });

    const json = await res.json();

    if (!json.success) {
        alert(json.message || "콜 요청 실패");
        return;
    }

    currentDispatchId = json.data.dispatchId;
    localStorage.setItem("dispatchId", currentDispatchId);

    document.getElementById("waiting-text").textContent = "기사님을 찾고 있습니다…";
    show("waiting-section");
    startTripPolling();
}

/* ================= 폴링 ================= */
function startTripPolling() {
    clearInterval(pollingTimer);

    pollingTimer = setInterval(async () => {
        const trip = await fetchTripActive();

        if (!trip) return;

        localStorage.setItem("tripStatus", trip.status);

        if (trip.tripId) {
            localStorage.setItem("tripId", trip.tripId);
        }

        if (trip.status === "STARTED" && trip.tripId) {
            clearInterval(pollingTimer);
            location.href = "/passenger/trips/active.html";
            return;
        }

        if (trip.status === "ENDED" && trip.tripId) {
            clearInterval(pollingTimer);
            location.href = `/passenger/trips/ended.html?tripId=${trip.tripId}`;
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

    resetState();
}

/* ================= 상태 초기화 ================= */
function resetState() {
    clearInterval(pollingTimer);
    pollingTimer = null;
    currentDispatchId = null;

    localStorage.removeItem("dispatchId");
    localStorage.removeItem("tripId");
    localStorage.removeItem("tripStatus");

    show("create-section");
}

/* ================= 실행 ================= */
initPassengerPage();
