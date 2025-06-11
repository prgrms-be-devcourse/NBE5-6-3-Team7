
  function logout(event) {
  event.preventDefault();

  const form = document.createElement('form');
  form.method = 'post';
  form.action = '/auth/logout';

  // CSRF 토큰 삽입
  const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
  const csrfParam = document.querySelector('meta[name="_csrf_parameter"]')?.getAttribute('content') || '_csrf';

  if (csrfToken && csrfParam) {
    const input = document.createElement('input');
    input.type = 'hidden';
    input.name = csrfParam;
    input.value = csrfToken;
    form.appendChild(input);
  } else {
    console.warn('CSRF 메타 태그가 누락되어 있습니다.');
  }

  document.body.appendChild(form);
  form.submit();
}
