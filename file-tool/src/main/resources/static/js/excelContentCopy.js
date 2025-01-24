$(function () {

    // 表单提交事件
    $('#uploadForm').on('submit', function (event) {
        event.preventDefault(); // 阻止默认表单提交

        // 获取文件输入和 JSON 数据
        const sourceFile = $('#sourceFile')[0].files[0]; // 获取源文件
        const targetFile = $('#targetFile')[0].files[0]; // 获取目标文件
        const excelEntity = {
            sourceMatchColumn: parseInt($('#sourceMatchColumn').val(), 10),
            targetMatchColumn: parseInt($('#targetMatchColumn').val(), 10),
            sourceColumn: parseInt($('#sourceColumn').val(), 10),
            targetColumn: parseInt($('#targetColumn').val(), 10)
        };

        // 文件大小限制
        const MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
        if (sourceFile.size > MAX_FILE_SIZE || targetFile.size > MAX_FILE_SIZE) {
            Swal.fire({
                icon: 'error',
                title: '文件大小超过限制',
                text: '文件大小不能超过 10MB！',
            });
            return;
        }

        // 显示“正在处理中...”提示框
        Swal.fire({
            title: '正在处理中...',
            text: '请稍等，文件正在上传和处理。',
            allowOutsideClick: false,
            didOpen: () => {
                Swal.showLoading();
            }
        });

        // 创建 FormData 对象
        const formData = new FormData();
        formData.append('sourceFile', sourceFile); // 添加源文件
        formData.append('targetFile', targetFile); // 添加目标文件
        formData.append('excelEntity', JSON.stringify(excelEntity)); // 添加 JSON 数据

        // 发送请求
        axios({
            method: 'post',
            url: './logicFile/v1/excelContentCopy',
            data: formData, // 包含文件和其他参数
            responseType: 'blob', // 重要：指定响应类型为二进制流
        }).then(response => {
            const url = window.URL.createObjectURL(new Blob([response.data]));
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', 'modified_target.xlsx'); // 设置下载文件名
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);

            // 显示成功消息
            Swal.fire({
                icon: 'success',
                title: '成功',
                text: '文件下载成功！请到浏览器下载内容查看。',
            });
        }).catch(error => {
            console.error('文件下载失败', error);
            // 显示错误消息
            Swal.fire({
                icon: 'error',
                title: '提交失败',
                text: '提交失败，请重试。',
            });
        });
    });
});