const currentPath = window.location.pathname;

document.querySelectorAll(".sidebar .menu a").forEach(link => {
  const linkPath = link.getAttribute("href");
  // 설정 페이지는 하위 경로도 모두 active로 처리
  if (linkPath === "/app/settings" && currentPath.startsWith("/app/settings")) {
    link.classList.add("active");

    // 일반적인 정확한 경로 매칭
  } else if (currentPath === linkPath) {
    link.classList.add("active");
  }
});
