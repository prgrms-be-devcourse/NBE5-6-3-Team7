document.addEventListener("DOMContentLoaded", function () {
  const btnFindId = document.getElementById("find-id-btn");
  const btnFindPw = document.getElementById("find-pw-btn");
  const formFindId = document.getElementById("form-find-id");
  const formFindPw = document.getElementById("form-find-pw");

  // 아이디 찾기 상태 관리
  let findIdStep = window.findIdStep || 'email';
  const findIdSubmitBtn = document.getElementById("find-id-submit-btn");
  const verificationGroupId = document.getElementById("verification-group-id");

  // 비밀번호 찾기 상태 관리
  let findPwStep = window.findPwStep || 'email';
  const findPwSubmitBtn = document.getElementById("find-pw-submit-btn");
  const verificationGroupPw = document.getElementById("verification-group-pw");

  // 탭 전환 이벤트
  btnFindId.addEventListener("click", () => {
    if (btnFindId.disabled) return;

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
    if (btnFindPw.disabled) return;

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
      const email = document.querySelector("#form-find-id input[name='email']").value.trim();
      if (!email) {
        showMessage("이메일을 입력해주세요.", true);
        return;
      }

      if (!isValidEmail(email)) {
        showMessage("유효한 이메일 형식이 아닙니다.", true);
        return;
      }

      // 일반 폼 제출로 인증번호 요청
      submitFormWithLoading(formFindId, findIdSubmitBtn, '/auth/auth-idpw');

    } else if (findIdStep === 'verify') {
      const code = document.getElementById("code-input-id").value.trim();

      if (!code) {
        showMessage("인증번호를 입력해주세요.", true);
        return;
      }

      // 일반 폼 제출로 인증번호 검증
      submitFormWithLoading(formFindId, findIdSubmitBtn, '/auth/auth-idpw-verification');
    }
  });

  // 비밀번호 찾기 버튼 클릭 이벤트
  findPwSubmitBtn.addEventListener("click", function(e) {
    e.preventDefault();

    if (findPwStep === 'email') {
      const email = document.querySelector("#form-find-pw input[name='email']").value.trim();
      const userId = document.querySelector("#form-find-pw input[name='userId']").value.trim();

      if (!email) {
        showMessage("이메일을 입력해주세요.", true);
        return;
      }
      if (!userId) {
        showMessage("아이디를 입력해주세요.", true);
        return;
      }

      if (!isValidEmail(email)) {
        showMessage("유효한 이메일 형식이 아닙니다.", true);
        return;
      }

      // 일반 폼 제출로 인증번호 요청
      submitFormWithLoading(formFindPw, findPwSubmitBtn, '/auth/auth-idpw');

    } else if (findPwStep === 'verify') {
      const code = document.getElementById("code-input-pw").value.trim();

      if (!code) {
        showMessage("인증번호를 입력해주세요.", true);
        return;
      }

      // 일반 폼 제출로 인증번호 검증
      submitFormWithLoading(formFindPw, findPwSubmitBtn, '/auth/auth-idpw-verification');
    }
  });

  // 폼 제출 함수 (로딩 상태 포함)
  function submitFormWithLoading(form, button, action) {
    const originalText = button.innerHTML;
    const originalAction = form.action;

    // 로딩 상태로 변경
    button.innerHTML = '<i class="fas fa-spinner fa-spin"></i> 처리 중...';
    button.disabled = true;

    // 폼 액션 변경
    form.action = action;

    // 잠시 후 폼 제출 (사용자가 로딩 상태를 볼 수 있도록)
    setTimeout(() => {
      form.submit();
    }, 300);
  }

  // 이메일 유효성 검사 함수
  function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }

  // 폼 리셋 함수들
  function resetFindIdForm() {
    if (findIdStep === 'verify') return;

    findIdStep = 'email';
    verificationGroupId.style.display = 'none';
    findIdSubmitBtn.innerHTML = '<span>인증번호 받기</span>';
    findIdSubmitBtn.disabled = false;

    const emailInput = document.querySelector("#form-find-id input[name='email']");
    const codeInput = document.getElementById("code-input-id");

    emailInput.readOnly = false;
    emailInput.style.backgroundColor = '';
    emailInput.value = '';
    if (codeInput) codeInput.value = '';
  }

  function resetFindPwForm() {
    if (findPwStep === 'verify') return;

    findPwStep = 'email';
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
    if (codeInput) codeInput.value = '';
  }

  function clearMessages() {
    const errorMsg = document.getElementById("error-msg");
    if (errorMsg) {
      errorMsg.style.display = "none";
    }

    const existingMsg = document.querySelector(".dynamic-message");
    if (existingMsg) {
      existingMsg.remove();
    }

    const thymeleafMessages = document.querySelectorAll("p[th\\:if]");
    thymeleafMessages.forEach(msg => {
      msg.style.display = "none";
    });
  }
});

// 메시지 표시 함수
function showMessage(message, isError = false) {
  const errorMsg = document.getElementById("error-msg");
  if (errorMsg) {
    errorMsg.style.display = "none";
  }

  const linksSection = document.querySelector(".links-section");
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