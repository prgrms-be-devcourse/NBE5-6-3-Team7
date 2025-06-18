class SidebarManager {
  constructor() {
    this.currentPath = window.location.pathname;
    this.sidebar = document.querySelector('.sidebar');
    this.menuLinks = document.querySelectorAll('.sidebar .menu a');
    this.isExpanded = false;

    this.init();
  }

  init() {
    this.setActiveMenu();
    this.bindEvents();
    this.initTooltips();
    this.initKeyboardNavigation();
  }

  /**
   * 현재 경로에 따라 활성 메뉴 설정
   */
  setActiveMenu() {
    // 모든 활성 상태 초기화
    this.menuLinks.forEach(link => {
      link.classList.remove('active');
      const menuItem = link.closest('.menu-item');
      if (menuItem) {
        menuItem.classList.remove('active');
      }
    });

    // 경로 매칭 우선순위
    const pathMatchers = [
      // 1. 정확한 경로 매칭
      { path: this.currentPath, exact: true },
      // 2. 하위 경로 매칭 (설정 페이지 등)
      { path: '/app/settings', startsWith: true },
      { path: '/admin', startsWith: true },
      // 3. 홈 경로 특별 처리
      { path: '/', exact: true, fallback: true }
    ];

    let matchFound = false;

    for (const matcher of pathMatchers) {
      this.menuLinks.forEach(link => {
        const linkPath = link.getAttribute('href');

        let isMatch = false;

        if (matcher.exact && linkPath === matcher.path) {
          isMatch = true;
        } else if (matcher.startsWith && this.currentPath.startsWith(matcher.path) && linkPath.startsWith(matcher.path)) {
          isMatch = true;
        }

        if (isMatch && !matchFound) {
          this.setActiveLinkState(link);
          matchFound = true;
        }
      });

      if (matchFound) break;
    }

    // 매칭되는 것이 없으면 홈을 기본으로 설정
    if (!matchFound) {
      const homeLink = document.querySelector('.sidebar .menu a[href="/"], .sidebar .menu a[href="/admin"]');
      if (homeLink) {
        this.setActiveLinkState(homeLink);
      }
    }
  }

  /**
   * 링크를 활성 상태로 설정
   */
  setActiveLinkState(link) {
    link.classList.add('active');
    const menuItem = link.closest('.menu-item');
    if (menuItem) {
      menuItem.classList.add('active');
    }

    // 부모 메뉴가 있는 경우 부모도 활성화
    const parentMenuItem = menuItem?.closest('.menu-group');
    if (parentMenuItem) {
      parentMenuItem.classList.add('active');
    }
  }

  /**
   * 이벤트 바인딩
   */
  bindEvents() {
    // 사이드바 호버 이벤트
    this.sidebar.addEventListener('mouseenter', () => {
      this.isExpanded = true;
      this.sidebar.classList.add('expanded');
      this.hideTooltips();
    });

    this.sidebar.addEventListener('mouseleave', () => {
      this.isExpanded = false;
      this.sidebar.classList.remove('expanded');
      this.showTooltips();
    });

    // 메뉴 클릭 이벤트
    this.menuLinks.forEach(link => {
      link.addEventListener('click', (e) => {
        // 외부 링크나 특별한 처리가 필요한 경우
        const href = link.getAttribute('href');

        if (href === '#' || href.startsWith('javascript:')) {
          e.preventDefault();
          return;
        }

        // 로그아웃 링크 특별 처리
        if (href.includes('logout') || link.onclick) {
          return; // 기존 onclick 이벤트 실행
        }

        // 일반 링크의 경우 활성 상태만 변경 (실제 페이지 이동은 브라우저가 처리)
        this.setActiveLinkState(link);
      });

      // 포커스 이벤트 (키보드 네비게이션용)
      link.addEventListener('focus', () => {
        this.showTooltipForElement(link);
      });

      link.addEventListener('blur', () => {
        this.hideTooltipForElement(link);
      });
    });

    // 윈도우 리사이즈 이벤트
    window.addEventListener('resize', () => {
      this.handleResize();
    });
  }

  /**
   * 툴팁 초기화
   */
  initTooltips() {
    this.menuLinks.forEach(link => {
      const menuItem = link.closest('.menu-item');
      if (!menuItem) return;

      let tooltip = menuItem.querySelector('.tooltip');
      if (!tooltip) {
        tooltip = document.createElement('div');
        tooltip.className = 'tooltip';
        tooltip.textContent = link.querySelector('span')?.textContent || '';
        tooltip.setAttribute('role', 'tooltip');
        menuItem.appendChild(tooltip);
      }

      // 마우스 이벤트
      menuItem.addEventListener('mouseenter', () => {
        if (!this.isExpanded) {
          this.showTooltipForElement(link);
        }
      });

      menuItem.addEventListener('mouseleave', () => {
        this.hideTooltipForElement(link);
      });
    });
  }

  /**
   * 특정 요소의 툴팁 표시
   */
  showTooltipForElement(link) {
    const menuItem = link.closest('.menu-item');
    const tooltip = menuItem?.querySelector('.tooltip');
    if (tooltip && !this.isExpanded) {
      tooltip.style.opacity = '1';
      tooltip.style.visibility = 'visible';
    }
  }

  /**
   * 특정 요소의 툴팁 숨김
   */
  hideTooltipForElement(link) {
    const menuItem = link.closest('.menu-item');
    const tooltip = menuItem?.querySelector('.tooltip');
    if (tooltip) {
      tooltip.style.opacity = '0';
      tooltip.style.visibility = 'hidden';
    }
  }

  /**
   * 모든 툴팁 표시
   */
  showTooltips() {
    if (this.isExpanded) return;

    document.querySelectorAll('.sidebar .tooltip').forEach(tooltip => {
      tooltip.style.display = 'block';
    });
  }

  /**
   * 모든 툴팁 숨김
   */
  hideTooltips() {
    document.querySelectorAll('.sidebar .tooltip').forEach(tooltip => {
      tooltip.style.opacity = '0';
      tooltip.style.visibility = 'hidden';
    });
  }

  /**
   * 키보드 네비게이션 초기화
   */
  initKeyboardNavigation() {
    this.menuLinks.forEach((link, index) => {
      link.addEventListener('keydown', (e) => {
        switch (e.key) {
          case 'ArrowDown':
            e.preventDefault();
            this.focusNextMenuItem(index);
            break;
          case 'ArrowUp':
            e.preventDefault();
            this.focusPreviousMenuItem(index);
            break;
          case 'Enter':
          case ' ':
            e.preventDefault();
            link.click();
            break;
          case 'Escape':
            link.blur();
            break;
        }
      });
    });
  }

  /**
   * 다음 메뉴 아이템으로 포커스 이동
   */
  focusNextMenuItem(currentIndex) {
    const nextIndex = (currentIndex + 1) % this.menuLinks.length;
    this.menuLinks[nextIndex].focus();
  }

  /**
   * 이전 메뉴 아이템으로 포커스 이동
   */
  focusPreviousMenuItem(currentIndex) {
    const prevIndex = currentIndex === 0 ? this.menuLinks.length - 1 : currentIndex - 1;
    this.menuLinks[prevIndex].focus();
  }

  /**
   * 반응형 처리
   */
  handleResize() {
    const isMobile = window.innerWidth <= 768;

    if (isMobile) {
      this.sidebar.classList.add('mobile');
    } else {
      this.sidebar.classList.remove('mobile');
    }
  }

  /**
   * 특정 메뉴를 프로그래밍적으로 활성화
   */
  activateMenu(path) {
    const targetLink = Array.from(this.menuLinks).find(link =>
        link.getAttribute('href') === path
    );

    if (targetLink) {
      this.setActiveLinkState(targetLink);
    }
  }

  /**
   * 현재 활성 메뉴 가져오기
   */
  getActiveMenu() {
    return document.querySelector('.sidebar .menu a.active');
  }

  /**
   * 사이드바 상태 초기화
   */
  reset() {
    this.menuLinks.forEach(link => {
      link.classList.remove('active');
      const menuItem = link.closest('.menu-item');
      if (menuItem) {
        menuItem.classList.remove('active');
      }
    });
  }
}

// DOM이 로드되면 사이드바 매니저 초기화
document.addEventListener('DOMContentLoaded', () => {
  window.sidebarManager = new SidebarManager();
});

// 페이지 변경 감지 (SPA인 경우)
window.addEventListener('popstate', () => {
  if (window.sidebarManager) {
    window.sidebarManager.currentPath = window.location.pathname;
    window.sidebarManager.setActiveMenu();
  }
});

// 유틸리티 함수들
window.SidebarUtils = {
  /**
   * 외부에서 메뉴 활성화
   */
  activateMenu: (path) => {
    if (window.sidebarManager) {
      window.sidebarManager.activateMenu(path);
    }
  },

  /**
   * 현재 활성 메뉴 경로 가져오기
   */
  getActivePath: () => {
    const activeMenu = window.sidebarManager?.getActiveMenu();
    return activeMenu ? activeMenu.getAttribute('href') : null;
  },

  /**
   * 사이드바 상태 리셋
   */
  reset: () => {
    if (window.sidebarManager) {
      window.sidebarManager.reset();
    }
  }
};