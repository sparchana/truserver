/**
 * Created by hawk on 3/10/16.
 */
///Scroll
$(document).scroll(function(){
    if ($(this).scrollTop() >250) {
        $('nav').css({"background": "#039BE5"});
    }
    else{
        $('nav').css({"background": "transparent"});
    }
});
///modal
$(document).ready(function(){
    $(".button-collapse").sideNav();
    $('.parallax').parallax();
    $('.modal-trigger').leanModal({dismissible: true});
    $('#locationOption').autocomplete({
        data: {
            "Address1": null,
            "Address2": null,
        }});
    $('#jobRoleOption').autocomplete({
        data: {
            "Job Role1": null,
            "Job Role2": null,
        }})
    $('select').material_select();
});