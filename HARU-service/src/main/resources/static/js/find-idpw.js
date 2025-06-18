document.addEventListener("DOMContentLoaded", function () {
  const btnFindId = document.getElementById("find-id-btn");
  const btnFindPw = document.getElementById("find-pw-btn");
  const formFindId = document.getElementById("form-find-id");
  const formFindPw = document.getElementById("form-find-pw");

  // 아이디 찾기 상태 관리
  let findIdStep = 'email'; // 'email' 또는 'verify'
  const findIdSubmitBtn = document.getElementById("find-id-submit-btn");
  const verificationGroupId = document.getElementById("verification-group-id");
  const stepInputId = document.getElementById("step-input-id");

  // 비밀번호 찾기 상태 관리
  let findPwStep = 'email'; // 'email' 또는 'verify'
  const findPwSubmitBtn = document.getElementById("find-pw-submit-btn");
  const verificationGroupPw = document.getElementById("verification-group-pw");
  const useridGroupPw = document.getElementById("userid-group-pw");
  const stepInputPw = document.getElementById("step-input-pw");

  // 탭 전환 이벤트
  btnFindId.addEventListener("click", () => {
    formFindId.style.display = "flex";
    formFindPw.style.display = "none";

    btnFindId.classList.remove("disabled");
    btnFindId.classList.add("active");
    btnFindPw.classList.remove("active");
    btnFindPw.classList.add("disabled");

    clearMessages();
    resetFindIdForm();
  });

  btnFindPw.addEventListener("click", () => {
    formFindPw.style.display = "flex";
    formFindId.style.display = "none";

    btnFindPw.classList.remove("disabled");
    btnFindPw.classList.add("active");
    btnFindId.classList.remove("active");
    btnFindId.classList.add("disabled");

    clearMessages();
    resetFindPwForm();
  });

  // 아이디 찾기 버튼 클릭 이벤트
  findIdSubmitBtn.addEventListener("click", function(e) {
    e.preventDefault();

    if (findIdStep === 'email') {
      // 이메일 단계: 인증번호 받기
      const email = document.querySelector("#form-find-id input[name='email']").value;
      if (!email) {
        showMessage("이메일을 입력해주세요.", true);
        return;
      }

      // 서버에 인증번호 요청
      requestVerificationCode('id', email);

    } else if (findIdStep === 'verify') {
      // 인증 단계: 아이디 찾기
      const code = document.getElementById("code-input-id").value;
      if (!code) {
        showMessage("인증번호를 입력해주세요.", true);
        return;
      }

      // 실제 폼 제출
      stepInputId.value = 'verify';
      formFindId.submit();
    }
  });

  // 비밀번호 찾기 버튼 클릭 이벤트
  findPwSubmitBtn.addEventListener("click", function(e) {
    e.preventDefault();

    if (findPwStep === 'email') {
      // 이메일 단계: 인증번호 받기
      const email = document.querySelector("#form-find-pw input[name='email']").value;
      const userId = document.querySelector("#form-find-pw input[name='userId']").value;

      if (!email) {
        showMessage("이메일을 입력해주세요.", true);
        return;
      }
      if (!userId) {
        showMessage("아이디를 입력해주세요.", true);
        return;
      }

      // 서버에 인증번호 요청
      requestVerificationCode('pw', email, userId);

    } else if (findPwStep === 'verify') {
      // 인증 단계: 비밀번호 재설정
      const code = document.getElementById("code-input-pw").value;
      if (!code) {
        showMessage("인증번호를 입력해주세요.", true);
        return;
      }

      // 실제 폼 제출
      stepInputPw.value = 'verify';
      formFindPw.submit();
    }
  });

  // 인증번호 요청 함수
  function requestVerificationCode(type, email, userId = null) {
    const submitBtn = type === 'id' ? findIdSubmitBtn : findPwSubmitBtn;
    const originalText = submitBtn.innerHTML;

    // 로딩 상태
    submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> 전송 중...';
    submitBtn.disabled = true;

    // 실제 환경에서는 AJAX로 서버에 요청
    // 여기서는 데모를 위해 setTimeout 사용
    setTimeout(() => {
      submitBtn.disabled = false;

      if (type === 'id') {
        // 아이디 찾기: 인증번호 입력 단계로 전환
        findIdStep = 'verify';
        verificationGroupId.style.display = 'block';
        submitBtn.innerHTML = '<span>아이디 찾기</span>';

        const emailInput = document.querySelector("#form-find-id input[name='email']");
        emailInput.readOnly = true;
        emailInput.style.backgroundColor = '#f9fafb';
        showMessage(`${email}로 인증번호가 전송되었습니다.`, false);

        // 인증번호 입력 필드에 포커스
        setTimeout(() => {
          document.getElementById("code-input-id").focus();
        }, 100);

      } else {
        // 비밀번호 찾기: 인증번호 입력 단계로 전환
        findPwStep = 'verify';
        verificationGroupPw.style.display = 'block';
        submitBtn.innerHTML = '<span>비밀번호 재설정</span>';

        const emailInput = document.querySelector("#form-find-pw input[name='email']");
        const userIdInput = document.querySelector("#form-find-pw input[name='userId']");
        emailInput.readOnly = true;
        userIdInput.readOnly = true;
        emailInput.style.backgroundColor = '#f9fafb';
        userIdInput.style.backgroundColor = '#f9fafb';
        showMessage(`${email}로 인증번호가 전송되었습니다.`, false);

        // 인증번호 입력 필드에 포커스
        setTimeout(() => {
          document.getElementById("code-input-pw").focus();
        }, 100);
      }
    }, 2000);
  }

  // 폼 리셋 함수들
  function resetFindIdForm() {
    findIdStep = 'email';
    stepInputId.value = 'email';
    verificationGroupId.style.display = 'none';
    findIdSubmitBtn.innerHTML = '<span>인증번호 받기</span>';
    findIdSubmitBtn.disabled = false;

    const emailInput = document.querySelector("#form-find-id input[name='email']");
    const codeInput = document.getElementById("code-input-id");

    emailInput.readOnly = false;
    emailInput.style.backgroundColor = '';
    emailInput.value = '';
    codeInput.value = '';
  }

  function resetFindPwForm() {
    findPwStep = 'email';
    stepInputPw.value = 'email';
    verificationGroupPw.style.display = 'none';
    findPwSubmitBtn.innerHTML = '<span>인증번호 받기</span>';
    findPwSubmitBtn.disabled = false;

    const emailInput = document.querySelector("#form-find-pw input[name='email']");
    const userIdInput = document.querySelector("#form-find-pw input[name='userId']");
    const codeInput = document.getElementById("code-input-pw");

    emailInput.readOnly = false;
    userIdInput.readOnly = false;
    emailInput.style.backgroundColor = '';
    userIdInput.style.backgroundColor = '';
    emailInput.value = '';
    userIdInput.value = '';
    codeInput.value = '';
  }

  function clearMessages() {
    // 메시지 컨테이너가 있다면 클리어
    const errorMsg = document.getElementById("error-msg");
    if (errorMsg) {
      errorMsg.style.display = "none";
    }

    // Thymeleaf 메시지도 숨김
    const thymeleafMessages = document.querySelectorAll("p[th\\:if]");
    thymeleafMessages.forEach(msg => {
      msg.style.display = "none";
    });
  }

  // 폼 제출 시 로딩 상태 표시
  const forms = document.querySelectorAll('.regist_form');
  forms.forEach(form => {
    form.addEventListener('submit', function(e) {
      const submitBtn = form.querySelector('.green-btn');
      const originalText = submitBtn.innerHTML;

      submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> 처리 중...';
      submitBtn.disabled = true;

      // 실제 환경에서는 제거하세요 - 데모용 복원
      setTimeout(() => {
        submitBtn.innerHTML = originalText;
        submitBtn.disabled = false;
      }, 3000);
    });
  });
});

// 메시지 표시 함수
function showMessage(message, isError = false) {
  // 기존 메시지들 숨김
  const errorMsg = document.getElementById("error-msg");
  if (errorMsg) {
    errorMsg.style.display = "none";
  }

  // 새 메시지 생성
  const linksSection = document.querySelector(".links-section");

  // 기존 동적 메시지 제거
  const existingMsg = document.querySelector(".dynamic-message");
  if (existingMsg) {
    existingMsg.remove();
  }

  const messageDiv = document.createElement("div");
  messageDiv.className = "dynamic-message";
  messageDiv.style.cssText = `
    margin: 20px 0;
    padding: 12px 16px;
    border-radius: 8px;
    font-size: 0.9rem;
    display: flex;
    align-items: center;
    gap: 8px;
    ${isError ?
      'background: linear-gradient(135deg, #fee2e2 0%, #fecaca 100%); color: #dc2626; border: 1px solid #fca5a5;' :
      'background: linear-gradient(135deg, #d1fae5 0%, #a7f3d0 100%); color: #065f46; border: 1px solid #6ee7b7;'
  }
  `;

  const icon = isError ? "fas fa-exclamation-circle" : "fas fa-check-circle";
  messageDiv.innerHTML = `<i class="${icon}"></i><span>${message}</span>`;

  linksSection.parentNode.insertBefore(messageDiv, linksSection);
}