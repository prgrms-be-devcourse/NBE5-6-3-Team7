
  function logout(event) {
  event.preventDefault();

  const form = document.createElement('form');
  form.method = 'post';
  form.action = '/auth/logout';

  document.body.appendChild(form);
  form.submit();
}
