let host = 'http://' + window.location.host;

$(document).ready(function () {
    const auth = getToken();
    if(auth === '') {
        $('#login-true').show();
        $('#login-false').hide();
        // window.location.href = host + "/api/user/login-page";
    } else {
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
    let auth = Cookies.get('Authorization');

    if(auth === undefined) {
        return '';
    }

    return auth;
}