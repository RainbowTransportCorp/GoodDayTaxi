async function driverTripGuard() {
  const token = localStorage.getItem("accessToken");
  const uuid = localStorage.getItem("userUuid");
  const role = localStorage.getItem("role");

  if (!token || !uuid || role !== "DRIVER") {
    location.href = "/index.html";
    return;
  }

  try {
    const res = await fetch("/api/v1/trips/drivers/active", {
      headers: {
        "Authorization": `Bearer ${token}`,
        "X-User-UUID": uuid,
        "X-User-Role": "DRIVER"
      }
    });

    // ✅ 운행 없음 → 콜 대기 화면
    if (res.status === 404 || res.status === 204) {
      if (!location.pathname.includes("/driver/dispatches/index.html")) {
        location.href = "/driver/dispatches/index.html";
      }
      return;
    }

    // ❌ 인증 문제
    if (res.status === 401 || res.status === 403) {
      alert("세션이 만료되었습니다. 다시 로그인해주세요.");
      location.href = "/index.html";
      return;
    }

    // ❌ 진짜 서버 에러
    if (!res.ok) {
      location.href = "/common/error.html";
      return;
    }

    const { data: trip } = await res.json();

    if (!trip || !trip.status) {
      location.href = "/driver/dispatches/index.html";
      return;
    }

    switch (trip.status) {
      case "READY":
        if (!location.pathname.includes("/driver/trips/ready.html")) {
          location.href = "/driver/trips/ready.html";
        }
        break;

      case "STARTED":
        if (!location.pathname.includes("/driver/trips/active.html")) {
          location.href = "/driver/trips/active.html";
        }
        break;

      case "ENDED":
        // 재로그인 / 새로고침 시에도 항상 접근 허용
        if (!location.pathname.includes("/driver/trips/completed.html")) {
          location.href = `/driver/trips/completed.html?tripId=${trip.tripId}`;
        }
        break;

      case "CANCELED":
      default:
        if (!location.pathname.includes("/driver/dispatches/index.html")) {
          location.href = "/driver/dispatches/index.html";
        }
    }

  } catch (e) {
    console.error("driverTripGuard 실패", e);
    location.href = "/driver/dispatches/index.html";
  }
}

driverTripGuard();
