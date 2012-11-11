var moveFooter = function(e) {
        var dH = $(document).height();
        var wH = $(window).height();
        if (dH <= wH) {
            $('.footer').css( 'position', 'fixed');
            $('.footer').css( 'bottom', 0);
            $('.footer').css( 'width', '96%');
            $('.footer').css( 'height', '30px');
        } else {
            $('.footer').css( 'position', 'relative');
        }
    };

$(document).ready(function(){
    $(".iframe").colorbox({iframe:true, width:"80%", height:"80%"});
    $(window).resize(moveFooter);
});