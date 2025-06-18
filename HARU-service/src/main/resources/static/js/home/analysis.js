document.addEventListener("DOMContentLoaded", function () {
  const contentEl = document.querySelector(".analysis-card .card-content");
  const cardEl = document.querySelector(".analysis-card");

  const today = new Date();
  const yyyy = today.getFullYear();
  const mm = String(today.getMonth() + 1).padStart(2, '0');
  const dd = String(today.getDate()).padStart(2, '0');
  const formattedDate = `${yyyy}-${mm}-${dd}`;
  const username = document.getElementById('username').value;

  // 로딩 메시지 + 커서 span 추가
  contentEl.innerHTML = `
      <span id="loading-text">최근 감정을 분석중입니다...</span>
      <span class="blinking-cursor">|</span>
    `;

  fetch(`/api/ai/stats/emotion?date=${formattedDate}&username=${encodeURIComponent(username)}`)
  .then(response => {
    if (!response.ok) {
      throw new Error("서버 오류");
    }
    return response.text();
  })
  .then(data => {
    // 개행 유지
    contentEl.innerText = data;

    // 카드 반짝임 효과
    cardEl.classList.add("flash-once");
    setTimeout(() => {
      cardEl.classList.remove("flash-once");
    }, 1000);
  })
  .catch(error => {
    contentEl.textContent = "감정 분석을 가져오는 데 실패했습니다.";
    console.error("감정 분석 요청 실패:", error);
  });
});