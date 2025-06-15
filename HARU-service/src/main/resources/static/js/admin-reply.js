document.addEventListener('DOMContentLoaded', () => {
  setupDropdown('period-button', 'period-dropdown', 'period-selected');
  setupDropdown('status-button', 'status-dropdown', 'status-selected');

  function setupDropdown(buttonId, dropdownId, spanId) {
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
        displaySpan.textContent = option.dataset.value;
        dropdown.style.display = 'none';
      });
    });

    window.addEventListener('click', () => {
      dropdown.style.display = 'none';
    });
  }
});