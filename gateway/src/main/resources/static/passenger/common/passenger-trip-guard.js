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

    switch (trip.status) {
      case "READY":
        location.href = "/passenger/trips/ready.html";
        break;
      case "STARTED":
        location.href = "/passenger/trips/active.html";
        break;
      case "ENDED":
        location.href = `/passenger/trips/completed.html?tripId=${trip.tripId}`;
        break;
    }
  } catch (e) {
    console.error("guard 처리 실패", e);
    alert("서버 응답을 확인할 수 없습니다.");
    location.href = "/passenger/dashboard/index.html";
  }
}
