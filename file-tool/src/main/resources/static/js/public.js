$(document).ready(function () {
    // 检查用户是否已登录
    const isLoggedIn = localStorage.getItem('isLoggedIn');
    const loginTime = localStorage.getItem('loginTime');
    const currentTime = new Date().getTime();
    const sessionTimeout = 30 * 60 * 1000; // 30分钟（以毫秒为单位）

    if (!isLoggedIn || isLoggedIn !== 'true' || !loginTime || (currentTime - loginTime > sessionTimeout) || isLoggedIn === null) {
        // 如果未登录或登录已过期，跳转到登录页面
        localStorage.removeItem('isLoggedIn');
        localStorage.removeItem('loginTime');

        // 如果未登录，跳转到登录页面
        Swal.fire({
            title: '未登录或登录已过期！',
            text: '点击确定后跳转登录页面。',
            icon: 'error',
            confirmButtonText: '确定'
        }).then((result) => {
            if (result.isConfirmed) {
                // 跳转到受保护的页面
                window.location.href = window.location.origin + '/fileTool/login.html';
            }
        });
    }


    // 退出登录
    /*$('#logout').on('click', function () {
        // 清除登录状态
        localStorage.removeItem('isLoggedIn');
        // 跳转到登录页面
        window.location.href = './login.html';
    });*/
});