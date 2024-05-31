document.addEventListener("DOMContentLoaded", function() {
    document.getElementById("findAccountForm").onsubmit = function(event) {
        event.preventDefault(); // 폼 제출을 방지하여 페이지 새로고침 방지

        var email = document.getElementById("email").value; // 이메일 입력값 가져오기

        if(validateEmail(email)) { // 이메일 형식 검증
            // 서버에 요청을 보내는 로직을 여기에 추가합니다. 예시에서는 간단히 콘솔에 출력만 함
            console.log("서버에 요청 보냄: ", email);
            alert("메일로 인증 정보를 담은 메일이 발송되었습니다. 메일이 보이지 않으면 스팸보관함을 확인하시기 바랍니다.")
            // 실제로는 여기에 AJAX 요청이나 fetch API를 사용하여 서버에 이메일을 보내고 결과를 처리합니다.
        } else {
            alert("올바른 이메일 주소를 입력해주세요.");
        }
    };
});

function validateEmail(email) {
    var re = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/; // 간단한 이메일 형식 검증 정규식
    return re.test(String(email).toLowerCase());
}
