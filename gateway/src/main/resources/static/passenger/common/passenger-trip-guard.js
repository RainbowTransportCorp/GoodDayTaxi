async function passengerTripGuard({ onTrip } = {}) {
  const token = localStorage.getItem("accessToken");
  const uuid = localStorage.getItem("userUuid");
  const role = localStorage.getItem("role");

  if (!token || !uuid || role !== "PASSENGER") {
    alert("로그인이 필요합니다.");
    location.href = "/index.html";
    return;
  }

  const headers = {
    "Authorization": `Bearer ${token}`,
    "X-User-UUID": uuid,
    "X-User-Role": "PASSENGER"
  };

  /* ================= 미결제 fallback (내부 함수) ================= */
  async function handleUnpaidFallback() {
    try {
      const params = new URLSearchParams({
        page: "1",
        size: "1",
        status: "PENDING",
        searchPeriod: "ALL",
        sortBy: "createdAt",
        sortAscending: "false"
      });

      const res = await fetch(`/api/v1/payments/search?${params.toString()}`, {
        headers
      });

      if (!res.ok) {
        location.href = "/passenger/dashboard/index.html";
        return;
      }

      const json = await res.json();
      const list = json?.data?.content ?? [];

      if (list.length > 0 && list[0].tripId) {
        location.href = `/passenger/payments/index.html?tripId=${list[0].tripId}`;
      } else {
        location.href = "/passenger/dashboard/index.html";
      }

    } catch (e) {
      console.error("미결제 fallback 실패", e);
      location.href = "/passenger/dashboard/index.html";
    }
  }

  /* ================= 운행 상태 체크 ================= */
  try {
    const res = await fetch("/api/v1/trips/passengers/active", { headers });

    if (res.status === 401 || res.status === 403) {
      alert("세션이 만료되었습니다. 다시 로그인해주세요.");
      location.href = "/index.html";
      return;
    }

    // ✅ 운행 없음 → 미결제 fallback
    if (res.status === 404 || res.status === 204) {
      await handleUnpaidFallback();
      return;
    }

    if (!res.ok) {
      location.href = "/passenger/dashboard/index.html";
      return;
    }

    const { data: trip } = await res.json();
    if (!trip || !trip.tripId) {
      await handleUnpaidFallback();
      return;
    }

    try {
      onTrip?.(trip);
    } catch (_) {}

    const query = `?tripId=${trip.tripId}`;

    switch (trip.status) {
      case "READY":
        if (!location.pathname.endsWith("ready.html")) {
          location.href = `/passenger/trips/ready.html${query}`;
        }
        break;

      case "STARTED":
        if (!location.pathname.endsWith("active.html")) {
          location.href = `/passenger/trips/active.html${query}`;
        }
        break;

      case "ENDED":
        try {
          const payRes = await fetch(`/api/v1/payments?tripId=${trip.tripId}`, {
            headers
          });

          if (payRes.ok) {
            const { data: payment } = await payRes.json();

            if (payment?.status === "COMPLETED") {
              location.href = `/passenger/trips/ended.html${query}`;
            } else {
              location.href = `/passenger/payments/index.html${query}`;
            }
          } else {
            location.href = `/passenger/payments/index.html${query}`;
          }
        } catch (e) {
          location.href = `/passenger/payments/index.html${query}`;
        }
        break;

      default:
        location.href = "/passenger/dashboard/index.html";
    }

  } catch (e) {
    console.error("passengerTripGuard 실패", e);
    location.href = "/passenger/dashboard/index.html";
  }
}
