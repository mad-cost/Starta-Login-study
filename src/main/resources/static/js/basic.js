let host = 'http://' + window.location.host; // localhost:8080

$(document).ready(function () {
    const auth = getToken();
    // getToken() 에서 받아온 Cookie의 Authorization의 auth 가 빈 문자열('')이면
    if(auth === '') {
        // 사용자를 로그인 페이지로 리다이렉트합니다.
        window.location.href = host + "/api/user/login-page";
    } else {
        // auth 변수가 빈 문자열이 아니면, 로그인된 상태로 간주하고 #login-true 요소를 표시하고,  #login-false 요소를 숨깁니다.
        $('#login-true').show();
        $('#login-false').hide();
    }
})


// TODO: 로그아웃 구현
function logout() {
    // 토큰 삭제
    Cookies.remove('Authorization', { path: '/' });
    // 토큰 삭제 후 이동 URL
    window.location.href = host + "/api/user/login-page";
}

function getToken() {
    //  Cookies.get('Authorization'): Authorization의 Value를 auth에 넣어준다
    let auth = Cookies.get('Authorization');
    // ("Value" : "Barer%20...")토큰이 없을 경우 빈 문자열('')을 반환
    if(auth === undefined) {
        return '';
    }

    return auth;
}