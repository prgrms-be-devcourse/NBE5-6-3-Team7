document.querySelector(".check-other").addEventListener("click", function () {
  const form = document.getElementById("aiForm");
  const formData = new FormData(form);

  fetch(form.action, {
    method: "POST",
    body: formData
  })
  .then(response => {
    if (response.ok) {
      // 폼 전송 성공 후 이동
      location.href = "/app/settings/ai";
    } else {
      alert("폼 전송 실패");
    }
  })
  .catch(error => {
    console.error("오류 발생:", error);
    alert("서버 오류");
  });
});