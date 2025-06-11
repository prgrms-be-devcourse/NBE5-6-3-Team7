// admin-ai-write.js
document.addEventListener('DOMContentLoaded', async () => {
  const submitBtn = document.getElementById('submitBtn');
  const imageChangeBtn = document.getElementById('imageChange');

  const imageInput = document.getElementById('imageInput');
  const thumbnail = document.getElementById('thumbnail');
  const nameInput = document.getElementById('name-input');
  const mbtiInput = document.getElementById('mbti-input');
  const infoInput = document.getElementById('info-input');
  const promptInput = document.getElementById('prompt-input');
  const editingAiId = localStorage.getItem('editingAiId');

  const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
  const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

  if (!submitBtn) return;

  if (editingAiId) {
    try {
      const res = await fetch(`/api/admin/ai/${editingAiId}`);
      if (!res.ok) throw new Error('AI 데이터 불러오기 실패');

      const ai = await res.json();

      nameInput.value = ai.name || '';
      mbtiInput.value = ai.mbti || '';
      infoInput.value = ai.info || '';
      promptInput.value = ai.prompt || '';

      if (ai.images && ai.images.length > 0) {
        const thumbImage = ai.images.find(img => img.type === 'THUMBNAIL') || ai.images[0];

        thumbnail.src = thumbImage.renamedName;
        thumbnail.style.display = 'block';
      }
    } catch (err) {
      console.error('AI 데이터 로드 오류:', err);
      alert('AI 캐릭터 정보를 불러오지 못했습니다.');
    }
  }

  imageChangeBtn.addEventListener('click', () => {
    imageInput.click();
  });

  imageInput.addEventListener('change', (e) => {
    const file = e.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = function (event) {
        thumbnail.src = event.target.result;
        thumbnail.style.display = 'block';
      };
      reader.readAsDataURL(file);
    }
  });

  submitBtn.addEventListener('click', async () => {
    const name = nameInput.value.trim();
    const mbti = mbtiInput.value.trim();
    const info = infoInput.value.trim();
    const prompt = promptInput.value.trim();
    const imageFile = imageInput.files[0];

    if (!name) return alert('이름을 입력하세요');
    if (!mbti) return alert('MBTI를 입력하세요');
    if (!info) return alert('소개글을 입력하세요');
    if (!prompt) return alert('프롬프트를 입력하세요');
    if (!thumbnail.src) return alert('사진을 업로드하세요');

    const formData = new FormData();
    if(editingAiId) formData.append('id', editingAiId);
    formData.append('name', name);
    formData.append('mbti', mbti);
    formData.append('info', info);
    formData.append('prompt', prompt);
    if(imageFile) formData.append('images', imageFile);

    try {
      const url = editingAiId ? '/api/admin/ai/modify' : '/api/admin/ai';
      const method = editingAiId ? 'PATCH' : 'POST';

      const res = await fetch(url, {
        method,
        headers: {
          [csrfHeader]: csrfToken
        },
        body: formData
      });

      if (!res.ok) throw new Error(editingAiId ? '수정 실패' : '등록 실패');

      alert(editingAiId ? 'AI 캐릭터가 수정되었습니다.' : 'AI 캐릭터가 등록되었습니다.');
      localStorage.removeItem('editingAiId');
      window.location.href = '/admin/ai';
    } catch (err) {
      console.error(err);
      alert('오류 발생. 다시 시도해주세요.');
    }
  });
});
