const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

function postLeave() {
  fetch('/member/leave', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded', // 또는 'application/json'
      [csrfHeader]: csrfToken
    },
    body: '' // 필요한 데이터 있으면 여기에 작성
  }).then(response => {
    if (response.ok) {
      window.location.href = '/member/leave-success'; // 성공 시 이동할 페이지
    }
  });
}
