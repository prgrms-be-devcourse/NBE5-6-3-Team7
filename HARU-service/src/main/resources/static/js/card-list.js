async function loadRecentDiaries() {
  const response = await fetch(`/api/diary/cards`);
  const data = await response.json();
  const diaries = data.diaryCards;

  const container = document.getElementById("diary-card-container");
  const template = document.getElementById("diary-card-template");

  diaries.slice(0, 14).forEach((diary) => {
    const clone = template.content.cloneNode(true);
    const card = clone.querySelector(".diary-card");

    // 배경/글자 설정
    if (diary.imagePath) {
      card.classList.add("with-image");

      // 오버레이용 div
      const overlay = document.createElement("div");
      overlay.className = "image-overlay";
      overlay.style.backgroundImage = `url('${diary.imagePath}')`;
      card.appendChild(overlay);
    } else {
      card.classList.add("no-image");
    }

    // 이모지
    const icon = clone.querySelector(".emotion-icon");
    const fileName = emotionImageMap[diary.emotion?.toUpperCase()] || emotionImageMap.DEFAULT;
    icon.src = `/images/emotion/weather/${fileName}`;

    // 날짜 (ex. 2025-05-08 → 05.08)
    const dateElem = clone.querySelector(".diary-date");
    const [year, month, day] = diary.date.split("-");
    dateElem.textContent = `${month}.${day}`;

    // 일기 내용
    const right = clone.querySelector(".diary-right");
    right.textContent = diary.content || "(내용 없음)";

    // 상세 페이지로 이동 이벤트 추가
    card.addEventListener("click", () => {
      const dateParam = diary.date;
      window.location.href = `/diary/record?date=${dateParam}`;
    });

    container.appendChild(clone);
  });
}
document.addEventListener("DOMContentLoaded", () => {
  loadRecentDiaries();
});
