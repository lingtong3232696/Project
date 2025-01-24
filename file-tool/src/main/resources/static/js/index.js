// 切换二级菜单的显示/隐藏
function toggleSubMenu(id) {
    const subMenu = document.getElementById(id);
    if (subMenu.style.display === "block") {
        subMenu.style.display = "none";
    } else {
        subMenu.style.display = "block";
    }
}

// 加载右侧内容并高亮当前选中的栏目
function loadContent(url) {
    const iframe = document.getElementById("content-frame");
    iframe.src = url;

    // 移除所有栏目的高亮状态
    const items = document.querySelectorAll('.list-group-item');
    items.forEach(item => item.classList.remove('active'));

    // 高亮当前选中的栏目
    event.target.classList.add('active');
}