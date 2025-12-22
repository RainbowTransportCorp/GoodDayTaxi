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
            email: email,
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
            await redirectPassengerByTripState();
            break;
        case "DRIVER":
            await redirectDriverByTripState();
            break;
        case "ADMIN":
        case "MASTER_ADMIN":
            location.href = "/admin/dashboard/index.html";
            break;
        default:
            alert("알 수 없는 역할입니다.");
    }
}

/* ================= PASSENGER ================= */
async function redirectPassengerByTripState() {
    const token = localStorage.getItem("accessToken");
    const uuid = localStorage.getItem("userUuid");

    try {
        const res = await fetch("/api/v1/trips/passengers/active", {
            headers: {
                "Authorization": `Bearer ${token}`,
                "X-User-UUID": uuid,
                "X-User-Role": "PASSENGER"
            }
        });

        if (res.status === 404 || res.status === 204) {
            location.href = "/passenger/dashboard/index.html";
            return;
        }

        if (res.status === 401 || res.status === 403) {
            alert("세션이 만료되었습니다. 다시 로그인해주세요.");
            location.href = "/index.html";
            return;
        }

        if (!res.ok) {
            throw new Error("PASSENGER_TRIP_SYNC_FAIL");
        }

        const { data: trip } = await res.json();

        if (!trip?.tripId) {
            console.warn("응답에 tripId 없음", trip);
            alert("운행 정보가 올바르지 않습니다.");
            location.href = "/passenger/dashboard/index.html";
            return;
        }

        switch (trip.status) {
            case "READY":
                location.href = `/passenger/trips/ready.html?tripId=${trip.tripId}`;
                break;
            case "STARTED":
                location.href = `/passenger/trips/active.html?tripId=${trip.tripId}`;
                break;
            case "ENDED":
                location.href = `/passenger/trips/completed.html?tripId=${trip.tripId}`;
                break;
            default:
                console.warn("알 수 없는 trip status", trip.status);
                location.href = "/passenger/dashboard/index.html";
        }

    } catch (e) {
        console.error("승객 운행 상태 동기화 실패", e);
        location.href = "/passenger/dashboard/index.html";
    }
}

/* ================= DRIVER ================= */
async function redirectDriverByTripState() {
    const token = localStorage.getItem("accessToken");
    const uuid = localStorage.getItem("userUuid");

    try {
        const res = await fetch("/api/v1/trips/drivers/active", {
            headers: {
                "Authorization": `Bearer ${token}`,
                "X-User-UUID": uuid,
                "X-User-Role": "DRIVER"
            }
        });

        if (res.status === 404 || res.status === 204) {
            location.href = "/driver/dashboard/index.html";
            return;
        }

        if (res.status === 401 || res.status === 403) {
            alert("세션이 만료되었습니다. 다시 로그인해주세요.");
            location.href = "/index.html";
            return;
        }

        if (!res.ok) {
            throw new Error("DRIVER_TRIP_SYNC_FAIL");
        }

        const { data: trip } = await res.json();

        switch (trip.status) {
            case "READY":
                location.href = "/driver/trips/ready.html";
                break;
            case "STARTED":
                location.href = "/driver/trips/active.html";
                break;
            default:
                location.href = "/driver/dashboard/index.html";
        }

    } catch (e) {
        console.error("기사 운행 상태 동기화 실패", e);
        location.href = "/driver/dashboard/index.html";
    }
}
