function getCookie(key) {
    var result = null;
    var cookie = document.cookie.split(';');
    cookie.some(function (item) {
        item = item.replace(' ', '');

        var dic = item.split('=');

        if (key === dic[0]) {
            result = dic[1];
            return true;
        }
    });
    return result;
}

function myPage(){
    $.ajax({
        url: '/api/mypage',
        method: 'get',
        headers: {
            Authorization: 'Bearer ' + getCookie("access_token"),
            'Content-Type': 'application/json',
        },
        success: function (data, status, xhr) {
            console.log("success")
            console.log(data)
        },
        error: function (data, status, err) {
        }
    })
}

function request(success, fail) {
    fetch(url, {
        method: post,
        headers: {
            Authorization: 'Bearer ' + getCookie("access_token"),
            'Content-Type': 'application/json',
        },
        body: body,
    }).then(response => {
        if (response.status === 200 || response.status === 201) {
            
        }
    });
}
