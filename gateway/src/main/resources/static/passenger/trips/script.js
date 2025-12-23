// =====================
// API URLS
// =====================
const API = {
    TRIPS_BY_PASSENGER: () =>
        `/api/v1/trips/passengers`,
};

// =====================
// AUTH
// =====================
const token = localStorage.getItem("accessToken");
const role = localStorage.getItem("role");
const passengerId = localStorage.getItem("userUuid");

if (!token || !role || !passengerId) {
    alert("로그인이 필요합니다.");
    location.replace("/index.html");
}

if (role !== "PASSENGER") {
    alert("승객만 접근할 수 있습니다.");
    location.replace("/index.html");
}

// =====================
// HEADERS
// =====================
const defaultHeaders = {
    Authorization: `Bearer ${token}`,
    "X-User-Role": role,
    "X-User-UUID": passengerId,
};

// =====================
// STATE
// =====================
let page = 0;
const size = 5;
let totalPages = 0;

// =====================
// LOAD
// =====================
async function loadTrips() {
    const listEl = document.getElementById("trip-list");
    listEl.textContent = "불러오는 중...";

    try {
        const res = await fetch(
            `${API.TRIPS_BY_PASSENGER(passengerId)}?page=${page}&size=${size}`,
            {
                headers: defaultHeaders,
            }
        );

        if (!res.ok) {
            throw new Error(`HTTP ${res.status}`);
        }

        const json = await res.json();
        if (!json.success) {
            throw new Error("조회 실패");
        }

        const data = json.data;
        const trips = data.trips;
        totalPages = data.totalPages;

        if (trips.length === 0) {
            listEl.textContent = "운행 기록이 없습니다.";
            return;
        }

        listEl.innerHTML = trips.map(t => `
            <div class="trip-card">
                <div class="trip-status ${t.status}">
                    ${t.status}
                </div>

                <div class="trip-info"><b>출발</b> ${t.pickupAddress}</div>
                <div class="trip-info"><b>도착</b> ${t.dropoffAddress}</div>
                <div class="trip-info"><b>요금</b> ${t.fare}원</div>

                <div class="trip-meta">
                    운행 ID: ${t.tripId}<br>
                    종료 시각: ${t.endedAt ?? "-"}
                </div>
            </div>
        `).join("");

        document.getElementById("page-info").textContent =
            `${page + 1} / ${totalPages}`;

    } catch (e) {
        console.error(e);
        listEl.textContent = "운행 기록 조회에 실패했습니다.";
    }
}

// =====================
// PAGINATION
// =====================
function nextPage() {
    if (page + 1 >= totalPages) return;
    page++;
    loadTrips();
}

function prevPage() {
    if (page === 0) return;
    page--;
    loadTrips();
}

// =====================
// INIT
// =====================
loadTrips();
