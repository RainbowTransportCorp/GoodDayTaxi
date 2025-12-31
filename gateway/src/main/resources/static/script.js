const BASE_URL = "/api/v1/auth/login";

/* ================= 공통 유틸 ================= */

async function safeReadJson(res) {
  if (res.status === 204) return null;
  const text = await res.text();
  if (!text) return null;
  try {
    return JSON.parse(text);
  } catch {
    return null;
  }
}

function buildHeaders(role) {
  const token = localStorage.getItem("accessToken");
  const uuid = localStorage.getItem("userUuid");
  return {
    "Authorization": `Bearer ${token}`,
    "X-User-UUID": uuid,
    "X-User-Role": role
  };
}

function handleAuthExpired() {
  alert("로그인이 만료되었습니다. 다시 로그인해주세요.");
  location.href = "/index.html";
}

/* ================= 로그인 ================= */

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
    body: JSON.stringify({ email, password: pw })
  });

  const json = await safeReadJson(res);

  if (!res.ok || !json?.success) {
    alert(json?.message || "로그인 실패");
    return;
  }

  const data = json.data;

  localStorage.setItem("accessToken", data.accessToken);
  localStorage.setItem("refreshToken", data.refreshToken);
  localStorage.setItem("userUuid", data.userUuid);
  localStorage.setItem("role", data.role);
  localStorage.setItem("email", data.email);

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

/* ================= PASSENGER ================= */

async function redirectPassengerAfterLogin() {
  const headers = buildHeaders("PASSENGER");

  try {
    const res = await fetch("/api/v1/trips/passengers/active", { headers });

    if (res.status === 401 || res.status === 403) {
      handleAuthExpired();
      return;
    }

    if (res.ok) {
      const json = await safeReadJson(res);
      const trip = json?.data ?? null;

      if (trip?.tripId) {
        localStorage.setItem("tripId", trip.tripId);
        localStorage.setItem("tripStatus", trip.status);

        if (trip.status === "READY") {
          location.href = `/passenger/trips/ready.html?tripId=${trip.tripId}`;
          return;
        }

        if (trip.status === "STARTED") {
          location.href = `/passenger/trips/active.html?tripId=${trip.tripId}`;
          return;
        }
      }
    }

  } catch (e) {
    console.error("🚨 승객 로그인 후 상태 복구 실패:", e);
  }

  location.href = "/passenger/dashboard/index.html";
}

/* ================= DRIVER ================= */

async function redirectDriverAfterLogin() {
  const headers = buildHeaders("DRIVER");

  try {
    const res = await fetch("/api/v1/trips/drivers/active", { headers });

    if (res.status === 401 || res.status === 403) {
      handleAuthExpired();
      return;
    }

    if (res.ok) {
      const json = await safeReadJson(res);
      const trip = json?.data ?? null;

      if (trip?.tripId) {
        localStorage.setItem("tripId", trip.tripId);
        localStorage.setItem("tripStatus", trip.status);

        if (trip.status === "READY") {
          location.href = `/driver/trips/ready.html?tripId=${trip.tripId}`;
          return;
        }

        if (trip.status === "STARTED") {
          location.href = `/driver/trips/active.html?tripId=${trip.tripId}`;
          return;
        }
      }
    }

  } catch (e) {
    console.error("🚨 기사 로그인 후 상태 복구 실패:", e);
  }

  location.href = "/driver/dashboard/index.html";
}

/* ================= ADMIN REGISTER TRIGGER ================= */

function goAdminRegister() {
  const ADMIN_TRIGGER_KEY = "GD-TAXI-ADMIN-2025"; // 🔐 임시 키 (배포 후 변경 가능)

  const input = prompt("관리자 등록 키를 입력하세요.");

  if (!input) {
    alert("취소되었습니다.");
    return;
  }

  if (input !== ADMIN_TRIGGER_KEY) {
    alert("인증에 실패했습니다.");
    return;
  }

  // ✅ 통과 시 세션에 잠금 해제 기록
  sessionStorage.setItem("admin-register-unlocked", "true");
  location.href = "/admin/register.html";
}

