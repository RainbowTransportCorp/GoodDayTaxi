async function driverTripGuard() {
  const token = localStorage.getItem("accessToken");
  const uuid = localStorage.getItem("userUuid");
  const role = localStorage.getItem("role");

  if (!token || !uuid || role !== "DRIVER") {
    location.href = "/index.html";
    return;
  }

  const res = await fetch("/api/v1/trips/drivers/active", {
    headers: {
      "Authorization": `Bearer ${token}`,
      "X-User-UUID": uuid,
      "X-User-Role": "DRIVER"
    }
  });

  /* ✅ 운행 없음 = 정상 (콜 대기 화면) */
  if (res.status === 404 || res.status === 204) {
    location.href = "/driver/dispatches/index.html";
    return;
  }

  /* ❌ 진짜 서버 에러만 */
  if (!res.ok) {
    location.href = "/common/error.html";
    return;
  }

  const {data: trip} = await res.json();

  switch (trip.status) {
    case "READY":
      location.href = "/driver/trips/ready.html";
      break;

    case "STARTED":
      location.href = "/driver/trips/active.html";
      break;

    case "ENDED":
      location.href =
          `/driver/trips/completed.html?tripId=${trip.tripId}`;
      break;

    case "CANCELED":
      location.href = "/driver/dispatches/index.html";
      break;

    default:
      location.href = "/driver/dispatches/index.html";
  }
}

driverTripGuard();
