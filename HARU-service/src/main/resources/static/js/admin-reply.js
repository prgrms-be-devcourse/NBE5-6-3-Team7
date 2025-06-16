document.addEventListener('DOMContentLoaded', () => {
  const replyContent = document.getElementById('replyContent');

  let dateRangeInput = document.getElementById('dateRangeInput');
  let period = '1month';
  let type = 'all';

  let replyMap = new Map();

  fetchDiaryAndReply(period, type);

  setupDropdown('period-button', 'period-dropdown', 'period-selected', (value) => {
    if (value == 'custom') {
      fp.clear();
      period = 'custom';
      dateRangeInput.value = '';

      return;
    }
    period = value;
    fetchDiaryAndReply(period, type);
  });

  setupDropdown('status-button', 'status-dropdown', 'status-selected', (value) => {
    type = value;
    fetchDiaryAndReply(period, type);
  });

  function setupDropdown(buttonId, dropdownId, spanId, onSelect) {
    const button = document.getElementById(buttonId);
    const dropdown = document.getElementById(dropdownId);
    const displaySpan = document.getElementById(spanId);

    button.addEventListener('click', (e) => {
      e.stopPropagation();
      const isVisible = dropdown.style.display === 'flex';
      document.querySelectorAll('.dropdown').forEach(d => d.style.display = 'none');
      dropdown.style.display = isVisible ? 'none' : 'flex';
    });

    dropdown.querySelectorAll('button').forEach(option => {
      option.addEventListener('click', () => {
        const displayText = option.innerText;
        const value = option.dataset.value;

        displaySpan.textContent = displayText;
        dropdown.style.display = 'none';

        if (dropdownId === 'period-dropdown') {
          dateRangeInput.value = '';
          if (value === 'custom') {
            dateRangeInput.style.display = 'inline-block';
          } else {
            dateRangeInput.style.display = 'none';
          }
        }

        onSelect(value);
      });
    });

    window.addEventListener('click', () => {
      dropdown.style.display = 'none';
    });
  }

  document.getElementById('selectAll')?.addEventListener('change', function () {
    const checkboxes = document.querySelectorAll('.item-checkbox');
    checkboxes.forEach(cb => cb.checked = this.checked);
  });

  function fetchDiaryAndReply(period, type) {
    const customDate = document.getElementById('dateRangeInput').value;
    const url = `/api/admin/reply?period=${period}&status=${type}&customDate=${customDate}`;

    fetch(url)
    .then(response => response.json())
    .then(data => {
      console.log('데이터:', data.ReplyInfos);
      renderDiaryAndReply(data.ReplyInfos)
    })
    .catch(err => {
      console.error('다이어리와 답변 불러오기 실패:', err);
      replyContent.innerHTML = '<p style="color:red;">데이터를 불러오지 못했습니다.</p>';
    });
  }

  function renderDiaryAndReply(items) {
    replyMap.clear();

    replyContent.innerHTML = '';
    if (!items || items.length === 0) {
      replyContent.innerHTML = '<p>데이터가 없습니다.</p>';
      return;
    }

    items.forEach((item, index) => {
      replyMap.set(item.diaryId, item);

      const div = document.createElement('div');
      div.className = 'reply-item';
      div.innerHTML = `
        <span><input type="checkbox" class="item-checkbox" data-id="${item.diaryId}" id="checkbox-${index}"></span>
        <span>${item.userId}</span>
        <span>${item.diaryId}</span>
        <span>${item.date}</span>
        <span>${item.diaryCreatedAt}</span>
        <span>${item.replyCreatedAt}</span>
        <span>${item.replyCreatedAt ? 'O' : 'X'}</span>
      `;
      replyContent.appendChild(div);
    })
  }

  window.fetchDiaryAndReply = fetchDiaryAndReply;
  window.renderDiaryAndReply = renderDiaryAndReply;

  const fp = flatpickr(dateRangeInput, {
    mode: "range",
    dateFormat: "Y-m-d",
    colorSpace: "green",
    onValueUpdate: function (selectedDates, dateStr, instance) {
      if (selectedDates.length === 2) {
        const format = (date) =>
            date.getFullYear() +
            "-" +
            String(date.getMonth() + 1).padStart(2, "0") +
            "-" +
            String(date.getDate()).padStart(2, "0");

        const formatted = format(selectedDates[0]) + " ~ " + format(selectedDates[1]);
        dateRangeInput.value = formatted;
      }
    },
    onClose: function () {
      fetchDiaryAndReply(period, type);
    }
  });
});