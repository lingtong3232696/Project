$(document).ready(function () {
    // 模拟用户数据
    const validUser = {
        username: 'admin',
        password: '123456'
    };

    // 监听表单提交事件
    $('#loginForm').on('submit', function (event) {
        event.preventDefault(); // 阻止表单默认提交行为

        // 获取输入的用户名和密码
        const username = $('#username').val();
        const password = $('#password').val();

        // 验证用户名和密码
        if (username === validUser.username && password === validUser.password) {
            // 登录成功，存储登录状态和时间
            const loginTime = new Date().getTime(); // 获取当前时间戳
            localStorage.setItem('isLoggedIn', 'true');
            localStorage.setItem('loginTime', loginTime);

            // 跳转到受保护的页面
            Swal.fire({
                title: '登录成功！',
                text: '点击确定后跳转到首页。',
                icon: 'success',
                confirmButtonText: '确定'
            }).then((result) => {
                if (result.isConfirmed) {
                    // 跳转到受保护的页面
                    window.location.href = '../index.html';
                }
            });
        } else {
            // 登录失败，显示错误消息
            $('#errorMessage').text('用户名或密码错误');
        }
    });
});