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

    // ❌ 인증 문제
    if (res.status === 401 || res.status === 403) {
      alert("세션이 만료되었습니다. 다시 로그인해주세요.");
      location.href = "/index.html";
      return;
    }

    // ✅ 운행 없음
    if (res.status === 404) {
      if (!location.pathname.endsWith("/driver/dispatches/index.html")) {
        location.href = "/driver/dispatches/index.html";
      }
      return;
    }

    if (!res.ok) {
      location.href = "/driver/dispatches/index.html"; // 또는 /common/error.html
      return;
    }

    const { data: trip } = await res.json();

    if (!trip?.status || trip.status === "CANCELED" || trip.status === "ENDED") {
      location.href = "/driver/dispatches/index.html";
      return;
    }

    const currentPath = location.pathname;
    const queryTripId = `?tripId=${trip.tripId}`;

    switch (trip.status) {
      case "READY":
        if (!currentPath.endsWith("/driver/trips/ready.html")) {
          location.href = `/driver/trips/ready.html${queryTripId}`;
        }
        break;

      case "STARTED":
        if (!currentPath.endsWith("/driver/trips/active.html")) {
          location.href = `/driver/trips/active.html${queryTripId}`;
        }
        break;

      default:
        location.href = "/driver/dispatches/index.html";
    }

  } catch (e) {
    console.error("driverTripGuard 실패", e);
    location.href = "/driver/dispatches/index.html";
  }
}

driverTripGuard();
