const BASE_URL = "/api/v1/auth/login";

async function login() {
    const email = document.getElementById("email").value;
    const pw = document.getElementById("password").value;

    if (!email || !pw) {
        alert("이메일과 비밀번호를 입력해주세요.");
        return;
    }

    const res = await fetch(BASE_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            email,
            password: pw
        })
    });

    const json = await res.json();

    if (!json.success) {
        alert(json.message || "로그인 실패");
        return;
    }

    const data = json.data;

    localStorage.setItem("accessToken", data.accessToken);
    localStorage.setItem("refreshToken", data.refreshToken);
    localStorage.setItem("userUuid", data.userUuid);
    localStorage.setItem("role", data.role);

    switch (data.role) {
        case "PASSENGER":
            await redirectPassengerAfterLogin();
            break;

        case "DRIVER":
            await redirectDriverAfterLogin();
            break;

        case "ADMIN":
        case "MASTER_ADMIN":
            location.href = "/admin/dashboard/index.html";
            break;

        default:
            alert("알 수 없는 역할입니다.");
    }
}

/* =================================================
   PASSENGER: 로그인 후 상태 복구 (핵심)
================================================= */
async function redirectPassengerAfterLogin() {
    const token = localStorage.getItem("accessToken");
    const uuid = localStorage.getItem("userUuid");

    const headers = {
        "Authorization": `Bearer ${token}`,
        "X-User-UUID": uuid,
        "X-User-Role": "PASSENGER"
    };

    try {
        /* 1️⃣ 먼저 active 운행 확인 */
        const tripRes = await fetch("/api/v1/trips/passengers/active", { headers });

        if (tripRes.ok) {
            const { data: trip } = await tripRes.json();

            if (trip?.tripId) {
                switch (trip.status) {
                    case "READY":
                        location.href = `/passenger/trips/ready.html?tripId=${trip.tripId}`;
                        return;
                    case "STARTED":
                        location.href = `/passenger/trips/active.html?tripId=${trip.tripId}`;
                        return;
                }
            }
        }

        /* 2️⃣ active 없음 → 미결제 ENDED 운행 있는지 결제 조회 */
        const paymentRes = await fetch("/api/v1/payments/latest", { headers });

        // 💡 이 API는
        // - 미결제 ENDED 운행 있으면 200 + tripId
        // - 없으면 404 / 204 라고 가정
        if (paymentRes.ok) {
            const { data: payment } = await paymentRes.json();

            if (payment?.tripId && payment.status !== "PAID") {
                location.href = `/passenger/trips/completed.html?tripId=${payment.tripId}`;
                return;
            }
        }

        /* 3️⃣ 아무 것도 없으면 대시보드 */
        location.href = "/passenger/dashboard/index.html";

    } catch (e) {
        console.error("승객 로그인 후 상태 복구 실패", e);
        location.href = "/passenger/dashboard/index.html";
    }
}

/* =================================================
   DRIVER: 로그인 후 상태 복구
================================================= */
async function redirectDriverAfterLogin() {
    const token = localStorage.getItem("accessToken");
    const uuid = localStorage.getItem("userUuid");

    const headers = {
        "Authorization": `Bearer ${token}`,
        "X-User-UUID": uuid,
        "X-User-Role": "DRIVER"
    };

    try {
        const res = await fetch("/api/v1/trips/drivers/active", { headers });

        if (res.ok) {
            const { data: trip } = await res.json();

            switch (trip.status) {
                case "READY":
                    location.href = "/driver/trips/ready.html";
                    return;
                case "STARTED":
                    location.href = "/driver/trips/active.html";
                    return;
            }
        }

        location.href = "/driver/dashboard/index.html";

    } catch (e) {
        console.error("기사 로그인 후 상태 복구 실패", e);
        location.href = "/driver/dashboard/index.html";
    }
}
