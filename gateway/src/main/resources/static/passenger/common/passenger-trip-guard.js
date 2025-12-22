async function passengerTripGuard({ onTrip } = {}) {
  const token = localStorage.getItem("accessToken");
  const uuid = localStorage.getItem("userUuid");

  if (!token || !uuid) {
    alert("로그인이 필요합니다.");
    location.href = "/index.html";
    return;
  }

  try {
    const res = await fetch("/api/v1/trips/passengers/active", {
      headers: {
        "Authorization": `Bearer ${token}`,
        "X-User-UUID": uuid,
        "X-User-Role": "PASSENGER"
      }
    });

    if (res.status === 404 || res.status === 204) {
      location.href = "/passenger/dispatches/index.html";
      return;
    }

    if (!res.ok) {
      const errorText = await res.text();
      console.error("Trip 상태 확인 실패:", res.status, errorText);
      alert("서버에 문제가 발생했습니다. 잠시 후 다시 시도해주세요.");
      location.href = "/passenger/dashboard/index.html";
      return;
    }

    const { data: trip } = await res.json();

    if (onTrip) onTrip(trip); // ⭐ trip 상태로 화면 렌더링 할 때

// 👇 이미 페이지가 그 상태에 맞는 곳이면 이동하지 않게
    switch (trip.status) {
      case "READY":
        if (!location.pathname.includes("ready.html")) {
          location.href = "/passenger/trips/ready.html";
        }
        break;

      case "STARTED":
        if (!location.pathname.includes("active.html")) {
          location.href = "/passenger/trips/active.html";
        }
        break;

      case "ENDED":
        // 항상 completed.html은 query로 접근하니 무조건 이동
        location.href = `/passenger/trips/completed.html?tripId=${trip.tripId}`;
        break;

      default:
        console.warn("예상치 못한 상태값:", trip.status);
        location.href = "/passenger/dashboard/index.html";
    }

  } catch (e) {
    console.error("guard 처리 실패", e);
    alert("서버 응답을 확인할 수 없습니다.");
    location.href = "/passenger/dashboard/index.html";
  }
}
