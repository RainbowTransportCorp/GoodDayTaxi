const BASE_URL = "/api/v1/auth/signup";

// 역할 선택 시 차량 정보 보이기/숨기기
document.getElementById("role").addEventListener("change", function () {
    const section = document.getElementById("vehicle-section");
    section.style.display = this.value === "DRIVER" ? "block" : "none";
});

async function signup() {
    const body = {
        email: document.getElementById("email").value,
        password: document.getElementById("password").value,
        name: document.getElementById("name").value,
        phoneNumber: document.getElementById("phone").value,
        slackId: document.getElementById("slackId").value || null,
        role: document.getElementById("role").value,
        vehicleNumber: document.getElementById("vehicleNumber")?.value || null,
        vehicleType: document.getElementById("vehicleType")?.value || null,
        vehicleColor: document.getElementById("vehicleColor")?.value || null
    };

    const res = await fetch(BASE_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body)
    });

    const json = await res.json();

    if (json.success) {
        alert("회원가입이 완료되었습니다!");
        window.location.href = "/index.html";
    } else {
        alert("오류: " + (json.message || "회원가입 실패"));
    }
}
