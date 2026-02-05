(function (){
    function getCookie(name){

        const v = document.cookie.match('(^|;) ?' + name + '=([^;]*)(;|$)');
        return v ? decodeURIComponent(v[2]) : null;
    }

    document.addEventListener("DOMContentLoaded", () => {
        const nombre = 'visitaVideojuego';
    })
}) ();
