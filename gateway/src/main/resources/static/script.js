const BASE_URL = "api/v1/auth/login";

async function login() {
    const email = document.getElementById("email").value;
    const pw = document.getElementById("password").value;

    if (!email || !pw) {
        alert("이메일과 비밀번호를 입력해주세요.");
        return;
    }

    const res = await fetch(BASE_URL, {
        method: "POST",
        headers: {"Content-Type": "application/json"},
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

    // 토큰 저장
    localStorage.setItem("accessToken", data.accessToken);
    localStorage.setItem("refreshToken", data.refreshToken);
    localStorage.setItem("userUuid", data.userUuid);
    localStorage.setItem("role", data.role);

    // role 별 라우팅
    switch (data.role) {
        case "PASSENGER":
            window.location.href = "/passenger/dashboard/index.html";
            break;

        case "DRIVER":
            window.location.href = "/driver/dashboard/index.html";
            break;

        case "ADMIN":
            window.location.href = "/admin/dashboard/index.html";
            break;

        case "MASTER_ADMIN":
            window.location.href = "/admin/dashboard/index.html";
            break;

        default:
            alert("알 수 없는 역할입니다.");
    }
}
