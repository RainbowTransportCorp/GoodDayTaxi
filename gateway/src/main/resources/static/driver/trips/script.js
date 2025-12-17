const token = localStorage.getItem("accessToken");
const role = localStorage.getItem("role");
const driverId = localStorage.getItem("userUuid");

if (role !== "DRIVER") {
    alert("기사만 접근할 수 있습니다.");
    location.href = "/index.html";
}

let page = 0;
const size = 5;

async function loadTrips() {
    const listEl = document.getElementById("trip-list");
    listEl.innerHTML = "불러오는 중...";

    const res = await fetch(
        `/api/v1/trips/drivers/${driverId}?page=${page}&size=${size}`,
        {
            headers: {
                "Authorization": `Bearer ${token}`
            }
        }
    );

    const json = await res.json();
    if (!json.success) {
        listEl.innerHTML = "조회 실패";
        return;
    }

    const data = json.data;
    const trips = data.trips;

    if (trips.length === 0) {
        listEl.innerHTML = "운행 기록이 없습니다.";
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
        `${data.page + 1} / ${data.totalPages}`;
}

function nextPage() {
    page++;
    loadTrips();
}

function prevPage() {
    if (page > 0) page--;
    loadTrips();
}

loadTrips();
