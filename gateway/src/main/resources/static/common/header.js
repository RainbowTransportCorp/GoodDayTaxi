/* ===== Common Header Logic ===== */

function getToken() {
    return localStorage.getItem("accessToken");
}

function getRole() {
    return localStorage.getItem("role");
}

function getEmail() {
    return localStorage.getItem("email");
}

function getDisplayName() {
    const email = getEmail();
    if (!email) return "사용자";
    return email.split("@")[0];
}

function logout() {
    localStorage.clear();
    window.location.href = "/index.html";
}

// 페이지 진입 시: 공통 헤더 HTML 삽입 및 사용자 정보/인디케이터 렌더링
document.addEventListener("DOMContentLoaded", async () => {
    const headerContainer = document.querySelector("header");
    if (!headerContainer) return;

    // 📌 공통 header.html 삽입
    try {
        const res = await fetch("/common/header.html");
        const html = await res.text();
        headerContainer.innerHTML = html;
    } catch (e) {
        console.error("header.html 불러오기 실패", e);
        return;
    }

    // 사용자 이름 표시
    const nameEl = document.getElementById("user-name");
    if (nameEl) {
        nameEl.textContent = `${getDisplayName()}님`;
    }

    // 사용자 역할 표시
    const subEl = document.getElementById("brand-sub");
    if (subEl) {
        switch (getRole()) {
            case "PASSENGER":
                subEl.textContent = "승객";
                break;
            case "DRIVER":
                subEl.textContent = "기사";
                break;
            case "ADMIN":
                subEl.textContent = "관리자";
                break;
            case "MASTER_ADMIN":
                subEl.textContent = "최고 관리자";
                break;
            default:
                subEl.textContent = "서비스";
        }
    }

    // 🚨 상태 인디케이터 동적 추가
    const role = getRole();
    if (role === "PASSENGER") renderPassengerIndicators();
    if (role === "DRIVER") renderDriverIndicators();
});

/* ====== 상태 인디케이터 (운행중, 미결제) ====== */

async function renderPassengerIndicators() {
    const indicatorBox = document.getElementById("top-indicators");
    if (!indicatorBox) return;

    const tripId = localStorage.getItem("tripId");
    const tripStatus = localStorage.getItem("tripStatus");

    if (tripId && (tripStatus === "READY" || tripStatus === "STARTED")) {
        const tripBtn = document.createElement("button");
        tripBtn.className = "btn-indicator";
        tripBtn.innerHTML = "🚕 운행중";
        tripBtn.onclick = () => {
            const url = `/passenger/trips/${tripStatus.toLowerCase()}.html?tripId=${tripId}`;
            location.href = url;
        };
        indicatorBox.appendChild(tripBtn);
    }

    // ✅ 미결제 건 확인 (search API 사용)
    const searchParams = new URLSearchParams({
        page: 0,
        size: 1,
        status: "REQUESTED",
        searchPeriod: "ALL",
        sortBy: "createdAt",
        sortAscending: false
    });

    try {
        const res = await fetch(`/api/v1/payments/search?${searchParams.toString()}`, {
            headers: {
                "Authorization": `Bearer ${getToken()}`,
                "X-User-UUID": localStorage.getItem("userUuid"),
                "X-User-Role": "PASSENGER"
            }
        });

        const json = await res.json();
        const list = json?.data?.content ?? [];

        if (list.length > 0) {
            const payBtn = document.createElement("button");
            payBtn.className = "btn-indicator warning";
            payBtn.innerHTML = "💳 미결제";
            payBtn.onclick = () => {
                location.href = "/passenger/payments/index.html?filter=UNPAID";
            };
            indicatorBox.appendChild(payBtn);
        }
    } catch (err) {
        console.warn("미결제 확인 실패", err);
    }
}

function renderDriverIndicators() {
    const indicatorBox = document.getElementById("top-indicators");
    if (!indicatorBox) return;

    const tripId = localStorage.getItem("tripId");
    const tripStatus = localStorage.getItem("tripStatus");

    if (tripId && (tripStatus === "READY" || tripStatus === "STARTED")) {
        const tripBtn = document.createElement("button");
        tripBtn.className = "btn-indicator";
        tripBtn.innerHTML = "🚕 운행중";
        tripBtn.onclick = () => {
            const url = `/driver/trips/${tripStatus.toLowerCase()}.html?tripId=${tripId}`;
            location.href = url;
        };
        indicatorBox.appendChild(tripBtn);
    }
}
