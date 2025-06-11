document.addEventListener("DOMContentLoaded", function () {
  const btnFindId = document.getElementById("find-id-btn");
  const btnFindPw = document.getElementById("find-pw-btn");

  const formFindId = document.getElementById("form-find-id");
  const formFindPw = document.getElementById("form-find-pw");

  btnFindId.addEventListener("click", () => {
    formFindId.style.display = "flex";
    formFindPw.style.display = "none";

    btnFindId.classList.remove("disabled");
    btnFindId.classList.add("active");
    btnFindPw.classList.remove("active");
    btnFindPw.classList.add("disabled");

    const errorMsg = document.getElementById("error-msg");
    if (errorMsg) {
      errorMsg.remove(); // 또는 errorMsg.style.display = "none";
    }

  });

  btnFindPw.addEventListener("click", () => {
    formFindPw.style.display = "flex";
    formFindId.style.display = "none";

    btnFindPw.classList.remove("disabled");
    btnFindPw.classList.add("active");
    btnFindId.classList.remove("active");
    btnFindId.classList.add("disabled");
    const errorMsg = document.getElementById("error-msg");
    if (errorMsg) {
      errorMsg.remove(); // 또는 errorMsg.style.display = "none";
    }
  });
});
function handleFindPwClick(button) {
  if (button.disabled) return;

  // 폼 전환 등 처리
  document.getElementById("form-find-pw").style.display = "block";
  document.getElementById("form-find-id").style.display = "none";

  // 버튼 상태 조정
  button.classList.add("active");
  document.getElementById("find-id-btn").classList.remove("active");
}
