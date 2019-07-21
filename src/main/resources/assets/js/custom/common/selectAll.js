$(document).ready(function () {

    function check(el,cl){
        $(el).each(function(){
            $(this).parent('i').removeClass(cl);
            var checked = $(this).prop('checked');
            if(checked){
                $(this).parent('i').addClass(cl);
            }
        })
    }

    function aa(op) {
        var trnum = $(op).find('tr').length - 1;
        var cnum = $(op).find('input[type=checkbox]:checked').length;
        if(trnum == cnum){
            $("#selectAll").prop("checked", true).parent('i').addClass('checkbox_bg_check');
        }else{
            $("#selectAll").prop("checked", false).parent('i').removeClass('checkbox_bg_check');
        }
    }

    $("tbody").on("click", 'input[type="checkbox"]', function () {
        check('input[type="checkbox"]','checkbox_bg_check');
        aa("tbody");
    })

    $("tbody").on("click", 'input[type="radio"]', function () {
        check('input[type="radio"]','radio_bg_check');
    })

    $(document).on("click", 'input[type="checkbox"]', function () {
        check('input[type="checkbox"]','checkbox_bg_check');
    })

    $(document).on("click", 'input[type="radio"]', function () {
        check('input[type="radio"]','radio_bg_check');
    })

})




