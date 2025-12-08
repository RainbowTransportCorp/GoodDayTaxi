const BASE_URL = "/api/v1/dispatches";

let pollingTimer = null;

// 화면 전환
function show(sectionId) {
  document.querySelectorAll("section").forEach(sec => sec.style.display = "none");
  document.getElementById(sectionId).style.display = "block";
}

// 콜 생성
async function createDispatch() {
  const pickup = document.getElementById("pickup").value;
  const destination = document.getElementById("destination").value;

  if (!pickup || !destination) {
    alert("출발지와 도착지를 모두 입력해주세요.");
    return;
  }

  const res = await fetch(BASE_URL, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      pickupAddress: pickup,
      destinationAddress: destination
    })
  });

  const json = await res.json();
  const dispatchId = json.data.dispatchId;

  // 대기 화면으로 이동
  document.getElementById("waiting-text").textContent =
      `배차를 요청했습니다. (ID: ${dispatchId})`;
  show("waiting-section");

  // 폴링 시작
  startPolling(dispatchId);
}

// 배차 상태 체크 (폴링)
function startPolling(dispatchId) {
  if (pollingTimer) clearInterval(pollingTimer);

  pollingTimer = setInterval(async () => {
    const res = await fetch(`${BASE_URL}/${dispatchId}`);
    const json = await res.json();

    const status = json.data.status;

    if (status === "ASSIGNED" || status === "CONFIRMED") {
      clearInterval(pollingTimer);
      showDetail(json.data);
    }
  }, 3000);
}

// 상세 화면 표시
function showDetail(data) {
  const detailEl = document.getElementById("detail-container");

  detailEl.innerHTML = `
        <div><b>출발지:</b> ${data.pickupAddress}</div>
        <div><b>도착지:</b> ${data.destinationAddress}</div>
        <div><b>상태:</b> ${data.status}</div>
    `;

  show("detail-section");
}

// 홈으로 돌아가기
function backToHome() {
  if (pollingTimer) clearInterval(pollingTimer);
  show("create-section");
}
